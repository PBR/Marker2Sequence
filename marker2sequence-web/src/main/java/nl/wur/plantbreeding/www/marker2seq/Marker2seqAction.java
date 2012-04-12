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

/**
 * 0.2 Added basic handling of marker lists (From QTL_IM)
 */
package nl.wur.plantbreeding.www.marker2seq;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import nl.wur.plantbreeding.logic.marker.Marker2seqGraphics;
import nl.wur.plantbreeding.datatypes.MarkerSequence;
import nl.wur.plantbreeding.datatypes.Markerws;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nl.wur.plantbreeding.datatypes.Annotation;
import nl.wur.plantbreeding.datatypes.GeneticMarkers;
import nl.wur.plantbreeding.exceptions.MarkerNotMappedException;
import nl.wur.plantbreeding.exceptions.SeveralMappedPositionException;
import nl.wur.plantbreeding.logic.marker2seq.AnnotationSearch;
import nl.wur.plantbreeding.logic.marker2seq.Marker2seq;
import nl.wur.plantbreeding.logic.swtools.QueryRdf;
import nl.wur.plantbreeding.logic.util.FileName;
import nl.wur.plantbreeding.logic.marker2seq.Marker2SeqUtils;
import nl.wur.plantbreeding.www.util.EmailExceptions;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.biomoby.shared.MobyException;
import org.xml.sax.SAXException;

