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

package nl.wur.plantbreeding.logic.marker2seq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Utility class for Marker2seq
 * @author Pierre-Yves Chibon - py@chibon.fr
 */
public class Marker2SeqUtils {

    /** Logger. */
    private static final Logger LOG =
            Logger.getLogger(Marker2SeqUtils.class.getName());

    /**
     * Return the base graph to use for querying according to the specie and
     * the theme used in the project.
     * @param specie can be tomato, potato or arabidopsis (case insensitive).
     * @param theme used to add project specific graph (ie: for cbsg).
     * @return a String to add to the queries to retrieve the right information.
     */
    public static String GetBaseGraphFromTheme(String specie,
                final String theme){
        specie = specie.toLowerCase();
//        LOG.log(Level.INFO, "Species {0}", specie);
//        LOG.log(Level.INFO, "Theme {0}", theme);
        String graph = "FROM <http://itag2.pbr.wur.nl/> \n";
        
        if (specie.equals("tomato")) {
            graph = "FROM <http://itag2.pbr.wur.nl/> \n";
        } else if (specie.equals("potato")) {
            graph = "FROM <http://pgsc.pbr.wur.nl/> \n";
        } else if (specie.equals("arabidopsis")) {
            graph = "FROM <http://at.pbr.wur.nl/> \n";
        }

        if (theme.startsWith("CBSG")){
            graph = graph + "FROM <http://cbsg.pbr.wur.nl/> \n";
        }
        
//        LOG.log(Level.INFO, "Using basegraph {0}", graph);
        return graph;
    }

    /**
     * This methods takes as input the outpu from the method
     * 'getGeneSelectedAnnotation' in the QueryRdf class and transform it into
     * a HashMap.
     * @param matrix an ArrayList of ArrayList of String containing the matrix of
     * results.
     * @return a HashMap containing as key the type of information from which
     * the gene was selected and as value the actual string of information.
     */
    public static HashMap<String, ArrayList<String>> TransformAnnotationListToHash(
            ArrayList<ArrayList<String>> matrix){
        HashMap<String, ArrayList<String>> output =
                new HashMap<String, ArrayList<String>>();

        for (ArrayList<String> row : matrix){
            if (row.get(1).contains("GENE#")){
                Marker2SeqUtils.addToHashMap("Gene", row.get(2), output);

            } else if (row.get(1).contains("geneontology.org")
                    || row.get(1).contains("GO")){
                Marker2SeqUtils.addToHashMap("Go", row.get(2), output);

            } else if (row.get(1).contains("Pathway")){
                Marker2SeqUtils.addToHashMap("Pathway", row.get(2), output);

            } else if (row.get(1).contains("uniprot.org")){
                Marker2SeqUtils.addToHashMap("Protein", row.get(2), output);

            } else{
                System.out.println("Not mapped:" + row.get(1));
            }
        }
        return output;
    }

    /**
     * Add the given value at the given key, or enter the new key/value.
     * @param key a String of the key.
     * @param value a String value to be added for this key.
     * @param map the HashMap to update.
     */
    private static void addToHashMap(String key, String value,
            HashMap<String, ArrayList<String>> map){
        if(map.containsKey(key)){
            ArrayList<String> values = map.get(key);
            if (!values.contains(value)){
                values.add(value);
            }
            map.put(key, values);
        } else {
            ArrayList<String> values = new ArrayList<String>();
            values.add(value);
            map.put(key, values);
        }
    }

}
