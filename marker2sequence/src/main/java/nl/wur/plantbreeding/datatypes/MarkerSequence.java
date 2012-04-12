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

import java.util.List;

/**
 * Represents the marker information of a Sequence
 * @author Pierre-Yves Chibon py@chibon.fr
 */
public class MarkerSequence extends Sequence {

    private Markerws startmarker;
    private Markerws endmarker;
    private List<Markerws> markers;

    public MarkerSequence() {
    }

    public MarkerSequence(String name, String sequence, String type, Markerws startmarker, Markerws endmarker, List<Markerws> markers) {
        super(name, sequence, type);
        this.startmarker = startmarker;
        this.endmarker = endmarker;
        this.markers = markers;
    }

    public MarkerSequence(Markerws startmarker, Markerws endmarker, List<Markerws> markers) {
        this.startmarker = startmarker;
        this.endmarker = endmarker;
        this.markers = markers;
    }

    public Markerws getEndmarker() {
        return endmarker;
    }

    public void setEndmarker(Markerws endmarker) {
        this.endmarker = endmarker;
    }

    public List<Markerws> getMarkers() {
        return markers;
    }

    public void setMarkers(List<Markerws> markers) {
        this.markers = markers;
    }

    public Markerws getStartmarker() {
        return startmarker;
    }

    public void setStartmarker(Markerws startmarker) {
        this.startmarker = startmarker;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString(){
        String s = " name: " + this.getName() + "\n"
                + " start marker: " + this.getStartmarker().getName() + "\n"
                + " end marker: " + this.getEndmarker().getName() + "\n"
                + " markers #: " + this.getMarkers().size() + "\n"
                + " sequence:" + this.getSequence() + "\n";
        return s;
    }
}