/**
 * This is the action class of the marker2seq tool.
 * This class checks if the two markers are valid input, call the two
 * web-service call the creation of the picture and returns all the information
 * to the jsp.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class Marker2seqAction extends org.apache.struts.action.Action {

    /* forward name="success" path="" */
    /** Value return to the mapping to indicate to proceed to the page. */
    private String success = "success";
    /** Value return to the mapping to indicate an error (generic case). */
    private static final String ERROR = "error";
    /** Value return to the mapping to indicate a biomoby error. */
    private static final String ERRORMOBY = "errormoby";
    /** Width of the picture, retrieved from Marker2seqGraphics. */
    private static final Integer PICTUREWIDTH = Marker2seqGraphics.PICTUREWIDTH;
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(
            Marker2seqAction.class.getName());
    /** URI used for reference. */
    private static final String URI = "http://pbr.wur.nl/";
    /** Queries run against virtuoso. */
    private final QueryRdf query = new QueryRdf(URI);

    /**
     * This is the action called from the Struts framework.
     *  - calls the two web-services (to retrieve the list of markers then
     * the annotation).
     *  - generates the picture
     *  - output the picture to a file
     *  - set the session Attributes
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

        final long start = System.currentTimeMillis();

        final ActionMessages errors = new ActionMessages();
        final ServletContext context = getServlet().getServletContext();

        LOG.info(" *** Start ***");
        final HttpSession session = request.getSession();
        session.setAttribute("map", null); // genetic map table
        session.setAttribute("annotation", null); // annotation table
        session.setAttribute("picturemap", null);
        session.setAttribute("marker2filename", null);
        session.setAttribute("resultannotation", null);

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
            this.query.setService(endpoint);
        }

        final String theme = context.getInitParameter("theme");
        final String species = context.getInitParameter("m2sSpecies");
        final String graph = Marker2SeqUtils.GetBaseGraphFromTheme(species, theme);
        this.query.setBasegraph(graph);

        request.setAttribute("species", species.toLowerCase());

        //Import the form containing the marker names.
        final EnterMarkerNameForm markerForm = (EnterMarkerNameForm) form;

        String[] markers = {};
        String keyword = null;

        final Marker2seq mk2seq = new Marker2seq();
        mk2seq.setEndpoint(endpoint);
        mk2seq.setBasegraph(graph);
        Model model = ModelFactory.createDefaultModel();

        if (markerForm.getMarker1() != null
                && !"".equals(markerForm.getMarker1())
                && markerForm.getMarker2() != null
                && !"".equals(markerForm.getMarker2())) {
            markerForm.setMarkers(markerForm.getMarker1() + ","
                    + markerForm.getMarker2());
        }

        try {
            //FIXME: Add NULL check!
            if (markerForm.getMarkers() != null
                    && !markerForm.getMarkers().isEmpty()) {
                System.out.println(markerForm.getMarkers());
                markers = markerForm.getMarkers().split(",");
                for (int i = 0; i < markers.length; i++) {
                    markers[i] = markers[i].trim();
                }
                if (species.equalsIgnoreCase("arabidopsis")) {
                    // Retrieve the models from Virtuoso rather than the WS
                    model = mk2seq.retrieveModelFromVirtuoso(markers);
                } else {
                    try {
                        model = mk2seq.retrieveModel(markers);
                    }
                    catch (SAXException ex) {
                        LOG.log(Level.SEVERE,
                                "ERROR while parsing the web-service output:" + " {0}", ex.getMessage());
                        LOG.log(Level.SEVERE,
                                "Trying to retrieve information from Virtuoso");
                        markerForm.setLoci(mk2seq.getLociString(markers));
                    }
                }
                // If nothing was returned, try to get the interval
                if (model.isEmpty()) {
                    markerForm.setLoci(mk2seq.getLociString(markers));
                }
            } else if (markerForm.getLoci() != null
                    && !markerForm.getLoci().isEmpty()) {
//                model = mk2seq.retrieveModelFromLoci(markerForm.getLoci());
                model = ModelFactory.createDefaultModel();

            } else if (markerForm.getMaploci() != null
                    && !markerForm.getMaploci().isEmpty()) {
                try {
                    model = mk2seq.retrieveModelFromMapLoci(
                            markerForm.getMaploci().trim());
                }
                catch (SAXException ex) {
                    LOG.log(Level.SEVERE,
                            "ERROR while parsing the web-service output:"
                            + " {0}", ex.getMessage());
                }
            } else {
                LOG.info("Got nothing from the URL");
                errors.add("NoInputGiven",
                        new ActionMessage("errors.detail", "No input given"));
                saveErrors(request, errors);
                return mapping.findForward(ERROR);
            }
        }
        catch (SeveralMappedPositionException ex) {
            LOG.log(Level.SEVERE, "ERROR :"
                    + " {0}", ex.getMessage());
            this.handleError(errors, request, markers);
        }
        catch (NumberFormatException ex) {
            LOG.log(Level.SEVERE, "ERROR :"
                    + " {0}", ex.getMessage());
            errors.add("WrongInputGiven",
                    new ActionMessage("errors.detail", "Wrong input form given"));
            saveErrors(request, errors);
            return mapping.findForward(ERROR);
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, "ERROR :"
                    + " {0}", ex.getMessage());
            this.handleError(errors, request, markers);
            EmailExceptions.sendExceptionEmail(context, request, ex);
        }

        if (request != null && request.getParameter("kw") != null) { //||
            //(markerForm != null && !markerForm.getKeyword().isEmpty())) {
            success = "successannotation";
            markerForm.setKeyword(request.getParameter("kw"));
            keyword = markerForm.getKeyword();
            LOG.log(Level.INFO, "Keyword given: {0}", keyword);
        }
        LOG.log(Level.INFO, "model start : {0}", model.size());

        if (model.isEmpty()
                && ( markerForm.getLoci() == null
                || markerForm.getLoci().isEmpty() )) {
            LOG.log(Level.SEVERE, "No markers could be retrieved.");
            errors.add("Marker2seq", new ActionMessage("m2s.error.wrong.input"));
            saveErrors(request, errors);
            return mapping.findForward(ERROR);
        }
        model = mk2seq.addInfoToModel(model, markerForm.getLoci());

        // if the model is still empty, something went wrong...
        if (model.isEmpty()) {
            LOG.log(Level.SEVERE, "No markers could be retrieved.");
            errors.add("Marker2seq", new ActionMessage("m2s.error.wrong.input"));
            saveErrors(request, errors);
            return mapping.findForward(ERROR);
        }

        List<Annotation> annotationlist = this.query.getAnnotationList(model);
        final ArrayList<Markerws> markerfromsgn = this.query.getPhysicalMap(model);
        final ArrayList<MarkerSequence> markersequence =
                this.query.getMarkerSequence(model);
        final List<GeneticMarkers> markerlist = mk2seq.getMarkerlist();

        final long elapsedTimeMillis = System.currentTimeMillis() - start;
        final float elapsedTimeSec = elapsedTimeMillis / 1000F;
        LOG.log(Level.INFO, "time: {0}s", elapsedTimeSec);

        // Post-processing
        try {
            if (( markersequence == null || markersequence.isEmpty() )
                    || ( markerlist == null || markerlist.isEmpty() )) {
                LOG.info("No picture");
            } else {
                for (MarkerSequence ms : markersequence) {
                    this.setExtremeMarkers(ms);
                }

                BufferedImage bim = new BufferedImage(100, 100,
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D gr = bim.createGraphics();

                final HashMap<String, Color> legend =
                        new HashMap<String, Color>(4);
                legend.put("Map & Marker", Color.gray);
                legend.put("Scaffolds", Color.blue);
                legend.put("Unknown orientation", Color.pink);

                final Marker2seqGraphics graphic = new Marker2seqGraphics();

                //sequences =    this.cleanSequences(sequences);
                // Calculate heigth of the picture
                // Sequence part
                Integer heigth = markersequence.size()
                        * ( graphic.getVerticalspace()
                        + graphic.getSequenceheigth() );
                // Name of the marker part
                heigth = heigth + gr.getFontMetrics().stringWidth(
                        graphic.getLonguestMarker(markerlist))
                        + // Legend part
                        10 + legend.size() * graphic.getVerticalspace()
                        //
                        + graphic.getSequencetop();

                //graphic.setHeigth(heigth);

                // Draw the actual image and generate the mapstring:
                bim = new BufferedImage(PICTUREWIDTH, heigth,
                        BufferedImage.TYPE_INT_RGB);
                gr = bim.createGraphics();
                // Fill the background in white
                gr.setColor(Color.white);
                gr.fillRect(0, 0, PICTUREWIDTH, heigth);
                graphic.drawImage(markerlist, markersequence, gr, legend,
                        Arrays.asList(markers));

                session.setAttribute("picturemap", graphic.getMap());
                final String marker2filename = FileName.generateFileNameByTime()
                        + ".png";
                //write as png:
                final File file = new File(System.getProperty("java.io.tmpdir"),
                        marker2filename);
                LOG.log(Level.INFO, "Image file is {0}",
                        file.getCanonicalPath());
                ImageIO.write(bim, "png", file);
                // name of the picture
                session.setAttribute("m2s_alignment_fn", marker2filename);
                //Clear the original response from the webservice
                markersequence.clear();
            }
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }

        try {
            // genetic map table
            if (markerlist != null && !markerlist.isEmpty()) {
                session.setAttribute("map", this.transformToTable(markerlist));
            }
        }
        catch (MobyException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        // annotation table
        //session.setAttribute("annotation", this.query.getAnnotationList(model));
        session.setAttribute("annotation", annotationlist); // annotation table
        session.setAttribute("markers", markerfromsgn); // marker table
        session.setAttribute("model", model); // model

        if (keyword == null || keyword.isEmpty()) {
            session.removeAttribute("resultannotation");
        } else {
            AnnotationSearch search = new AnnotationSearch(graph);
            search.setEndpoint(endpoint);
            annotationlist = this.query.getAnnotationList(
                    search.searchModel(model, keyword));
            session.setAttribute("resultannotation", annotationlist);
        }

        //reset
        markerForm.clearForm();

        LOG.info(" *** Ends ***");

        final long elapsedTimeMs2 = System.currentTimeMillis() - start;
        final float elapsedTimeSec2 = elapsedTimeMs2 / 1000F;
        LOG.log(Level.INFO, "time: {0}s", elapsedTimeSec2);

        return mapping.findForward(success);

    }

    /**
     * This function transform a list to a HashMap used to present the table
     * in the view.
     * @param snplist
     * @throws MobyException
     * @return List<HashMap<String, String>>
     */
    private List<HashMap<String, String>> transformToTable(
            final List<GeneticMarkers> markers)
            throws MobyException {
        final ArrayList<HashMap<String, String>> table =
                new ArrayList<HashMap<String, String>>();

        for (GeneticMarkers marker : markers) {
            final HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", marker.getName());
            map.put("map_position", new Float(
                    marker.getMoby_position().getFloatValue()).toString());
            map.put("chromosome", Long.toString(
                    marker.getMoby_Chromosome().getIntValue()));
            table.add(map);
        }

        return table;
    }

    /**
     * Set the two Extreme marker (start and stop) and a given scafold
     * This way I know according to the genetic map where the scafold should
     * start and stop
     * @param ms
     * @return MarkerSequence
     */
    private MarkerSequence setExtremeMarkers(final MarkerSequence ms) {
        Markerws start = null;
        Markerws end = null;
        for (Markerws marker : ms.getMarkers()) {
            if (start == null) {
                start = marker;
            }
            if (end == null) {
                end = marker;
            }
            if (marker.getMap_position() <= start.getMap_position()
                    && marker.getSeq_position_start()
                    < start.getSeq_position_start()) {
                start = marker;
            }
            if (marker.getMap_position() >= end.getMap_position()
                    && marker.getSeq_position_end()
                    > end.getSeq_position_end()) {
                end = marker;
            }
        }
        ms.setEndmarker(end);
        ms.setStartmarker(start);
//        System.out.println(ms.getName());
//        System.out.println(" start: " + start.getName()
//                                + " end: " + end.getName());
        return ms;

    }

    /**
     * This function displays the potential error found while parsint the output
     * from the web-service. This allow to see quickly if one of the input
     * markers is located on a different LG than the others.
     * @param errors an ActionMessages used to set the error message to display
     * to the user.
     * @param request a HttpServletRequest to which the error is attached.
     * @param markers the markers to locate.
     */
    private void handleError(ActionMessages errors, HttpServletRequest request,
            String[] markers) {
        try {
            ArrayList<String[]> positions = this.getPosition(markers);
            String output = "";
            for (String[] pos : positions) {
                String sca = pos[1];
                if (sca.contains("SCAFFOLD#")) {
                    sca = sca.split("SCAFFOLD#")[1];
                }
                output += "<br /> " + pos[0] + " has scaffold: " + sca;
            }
            LOG.log(Level.SEVERE, "ERROR Makers have different scaffolds:"
                    + " {0}", output);
            errors.add("Marker2seq", new ActionMessage("errors.detail",
                    "Could not proceed because not all the markers are located "
                    + "on the same chromosome:" + output));
            saveErrors(request, errors);

        }
        catch (Exception exc) {
            LOG.log(Level.SEVERE, "ERROR could not map marker:"
                    + " {0}", exc.getMessage());
            errors.add("Marker2seq", new ActionMessage("errors.detail",
                    exc.getMessage()));
            saveErrors(request, errors);
        }
    }

    /**
     * This function return an array of String containing {Marker name, Marker
     * scaffold} for a given marker.
     * @param markers the name of the marker as used at the end of its URI.
     * @return an array of string in the format {Marker name, scaffold}.
     * @throws MarkerNotMappedException if a marker cannot be located.
     */
    private ArrayList<String[]> getPosition(String[] markers)
            throws MarkerNotMappedException {
        ArrayList<String[]> output = new ArrayList<String[]>();
        for (String marker : markers) {
            String scaffold = this.query.getScaffoldMarker(marker);
            if (scaffold == null) {
                throw new MarkerNotMappedException("Could not map marker: " + marker);
            } else {
                String[] tmp = {marker, scaffold};
                output.add(tmp);
            }
        }
        return output;
    }
}
