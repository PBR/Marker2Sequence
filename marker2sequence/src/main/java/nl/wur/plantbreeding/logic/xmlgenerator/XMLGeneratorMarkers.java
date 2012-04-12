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

package nl.wur.plantbreeding.logic.xmlgenerator;

/**
 * This class generates the XML for the web-service
 * "getMarkersForRegionDelimitedByQueryMarkers"
 * @author Pierre-Yves Chibon <py@chibon.fr>
 */
public class XMLGeneratorMarkers {

    /**
     *  Generates the input XML for the web-service "getMarkersForRegionDelimitedByQueryMarkers"
     * which takes two marker name as parameter
     * @param marker1 string representing the name of one marker (ie: "Cf9")
     * @param marker2 string representing the name of another marker (ie: "T1586")
     * @return
     */
    public static String generateXML(String marker1, String marker2) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<moby:MOBY xmlns:moby=\"http://www.biomoby.org/moby\">"
                + " <moby:mobyContent>"
                + "  <moby:mobyData moby:queryID=\"sip_1_\"> "
                + "     <moby:Simple moby:articleName=\"Marker2\">"
                + "       <moby:Marker moby:id=\"" + marker2 + "\" moby:namespace=\"\" /> "
                + "     </moby:Simple> "
                + "     <moby:Simple moby:articleName=\"Marker1\">"
                + "       <moby:Marker moby:id=\"" + marker1 + "\" moby:namespace=\"\" />"
                + "     </moby:Simple>"
                + "   </moby:mobyData>"
                + "  </moby:mobyContent>"
                + " </moby:MOBY>";
        return xml;
    }

    /**
     * Generates the input XML for the web-service "getMarkersForRegionByQueryMarkerList"
     * which takes a list of markers as input
     * @param markers String[] containing the name of all the markers
     * @return
     */
    public static String generateXML(String[] markers){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<moby:MOBY xmlns:moby=\"http://www.biomoby.org/moby\">"
                + "  <moby:mobyContent>"
                + "    <moby:mobyData moby:queryID=\"sip_1_\">"
                + "      <moby:Collection moby:articleName=\"Markers\">";
        for(String marker: markers){
            xml = xml + "        <moby:Simple>"
                + "          <moby:Marker moby:id=\"" + marker + "\" moby:namespace=\"\" />"
                + "        </moby:Simple>";

        }
         xml = xml + "      </moby:Collection>"
                + "    </moby:mobyData>"
                + "  </moby:mobyContent>"
                + "</moby:MOBY>";
        return xml;
    }
}
