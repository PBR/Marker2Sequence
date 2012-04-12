/*
 *  Copyright 2011, 2012 Plant Breeding, Wageningen UR.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package nl.wur.plantbreeding.www.annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import nl.wur.plantbreeding.datatypes.Annotation;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nl.wur.plantbreeding.logic.marker2seq.AnnotationRetriever;
import nl.wur.plantbreeding.www.marker2seq.GoDistribution;
import nl.wur.plantbreeding.www.util.EmailExceptions;
import nl.wur.plantbreeding.logic.marker2seq.Marker2SeqUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.xml.sax.SAXException;

/**
 * This class retrieves and displays in a table and a pie chart the annotation
 * for a given gene using the web-service from Cologne.
 * "GetSifterPredictedFunctionTermsByProtein"
 *
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class AnnotationAction extends org.apache.struts.action.Action {

    /**
     * Returned in case all goes right.
     */
    private static final String SUCCESS = "success";
    /**
     * When something unexpected goes wrong.
     */
    private static final String ERROR = "error";
    /**
     * When the web-service returns an error.
     */
    private static final String ERRORMOBY = "errormoby";
    /**
     * Logger.
     */
    private static final Logger LOG =
            Logger.getLogger(AnnotationAction.class.getName());

    /**
     * This is the action called from the Struts framework. - calls the
     * web-service (to retrieve the list of annotation) - generates the picture
     * - output the picture to a file - set the session Attributes
     *
     * @param mapping The ActionMapping used to select this instance.
     * @param form The optional ActionForm bean for this request.
     * @param request The HTTP Request we are processing.
     * @param response The HTTP Response we are processing.
     * @return mapping.findForward to error / detailed overview jsp.
     */
    @Override
    public final ActionForward execute(final ActionMapping mapping,
            final ActionForm form,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        final ActionMessages errors = new ActionMessages();
        final ServletContext context = getServlet().getServletContext();

        LOG.info(" *** Start ***");
        final HttpSession session = request.getSession();
        request.setAttribute("annotation", null); // annotation table
        request.setAttribute("annotationpie", null);

        // Retrieve and set some configuration element
        final boolean debug = Boolean.parseBoolean(
                context.getInitParameter("BioMobyDebug"));
        LOG.log(Level.INFO, "Debug: {0}", debug);
        final boolean ssl = Boolean.parseBoolean(
                context.getInitParameter("BioMobySSL"));

        final String geneid = request.getParameter("geneid");
//        geneid = "SL1.00sc04161_16.1.1";

        final String theme = context.getInitParameter("theme");
        final String species = context.getInitParameter("m2sSpecies");
        final boolean biomoby = ( context.getInitParameter("BioMoby").equals("True") );
        final String graph = Marker2SeqUtils.GetBaseGraphFromTheme(species, theme);
        final AnnotationRetriever annotr = new AnnotationRetriever(graph);

        // Retrieve the sparql endpoint where are stored our graphs
        // from the web.xml
        final String endpoint = context.getInitParameter("sparqlserver");

        if (endpoint == null) {
            LOG.log(Level.INFO, "Could not retrieve the sparql server address "
                    + "(at key: sparqlserver), Going to the "
                    + "default value: http://localhost:8890/sparql/.");

        }
        LOG.log(Level.INFO, "Using endpoint {0}", endpoint);

        if (endpoint != null && !endpoint.isEmpty()) {
            annotr.setEndpoint(endpoint);
        }

        // Calls the first web-service to retrieve the markers in the map
        ArrayList<HashMap<String, String>> gotermlist =
                new ArrayList<HashMap<String, String>>();
        // Query Cologne to retrive the GO term assigned by AFAWE if we work on
        // tomato
        if (species.equalsIgnoreCase("tomato")) {
            List<Annotation> gotermlst = new ArrayList<Annotation>();
            try {
                if (biomoby) {
                    gotermlst = annotr.getGoTermFromSifter(geneid, ssl, debug);
                }
            }
            catch (SAXException ex) {
                LOG.log(Level.SEVERE, "ERROR web-service Cologne: {0}",
                        ex.getMessage());
                errors.add("ws1", new ActionMessage(
                        "m2s.error.ws", ex.getMessage()));
                saveErrors(request, errors);
//            return mapping.findForward(ERRORMOBY);
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, "ERROR : {0}", ex.getMessage());
                LOG.severe(ex.getLocalizedMessage());
                EmailExceptions.sendExceptionEmail(context, request, ex);
                return mapping.findForward(ERROR);
            }

            LOG.log(Level.INFO, "{0} go terms from afawe", gotermlst.size());
            gotermlist = this.annotationListToHash(gotermlst);
        }

        // Add the GO terms present in sparql to the list.
        gotermlist = annotr.getGoTermFromSparql(geneid, gotermlist);
        LOG.log(Level.INFO, "{0} go terms in total", gotermlist.size());

        if (!gotermlist.isEmpty()) {
            request.setAttribute("annotation", gotermlist); // annotation table
        }
        // Post-processing
        if (!gotermlist.isEmpty()) {
            String[] out = null;
            try {
                List<String> gos = this.generateGoTermList(gotermlist);
                boolean legend = true;
                if (gos.size() > 10) {
                    legend = false;
                }
                out = GoDistribution.generateDistribution(
                        gos,
                        "GO name space distribution", "#", session,
                        legend, legend, true, 10);
                if (out != null) {
                    //store the image:
                    request.setAttribute("annotationpie", out[0]);
                    request.setAttribute("chartmap", out[1]);
                }
            }
            catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }


        // Query Munich to retrive the gene information from PlantDB
