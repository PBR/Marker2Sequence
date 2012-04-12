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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nl.wur.plantbreeding.datatypes.Annotation;
import nl.wur.plantbreeding.logic.marker2seq.AnnotationSearch;
import nl.wur.plantbreeding.logic.swtools.QueryRdf;
import nl.wur.plantbreeding.logic.marker2seq.Marker2SeqUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * This is the action class of the marker2seq tool.
 * This class checks if the two markers are valid input, call the two
 * web-services call the creation of the picture and returns all the information
 * to the jsp.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class SearchAnnotationAction extends org.apache.struts.action.Action {

    /** forward name="success". */
    private static final String SUCCESS = "success";
    /** When something wrong happens. */
    private static final String ERROR = "error";
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(
            SearchAnnotationAction.class.getName());

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
    public ActionForward execute(final ActionMapping mapping,
            final ActionForm form,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        final long start = System.currentTimeMillis();

        LOG.info(" *** Start ***");
        final HttpSession session = request.getSession();
        final ServletContext context = getServlet().getServletContext();
        final ActionMessages errors = new ActionMessages();
        final Model model = (Model) session.getAttribute("model");
        final QueryRdf query = new QueryRdf();

        if (model == null) {
            errors.add("SearchAnnotationAction",
                    new ActionMessage("errors.m2s.search.annotation.no.model"));
            saveErrors(request, errors);
            return mapping.findForward(ERROR);
        }
//        final SearchAnnotationForm keywordform = (SearchAnnotationForm) form;

        //TODO: Fix the use of the form
//        String kw = keywordform.getKeyword();
        final String kw = request.getParameter("keyword");
        LOG.log(Level.INFO, "kw: {0}", kw);
        //String graph = keywordform.getGraph()
        final String graph = request.getParameter("graph");
        LOG.log(Level.INFO, "graph: {0}", graph);
//        String restricted = keywordform.getRestricted();
        final String restricted = request.getParameter("restricted");
        LOG.log(Level.INFO, "restricted: {0}", restricted);

        final String theme = context.getInitParameter("theme");
        final String species = context.getInitParameter("m2sSpecies");
        final String bgraph = Marker2SeqUtils.GetBaseGraphFromTheme(species, theme);

        request.setAttribute("species", species.toLowerCase());

        if (kw == null || kw.isEmpty()) {
            session.setAttribute("kw", "notok");
        } else {
            session.setAttribute("kw", kw);
            AnnotationSearch search = new AnnotationSearch(bgraph);
            final List<Annotation> annotationlist = query.getAnnotationList(
                    search.searchModel(model, kw));
            session.setAttribute("resultannotation", annotationlist);
        }


        if (graph != null && !graph.isEmpty()) {
            if (graph.equals("alignment")) {
                session.setAttribute("type", "alignment");
                session.setAttribute("restricted", "notok");
                final String alignfn = (String) session.getAttribute(
                        "m2s_alignment_fn");
                if (alignfn == null || alignfn.isEmpty()) {
                    LOG.log(Level.INFO, "TODO: regenerate the picture");
                    //TODO: Add code to regenerate the alignment picture
                }
            } else if (graph.equals("go")) {
                session.setAttribute("type", "go");
                if (restricted != null && restricted.equals("1")
                        && kw != null && !kw.isEmpty()) {
                    LOG.log(Level.INFO, "Restricted: {0}", kw);
                    session.setAttribute("restricted", "ok");
                    this.generateGoDistributionGraph(
                            query.getRestrictedModel(model, kw), session);
                } else {
                    session.setAttribute("restricted", "notok");
                    LOG.log(Level.INFO, "Not Restricted");
                    this.generateGoDistributionGraph(model, session);
                }
            }
        }


        final long elapsedTimeMs2 = System.currentTimeMillis() - start;
        final float elapsedTimeSec2 = elapsedTimeMs2 / 1000F;
        LOG.log(Level.INFO, "time: {0}s", elapsedTimeSec2);

        return mapping.findForward(SUCCESS);

    }

    /**
     * For a given model, generate the pie chart of the GO distribution and
     * put them in the session.
     * @param model a Jena Model from which the GO are queried
     * @param session the session in which to store the filename of the graph
     */
    private void generateGoDistributionGraph(final Model model,
            final HttpSession session) {
        final QueryRdf query = new QueryRdf();
        LOG.log(Level.INFO, "Generating Pie chart");
        /*
         * Distribution of the go namespace
         */
        this.setGoNamespaceDistribution(query, model, session);

        /*
         * Distribution of the go terms
         */
        this.setGoTermDistribution(query, model, session);

        /*
         * Distribution of the go synonyms
         */
        this.setGoSynonymDistribution(query, model, session);
    }

    /**
     * Retrieve the Go Term distribution from the model and generate the
     * associated Pie Chart.
     * @param query a QueryRdf object used to retrieved the GO terms
     * @param model the jena Model to query
     * @param session the session in which will be stored the graph
     */
    private void setGoTermDistribution(
            final QueryRdf query,
            final Model model,
            final HttpSession session) {
        final HashMap<String, Integer> distrib =
                query.getGoDistribution(model);
        int tot = 0;
        for (Integer num : distrib.values()) {
            tot += num;
        }
        String[] out = null;
        try {
            out = GoDistribution.generateDistribution(distrib,
                    "GO distribution", "#", session,
                    false, true, true, 10);
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        if (out != null) {
            //store the image:
            session.setAttribute("m2s_go_fn", out[0]);
            session.setAttribute("m2s_go_map", out[1]);
            session.setAttribute("m2s_go_gonum", tot);
        }
        LOG.log(Level.INFO, "GO term distribution done");
    }

    /**
     * Retrieve the GO Term namespace distribution from the model and generate
     * the associated Pie Chart.
     * @param query a QueryRdf object used to retrieved the GO terms
     * @param model the jena Model to query
     * @param session the session in which will be stored the graph
     */
    private void setGoNamespaceDistribution(
            final QueryRdf query,
            final Model model,
            final HttpSession session) {
        final HashMap<String, Integer> distrib =
                query.getGoNameSpaceDistribution(model);
        int tot = 0;
        for (Integer num : distrib.values()) {
            tot += num;
        }
        String[] out = null;
        try {
            out = GoDistribution.generateDistribution(distrib,
                    "GO name space distribution", "#", session,
                    true, true, true, 10);
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        if (out != null) {
            //store the image:
            session.setAttribute("m2s_go_ns_fn", out[0]);
            session.setAttribute("m2s_go_ns_map", out[1]);
            session.setAttribute("m2s_go_ns_gonum", tot);
        }
        LOG.log(Level.INFO, "GO namespace distribution done");
    }

    /**
     * Retrieve the GO Term synonym distribution from the model and generate
     * the associated Pie Chart.
     * @param query a QueryRdf object used to retrieved the GO terms
     * @param model the jena Model to query
     * @param session the session in which will be stored the graph
     */
    private void setGoSynonymDistribution(
            final QueryRdf query,
            final Model model,
            final HttpSession session) {
        final HashMap<String, Integer> distrib =
                query.getGoSynonymDistribution(model);
        int tot = 0;
        for (Integer num : distrib.values()) {
            tot += num;
        }
        String[] out = null;
        try {
            out = GoDistribution.generateDistribution(distrib,
                    "GO synonym distribution", "#", session,
                    false, true, true, 10);
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        if (out != null) {
            //store the image:
            session.setAttribute("m2s_go_syn_fn", out[0]);
            session.setAttribute("m2s_go_syn_map", out[1]);
            session.setAttribute("m2s_go_syn_gonum", tot);
        }
        LOG.log(Level.INFO, "GO synonym distribution done");
    }
}
