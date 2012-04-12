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

package nl.wur.plantbreeding.datatypes;

import java.util.ArrayList;
import org.biomoby.shared.MobyException;
import org.biomoby.shared.datatypes.GeneticMarker;

/**
 * This is a class that defines all the Annotation object which can
 * be retrieve from the web-service
 * (getAnnotationsForGeneticIntervalBySgnMarkerID)
 *
 * The attributes are defined by the attributes retrieved in the XML.
 *
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class Annotation {

    /** Name of the scafold in which this feature is. */
    private String scafoldname;
    /** Name of the feature. */
    private String name;
    /** Type of the feature. */
    private String type;
    /** Method of the feature. */
    private String method;
    /** Start position on the feature on the scafold (in bp). */
    protected Integer seq_position_start;
    /** End position of the featuer on the scafold (in bp). */
    protected Integer seq_position_end;
    /** Description of this annotation. */
    private String description;
    /** Strand in which the annotation is located. */
    private String strand;
    /** Ranking factor which can be used to order the annotation. */
    private double factor;

    /**
     * Constructor.
     */
    public Annotation() {
    }

    /**
     * Retrieves the ranking factor of this annotation element.
     * @return double factor
     */
    public double getFactor() {
        return factor;
    }

    /**
     * Set the ranking factor of this annotation element.
     * @param factor a string which can be used to rank the annotation elements.
     */
    public void setFactor(String factor) {
        this.factor = Double.parseDouble(factor);
    }

    /**
     * Set the ranking factor of this annotation element.
     * @param factor an int which can be used to rank the annotation elements.
     */
    public void setFactor(double factor) {
        this.factor = factor;
    }

    /**
     * Retrieves the string describing this annotation.
     * @return String description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of this annotation.
     * @param description String describing this annotation
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the strand in which this annotation is located.
     * @return String representind the stand of the annotation
     */
    public String getStrand() {
        return strand;
    }

    /**
     * Set the strand in which this annotation is located
     * should be either 0 or 1.
     * @param strand
     */
    public void setStrand(String strand) {
        this.strand = strand;
    }

    /**
     * Returns the scafold name of this feature.
     * @return String scafoldname
     */
    public String getScafoldname() {
        return scafoldname;
    }

    /**
     * Sets the scafold name of this feature.
     * @param scafoldname
     */
    public void setScafoldname(String scafoldname) {
        this.scafoldname = scafoldname;
    }

    /**
     * Returns the name of this feature.
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this feature.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the end position on this feature on the scafold.
     * @return Integer seq_position_end
     */
    public Integer getSeq_position_end() {
        return seq_position_end;
    }

    /**
     * Sets the end position on this feature on the scafold.
     * @param seq_position_end
     */
    public void setSeq_position_end(Integer seq_position_end) {
        this.seq_position_end = seq_position_end;
    }

    /**
     * Returns the start position on this feature on the scafold.
     * @return Integer seq_position_start
     */
    public Integer getSeq_position_start() {
        return seq_position_start;
    }

    /**
     * Sets the start position on this feature on the scafold.
     * @param seq_position_start
     */
    public void setSeq_position_start(Integer seq_position_start) {
        this.seq_position_start = seq_position_start;
    }

    /**
     * Returns the type of this feature.
     * @return String type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of this feature.
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the method of this feature.
     * @return String method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the method of this feature.
     * @param method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Returns a string containing the information about this feature.
     * @return
     */
    @Override
    public String toString() {
        String s = " name: " + this.name + "\n"
                + " seq method: " + this.method + "\n"
                + " seq type: " + this.type + "\n"
                + " seq scafoldname: " + this.scafoldname + "\n"
                + " seq start position: " + this.seq_position_start + "\n"
                + " seq end position: " + this.seq_position_end + "\n"
                + " factor: " + this.factor + "\n";

        return s;
    }

    /**
     * Transform this feature to a Markerws object if this feature has its name
     * or Id in the given list of markers on the map.
     * If this feature does not have its name/Id of the list of markers on the
     * map the function returns null.
     * @param markeronmap
     * @return Markerws markerws
     * @throws MobyException
     */
    public Markerws toMarkerws(ArrayList<GeneticMarkers> markeronmap)
            throws MobyException {
        Markerws markerws = new Markerws();
        GeneticMarker gm = null;
        for (GeneticMarker marker : markeronmap) {
            if (marker.getName().equals(this.name)
                    || ( "SGN-M" + marker.getId() ).equals(this.name)) {
                gm = marker;
            }
        }
        if (gm == null) {
            return null;
        }
        markerws.setName(gm.getName());
        markerws.setChromosome(gm.getMoby_Chromosome().getIntValue());
        markerws.setMap_position(new Float(gm.getMoby_position().
                getFloatValue()));
        markerws.setSgnID(gm.getId());
        markerws.setSeq_position_start(this.seq_position_start);
        markerws.setSeq_position_end(this.seq_position_end);
        markerws.setScafoldname(this.scafoldname);
        markerws.setMethod(this.method);
        markerws.setType(this.type);

        return markerws;
    }
}
