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
 * This class generates the XML for the web-service developed by Cologne:
 * "GetSifterPredictedFunctionTermsByProtein"
 * @author Pierre-Yves Chibon <py@chibon.fr>
 */
public class XMLGeneratorGetAnnotation {

    /**
     * Generates the input XML for the web-services:
     *  - GetSifterPredictedFunctionTermsByProtein - MPIZ
     *  - GetElementAnnotation - MIPS
     * which takes two parameters, a namespace and a geneid
     * @param geneid string retrieved from SGN (ie:SL1.00sc04161_16.1.1)
     * @param namespace string describing the organism to query (ie: MIPS_GE_Tomato)
     * @return
     */
    public static String generateXML(String geneid, String namespace) {
       return XMLGeneratorGetAnnotation.generateXML(geneid, namespace, "identifier");
    }

    /**
     * Generates the input XML for the web-services:
     *  - GetSifterPredictedFunctionTermsByProtein - MPIZ
     *  - GetElementAnnotation - MIPS
     * which takes two parameters, a namespace and a geneid
     * @param geneid string retrieved from SGN (ie:SL1.00sc04161_16.1.1)
     * @param namespace string describing the organism to query (ie: MIPS_GE_Tomato)
     * @param articlename string used in the articlename of the XML, should be
     *   - "identifier" for GetSifterPredictedFunctionTermsByProtein - MPIZ (default)
     *   - "input" for GetElementAnnotation - MIPS
     * @return
     */
    public static String generateXML(String geneid, String namespace, String articlename) {
        String xml =    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                      +  "<moby:MOBY xmlns:moby=\"http://www.biomoby.org/moby\">"
                      +  "   <moby:mobyContent>"
                      +  "    <moby:mobyData moby:queryID=\"sip_1_\">"
                      +  "      <moby:Simple moby:articleName=\"" + articlename + "\">"
                      +  "        <moby:Object moby:id=\"" + geneid + "\" moby:namespace=\"" + namespace + "\" />"
                      +  "      </moby:Simple>"
                      +  "    </moby:mobyData>"
                      +  "  </moby:mobyContent>"
                      +  "</moby:MOBY>";
        return xml;
    }
}
