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

/**
 * Represent a marker on a map
 * @author Pierre-Yves Chibon py@chibon.fr
 */
public class Markerws extends Annotation {

    /** Serial version UID */
    private static final long serialVersionUID = 20100811;
    private Float map_position;
    private Long chromosome;
    private String sgnID;

    public Markerws() {
    }

    @Override
    public Markerws clone() {
        Markerws m = new Markerws();
        m.setChromosome(this.getChromosome());
        m.setMap_position(this.getMap_position());
        m.setMethod(this.getMethod());
        m.setName(this.getName());
        m.setScafoldname(this.getScafoldname());
        m.setSeq_position_end(this.getSeq_position_end());
        m.setSeq_position_start(this.getSeq_position_start());
        m.setSgnID(this.getSgnID());
        m.setType(this.getType());
        return m;
    }

    public Long getChromosome() {
        return chromosome;
    }

    public void setChromosome(Long chromosome) {
        this.chromosome = chromosome;
    }

    public Float getMap_position() {
        return map_position;
    }

    public void setMap_position(Float map_position) {
        this.map_position = map_position;
    }

    public String getSgnID() {
        return sgnID;
    }

    public void setSgnID(String sgnID) {
        this.sgnID = sgnID;
    }

    @Override
    public String toString() {
        String s = " name: " + this.getName() + "\n"
                + " chromosome: " + this.chromosome + "\n"
                + " scaffold: " + this.getScafoldname() + "\n"
                + " map position: " + this.map_position + "\n"
                + " seq start position: " + this.seq_position_start + "\n"
                + " seq end position: " + this.seq_position_end + "\n"
                + " SgnId: " + this.sgnID;
        return s;
    }
}