//        Annotation annot = new Annotation();
//        try {
//            annot = annotr.getAnnotationFromPlantDBMunich(
//                    geneid, ssl, debug).get(0);
//        }
//        catch (Exception ex) {
//            LOG.log(Level.INFO, "No info could be retrieved from Munich,"
//                    + " using virtuoso, error : {0}", ex.getMessage());
//            annot = annotr.getGeneAnnotation(geneid);
//        }
        Annotation annot = annotr.getGeneAnnotation(geneid);

        if (annot != null) {
            request.setAttribute("annotation-mips", annot); // annotation table
        }

        List<HashMap<String, String>> proteins =
                annotr.getProteinInfoForGeneId(geneid);
        LOG.log(Level.INFO, "{0} proteins found linked to this gene",
                proteins.size());
        if (!proteins.isEmpty()) {
            request.setAttribute("proteins", proteins); // proteins information
        }

        List<HashMap<String, String>> literature =
                annotr.getLiteratureFromSparql(geneid);
        LOG.log(Level.INFO, "{0} articles found linked to this gene",
                literature.size());
        // literature information
        if (!literature.isEmpty()) {
            request.setAttribute("literature", literature); // literature info
        }

        List<HashMap<String, String>> pathways =
                annotr.getPathwayFromSparql(geneid);
        LOG.log(Level.INFO, "{0} pathways found linked to this gene",
                pathways.size());
        // literature information
        if (!pathways.isEmpty()) {
            request.setAttribute("pathways", pathways); // pathway information
        }

        request.setAttribute("geneid", geneid);
        LOG.info(" *** Ends ***");
        return mapping.findForward(SUCCESS);

    }

    /**
     * For a given list of Annotation, returns a list containing the name of the
     * annotation.
     *
     * @param gotermlist List of Annotation retrieved from the web-service
     * @return a list a String containing the GO name
     */
    private List<String> generateGoTermList(
            final List<HashMap<String, String>> gotermlist) {
        final ArrayList<String> names = new ArrayList<String>();
        for (HashMap<String, String> go : gotermlist) {
            String name = go.get("name");
            if (!names.contains(name)) {
                names.add(go.get("name"));
            }
        }
        return names;
    }

    /**
     * Transforms a list of Annotation to an arraylist of hashmap. Adds to them
     * a source keyword : "afawe". This is done for two reason: - change the
     * variable name to something which make sense since we use the annotation
     * object which is not totally suited for this type of information - Enable
     * later addition of new GO term from a different sources (here the ITAG
     * sparql endpoint).
     *
     * @param gotermlst the list of Annotation object to convert
     * @return the GO information in a list of HashMap
     */
    private ArrayList<HashMap<String, String>> annotationListToHash(
            final List<Annotation> gotermlst) {
        ArrayList<HashMap<String, String>> output =
                new ArrayList<HashMap<String, String>>();
        HashMap<String, String> row;
        for (Annotation go : gotermlst) {
            row = new HashMap<String, String>();
            row.put("goid", go.getScafoldname());
            row.put("name", go.getName());
            row.put("process", go.getType());
            row.put("definition", go.getMethod());
            row.put("source", "afawe");
            output.add(row);
        }
        return output;
    }
}
