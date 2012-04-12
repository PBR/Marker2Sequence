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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.biomoby.shared.MobyException;
import org.biomoby.shared.datatypes.GeneticMarker;

/**
 *
 * @author Pierre-Yves Chibon <py@chibon.fr>
 */
public class GeneticMarkers extends GeneticMarker implements Comparable<GeneticMarker> {

    /**
     *
     * @param gm
     * @return
     */
    public int compareTo(GeneticMarker gm) {
        try {
            if (this.get_position() < gm.get_position()) {
                return -1;
            } else {
                return 1;
            }
        } catch (MobyException ex) {
            Logger.getLogger(GeneticMarkers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
}
