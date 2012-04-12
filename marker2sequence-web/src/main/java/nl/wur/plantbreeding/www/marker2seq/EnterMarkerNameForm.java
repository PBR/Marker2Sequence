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
 * 0.3 Added Loci support
 * 0.2 Added handling of QTL_IM marker string
 */
package nl.wur.plantbreeding.www.marker2seq;

import javax.servlet.http.HttpServletRequest;
import nl.wur.plantbreeding.logic.util.Validation;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Struts ActionForm for assistance in entering marker name.
 *
 * @author Pierre-Yves Chibon py@chibon.fr
 * @author Richard Finkers
 * @version 0.3
 * @since 0.1
 */
public class EnterMarkerNameForm extends org.apache.struts.action.ActionForm {

    /** Serial version UID */
    private static final long serialVersionUID = 260710L;
    /** Marker 1 */
    private String marker1;
    /** Marker 2 */
    private String marker2;
    /** A comma separated string containing more than two markers (QTL_IM marker interval) */
    private String markers;
    /**
     * A comma separated string containing the list of loci on the genome to
     * investigate.
     * in the form: chrX:start..stop,chrY:start,stop
     */
    private String loci;
    /**
     * A comma separated string containing the list of loci on the map to 
     * investigate
     * in the form: chrX:start..stop,chrY:start,stop
     */
    private String maploci;
    /** A keyword used to reduced the gene list */
    private String keyword;

    public String getMarker1() {
        return marker1;
    }

    public void setMarker1(String marker1) {
        this.marker1 = marker1.trim();
    }

    public String getMarker2() {
        return marker2;
    }

    public void setMarker2(String marker2) {
        this.marker2 = marker2.trim();
    }

    public String getMarkers() {
        return markers;
    }

    public void setMarkers(String markers) {
        this.markers = markers;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setLoci(String loci) {
        this.loci = loci;
    }

    public String getLoci() {
        return this.loci;
    }

    public String getMaploci() {
        return maploci;
    }

    public void setMaploci(String maploci) {
        this.maploci = maploci;
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
        System.out.println("Validation Marker1: " + getMarker1());
        System.out.println("Validation Marker2: " + getMarker2());
        System.out.println("Validation Markers: " + getMarkers());
        System.out.println("Validation Loci: " + getLoci());
        System.out.println("Validation MapLoci: " + getMaploci());
        System.out.println("Validation keyword: " + getKeyword());
        ActionErrors errors = new ActionErrors();

        if (getMarker1() == null ? "" == null : getMarker1().isEmpty()
                || getLoci() == null ? "" == null : getLoci().isEmpty()
                || getMarkers() == null ? "" == null : getMarkers().isEmpty()) {
            //input error
            System.out.println("General validation error");
        } else {
            //test if marker list = null
            if (getMarkers() == null && getLoci() == null && getMaploci() == null) {
                //validate Marker 1 and Marker 2 input
                System.out.println("Marker list empty, validating marker 1 and marker 2");
                if (getMarker1() == null ? "" == null : getMarker1().isEmpty()) {
                    errors.add("name", new ActionMessage("error.marker2seq.marker1.name"));
                } else if (Validation.containsSpecialCharactersCheck(getMarker1())) {
                    errors.add("input", new ActionMessage("errors.invalid",
                                "Maker name :'" + getMarker1() + "'"));
                }
                if (getMarker2() == null ? "" == null : getMarker2().isEmpty()) {
                    errors.add("name", new ActionMessage("error.marker2seq.marker2.name"));
                } else if (Validation.containsSpecialCharactersCheck(getMarker2())) {
                    errors.add("input", new ActionMessage("errors.invalid",
                                "Maker name :'" + getMarker2() + "'"));
                }
            } else if (getMarkers() != null && getLoci() == null &&
                        getMaploci() == null) {
                //validate marker list input
                System.out.println("Validating marker list");
                if (getMarkers() == null ? "" == null : getMarkers().isEmpty()) {
                    errors.add("name", new ActionMessage("error.marker2seq.markers.name"));
                } else if (Validation.containsSpecialCharactersCheck(getMarkers())) {
                    errors.add("input", new ActionMessage("errors.invalid",
                                "Makers list :'" + getMarkers() + "'"));
                }
            } else if (getLoci() != null && getMaploci() != null ){
                System.out.println("Validating loci list");
                if (getLoci() == null ? "" == null : getLoci().isEmpty()) {
                    errors.add("name", new ActionMessage("error.marker2seq.markers.name"));
                } else if (Validation.containsSpecialCharactersCheck(getLoci())) {
                    errors.add("input", new ActionMessage("errors.invalid",
                                "Loci list :'" + getLoci() + "'"));
                }
            } else {
                System.out.println("Validating Maploci list");
                if (getMaploci() == null ? "" == null : getMaploci().isEmpty()) {
                    errors.add("name", new ActionMessage("error.marker2seq.markers.name"));
                } else if (Validation.containsSpecialCharactersCheck(getMaploci())) {
                    errors.add("input", new ActionMessage("errors.invalid",
                                "Maploci list :'" + getLoci() + "'"));
                }
            }
            // Validate the keyword if present
            if (getMarkers() != null && !getMarkers().isEmpty()
                    && Validation.containsSpecialCharactersCheck(getKeyword())) {
                errors.add("input", new ActionMessage("errors.invalid",
                            "Keyword:'" + getKeyword() + "'"));
            }
        }
        return errors;
    }

    /**
     * Clear the form after an successful invocation.
     */
    void clearForm() {
        setMarker1("");
        setMarker2("");
        setMarkers(null);
        setLoci(null);
        setMaploci(null);
        setKeyword(null);
    }
}
