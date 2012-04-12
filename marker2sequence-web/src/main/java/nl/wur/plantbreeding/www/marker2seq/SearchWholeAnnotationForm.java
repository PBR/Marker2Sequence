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
 * 0.2 Added handling of QTL_IM marker string
 */
package nl.wur.plantbreeding.www.marker2seq;

import javax.servlet.http.HttpServletRequest;
import nl.wur.plantbreeding.logic.util.Validation;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Struts ActionForm for assistance in entering a keyword.
 *
 * @author Pierre-Yves Chibon py@chibon.fr
 * @version 0.1
 * @since 0.1
 */
public class SearchWholeAnnotationForm
        extends org.apache.struts.action.ActionForm {

    /** Serial version UID. */
    private static final long serialVersionUID = 260711L;
    /** Keyword to search for. */
    private String keyword;

    /**
     * Retrieve the keyword of the form.
     * @return a String of the keyword
     */
    public final String getKeyword() {
        return keyword;
    }

    /**
     * Set the keyword of the form.
     * @param tmpkeyword a String of the keyword
     */
    public final void setKeyword(final String tmpkeyword) {
        this.keyword = tmpkeyword;
    }

    /**
     * Validates the information received.
     * @param mapping an ActionMapping
     * @param request a HttpServletRequest
     * @return an ActionErrors
     */
    @Override
    public final ActionErrors validate(final ActionMapping mapping,
            final HttpServletRequest request) {
        //TODO: remove debug code
        System.out.println("Starting SearchWholeAnnotationForm validation");
        System.out.println("Validation keyword: " + this.getKeyword());
        ActionErrors errors = new ActionErrors();

        if (this.getKeyword() == null ? "" == null
                    : this.getKeyword().isEmpty()) {
            //input error
            System.out.println("General validation error");
        } else {
            //validate keyword
            if (Validation.containsSpecialCharactersCheck(this.getKeyword())) {
                errors.add("input", new ActionMessage("errors.invalid",
                        "Input: '" + this.getKeyword() + "'"));
            }
        }
        return errors;
    }

    /**
     * Clear the form after an successful invocation.
     */
    final void clearForm() {
        this.setKeyword("");
    }
}
