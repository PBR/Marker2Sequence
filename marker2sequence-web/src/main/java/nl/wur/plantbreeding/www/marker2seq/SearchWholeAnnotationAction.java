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

package nl.wur.plantbreeding.www.marker2seq;

import com.hp.hpl.jena.rdf.model.Model;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nl.wur.plantbreeding.datatypes.Annotation;
import nl.wur.plantbreeding.logic.swtools.QueryRdf;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

/**
 * This is the action class used to search the whole genome annotation.
 * This class checks if the keyword is a valid input, query the genome
 * annotation for related genes and return the gene list.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class SearchWholeAnnotationAction
        extends org.apache.struts.action.Action {

    /** forward name="success". */
    private static final String SUCCESS = "success";
    /** When something wrong happens. */
    private static final String ERROR = "error";
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(
            SearchWholeAnnotationAction.class.getName());

    /**
     * This is the action called from the Struts framework.
     *  - From the given keyword:
     *    - Search the whole annotation
     *    - Display the gene list
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
        final ActionMessages errors = new ActionMessages();
        final QueryRdf query = new QueryRdf();

        //TODO: Fix the use of the form
        final SearchWholeAnnotationForm keywordform = (SearchWholeAnnotationForm) form;
        final String kw = keywordform.getKeyword();

        Model model = query.getGeneAssociatedWithKwFromAnnotation(kw);

        final List<Annotation> annotationlist = query.getAnnotationList(model);
        session.setAttribute("resultannotation", annotationlist);
        LOG.info(annotationlist.size() + " genes found.");

        final long elapsedTimeMs2 = System.currentTimeMillis() - start;
        final float elapsedTimeSec2 = elapsedTimeMs2 / 1000F;
        LOG.log(Level.INFO, "time: {0}s", elapsedTimeSec2);

        LOG.info(" *** End ***");
        return mapping.findForward(SUCCESS);
    }
}
