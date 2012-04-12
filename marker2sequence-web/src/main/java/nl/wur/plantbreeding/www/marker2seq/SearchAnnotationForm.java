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

import javax.servlet.http.HttpServletRequest;
import nl.wur.plantbreeding.logic.util.Validation;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Struts ActionForm for assistance in entering a keyword
 *
 * @author Pierre-Yves Chibon py@chibon.fr
 * @version 0.1
 * @since 0.1
 */
public class SearchAnnotationForm extends org.apache.struts.action.ActionForm {

    /** Serial version UID */
    private static final long serialVersionUID = 260710L;
    /** Marker 1 */
    private String keyword;
    private String graph;
    private String restricted;

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public String getRestricted() {
        return restricted;
    }

    public void setRestricted(String restricted) {
        this.restricted = restricted;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Validates the information received
     * @param mapping
     * @param request
     * @return
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        //TODO: remove debug code
        System.out.println("Starting MarkerNameForm validation");
        System.out.println("Validation keyword: " + this.getKeyword());
        ActionErrors errors = new ActionErrors();

        if (this.getKeyword() == null ? "" == null : this.getKeyword().isEmpty()) {
            //input error
            System.out.println("General validation error");
        } else {
            //validate keyword
            System.out.println("Marker list empty, validating marker 1 and marker 2");
            if (this.getKeyword() == null ? "" == null : this.getKeyword().isEmpty()) {
                errors.add("name", new ActionMessage("error.marker2seq.marker1.name"));
            } else if (Validation.containsSpecialCharactersCheck(this.getKeyword())) {
                errors.add("input", new ActionMessage("errors.invalid", "Maker name :'" + this.getKeyword() + "'"));
            }
        }
        return errors;
    }

    /**
     * Clear the form after an successful invocation.
     */
    void clearForm() {
        this.setKeyword("");
    }
}
