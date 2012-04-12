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
import java.util.List;

/**
 * Represent a sequence
 * @author Pierre-Yves Chibon py@chibon.fr
 */
public class Sequence {

    private String name;
    private String sequence;
    private String type;

    public Sequence() {
    }

    public Sequence(String name, String sequence, String type) {
        this.name = name;
        this.sequence = sequence;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Sequence> getSequenceFromMarker(List<String> markers) {
        ArrayList<Sequence> sequences = new ArrayList<Sequence>();
        throw new UnsupportedOperationException("Not yet implemented");
        // for (String markername: markers){
        //   sequences.add(db.getSequenceOfMarker(markername);
        // }
        // return sequences;
    }

    private Sequence getSequenceOfMarker(String markername, List<Sequence> sequences) {
        for (Sequence seq : sequences) {
            if (seq.name.equals(markername)) {
                return seq;
            }
        }
        return null;
    }
}
