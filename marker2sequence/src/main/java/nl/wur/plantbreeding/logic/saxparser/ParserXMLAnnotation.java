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

package nl.wur.plantbreeding.logic.saxparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nl.wur.plantbreeding.datatypes.Annotation;
import nl.wur.plantbreeding.datatypes.GeneticMarkers;
import nl.wur.plantbreeding.datatypes.MarkerSequence;
import nl.wur.plantbreeding.datatypes.Markerws;
import org.biomoby.shared.MobyException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Parser for XML annotation using SAX parser.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class ParserXMLAnnotation extends ParserXML {

    /** Count the number of features retrieved. */
    private int featurenumber = 0;
    /** Stores the value until assigned. */
    private String tempType;
    /** Annotation Object. */
    private Annotation annotation;
    /** list containing all the annotations displayed in the table. */
    private ArrayList<Annotation> annotations =
            new ArrayList<Annotation>();
    /** list containing all the markers used to build the pictures. */
    private ArrayList<Annotation> markerlist =
            new ArrayList<Annotation>();
    /** list containing all the markers displayed in the table. */
    private ArrayList<Annotation> markerfromsgn =
            new ArrayList<Annotation>();
    /** List containing the name (and id) of the
     * marker on the map (retrieved by the first service). */
    private ArrayList<String> markernameonmap;
    /** List of the GeneticMarker present on the map as retrieved by the
     * first service. */
    private ArrayList<GeneticMarkers> markeronmap;
    /** HashMap of String markername, Markerws Marker.  */
    private HashMap<String, Markerws> hashmarker =
            new HashMap<String, Markerws>();

    /**
     * Constructor.
     */
    public ParserXMLAnnotation() {
        super();
    }

    /**
     * Populates the private list markeronmap with the SGN-ID of all the markers
     * which are present in the genetic map (output from the 1st ws).
     * @param markers a list of GeneticMarkers
     */
    public final void setMarkerOnMap(final List<GeneticMarkers> markers) {
        markernameonmap = new ArrayList<String>();
        for (GeneticMarkers marker : markers) {
            markernameonmap.add("SGN-M" + marker.getId());
        }
        markeronmap = (ArrayList<GeneticMarkers>) markers;
    }

    /**
     * Returns a list of Annotation which contains all the markers
     * retrieved from SGN.
     * @return featurenumber
     */
    public final ArrayList<Annotation> getMarkerFromSgn() {
        return markerfromsgn;
    }

    /**
     * Returns the number of features found while parsing the xml.
     * @return featurenumber
     */
    public final int getFeatureNumber() {
        return featurenumber;
    }

    /**
     * Returns the list of Marker found on sgn (via the second web-service).
     * @return return ArrayList Markerws
     * @throws MobyException when something goes wrong
     */
    public final ArrayList<Markerws> getMarkerFromSgnAsMarkerws()
            throws MobyException {
        ArrayList<Markerws> markersgnlist = new ArrayList<Markerws>();
        if (debug) {
            System.out.println("before: " + markerfromsgn.size());
            System.out.println("map: " + markernameonmap.size());
        }
        for (Annotation marker : markerfromsgn) {
            if (debug) {
                System.out.println("marker:" + marker.getName() + " - "
                        + markernameonmap.contains(marker.getName()));
            }
            if (markernameonmap.contains(marker.getName())) {
                Markerws markerws = marker.toMarkerws(markeronmap);
                if (markerws != null && !markersgnlist.contains(markerws)) {
                    markersgnlist.add(markerws);
                }
            }
        }
        if (debug) {
            System.out.println("after: " + markersgnlist.size());
        }
        return markersgnlist;
    }

    /**
     * Retrieve the HashMap of marker (marker name, Markerws).
     * @return hashmarker
     */
    public HashMap<String, Markerws> getHashMarker() {
        return hashmarker;
    }

    /**
     * Returns an ArrayList of MarkerSequence which are the scaffolds and their
     * markers. They are used to generate the picture.
     * The boolean send here is the default one, which displays all scaffold
     * including the one with only one marker.
     * @return an ArrayList of MarkerSequence
     * @throws MobyException when something goes wrong.
     */
    public ArrayList<MarkerSequence> getMarkerSequence() throws MobyException {
        return this.getMarkerSequence(false);
    }

    /**
     * Returns an ArrayList of MarkerSequence which are the scaffolds and their
     * markers. They are used to generate the picture.
     * The boolean is used to not display the scaffold with only one marker
     * @param nosingle a boolean wether to allow scaffold with a single marker
     * on them or not.
     * @return an ArrayList of MarkerSequence
     * @throws MobyException when something goes wrong
     */
    public final ArrayList<MarkerSequence> getMarkerSequence(
            final boolean nosingle) throws MobyException {
        HashMap<String, MarkerSequence> hash =
                new HashMap<String, MarkerSequence>();
        for (Annotation annot : markerfromsgn) {
            String scafoldname = annot.getScafoldname();
            Markerws marker = annot.toMarkerws(markeronmap);
            if (marker == null) {
                continue;
            }
            hashmarker.put(marker.getName(), marker);

            if (hash.containsKey(scafoldname)) {
                MarkerSequence tmp = hash.get(scafoldname);
                ArrayList<Markerws> markers =
                        (ArrayList<Markerws>) tmp.getMarkers();
                markers.add(marker);
                tmp.setMarkers(markers);
                hash.put(scafoldname, tmp);
            } else {
                MarkerSequence tmp = new MarkerSequence();
                tmp.setName(scafoldname);
                tmp.setType(annot.getType());
                ArrayList<Markerws> markers = new ArrayList<Markerws>();
                markers.add(marker);
                tmp.setMarkers(markers);

                hash.put(scafoldname, tmp);
            }
        }

        ArrayList<MarkerSequence> ms =
                new ArrayList<MarkerSequence>(hash.values().size());
        for (MarkerSequence markerseq : hash.values()) {
            // kick out scafold with no marker available
            if (markerseq.getMarkers() != null) {
                if (nosingle) {
                    if (markerseq.getMarkers().size() > 1) {
                        ms.add(markerseq);
                    }
                } else {
                    ms.add(markerseq);
                }
            } else {
//                System.out.println("out: " + markerseq.getName());
//                LOG.log(Level.INFO, "out: {0}", markerseq.getName());
            }
        }

        return ms;
    }

    /**
     * Retrieve the Annotation list.
     * @return annotations
     */
    public ArrayList<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * Set the annotation list.
     * @param annotationlist an ArrayList of Annotation
     */
    public void setAnnotations(ArrayList<Annotation> annotationlist) {
        this.annotations = annotationlist;
    }

    /**
     * Function called for each opening tag.
     * @param uri the Namespace URI, or the empty string if the element has no
     * Namespace URI or if Namespace processing is not being performed
     * @param localName the local name (without prefix), or the empty string if
     * Namespace processing is not being performed
     * @param tagname the qualified name (with prefix), or the empty string if
     * qualified names are not available
     * @param attributes the attributes attached to the element. If there are
     * no attributes, it shall be an empty Attributes object. The value of
     * this object after startElement returns is undefined
     * @throws SAXException any SAX exception, possibly wrapping another
     * exception
     */
    //Event Handlers
    @Override
    public void startElement(final String uri, final String localName,
            final String tagname, final Attributes attributes)
            throws SAXException {

        //reset
        tempVal = "";
        if (tagname.equalsIgnoreCase("moby:AnnotatedScaffold")) {
            if (debug) {
                System.out.println("New instance: " + tagname);
                System.out.println(attributes.getValue("moby:id"));
            }
            featurenumber += 1;
            annotation = new Annotation();
            annotation.setScafoldname(attributes.getValue("moby:id"));
            tempType = null;
        } else {
            tempType = attributes.getValue("moby:articleName");
        }
    }

    /**
     * Function called for each closing tag.
     * @param uri the Namespace URI, or the empty string if the element has no
     * Namespace URI or if Namespace processing is not being performed
     * @param localName the local name (without prefix), or the empty string if
     * Namespace processing is not being performed
     * @param qName the qualified name (with prefix), or the empty string if
     * qualified names are not available
     * @throws SAXException any SAX exception, possibly wrapping another
     * exception
     */
    @Override
    public void endElement(String uri, String localName,
            String qName) throws SAXException {

        if ((qName.equalsIgnoreCase("moby:exceptionMessage"))) {
            throw new SAXException(tempVal);
        } else if (qName.equalsIgnoreCase("moby:AnnotatedScaffold")) {
            //add it to the list

            if (debug) {
                System.out.println("Add Node");
            }

            if (annotation.getMethod().endsWith("markers")) {
                if (debug) {
                    System.out.println("Added to markerfromsgn");
                }
                //FIXME: I do not understand why this is needed, seems linked to
                // the redundancy of the markers in the output
                if (markernameonmap.contains(annotation.getName())) {
                    markerfromsgn.add(annotation);
                }
                markerlist.add(annotation);
            } else if (annotation.getName().startsWith("SL")) {
                if (debug) {
                    System.out.println("Added to annotations");
                }
                //TODO: remove this ugly hack
                annotation.setName(annotation.getName() + ".1");
                annotations.add(annotation);
            }//else {
//                System.out.println(annotation.getMethod()
//                        + " " + annotation.getName());
//            }
            tempType = null;
        } else if (tempType == null) {
            return;
        } else if (tempType.equalsIgnoreCase("FeatureType")) {
            if (debug) {
                System.out.println("Type: " + tempVal + " type: " + tempType);
            }
            annotation.setType(tempVal);
        } else if (tempType.equalsIgnoreCase("FeatureStart")) {
            if (debug) {
                System.out.println("FeatureStart: '" + tempVal
                        + "' type: " + tempType);
            }
            annotation.setSeq_position_start(new Integer(tempVal));
        } else if (tempType.equalsIgnoreCase("FeatureStop")) {
            if (debug) {
                System.out.println("FeatureStop: " + tempVal
                        + " type: " + tempType);
            }
            annotation.setSeq_position_end(new Integer(tempVal));
        } else if (tempType.equalsIgnoreCase("FeatureName")) {
            if (debug) {
                System.out.println("FeatureName: " + tempVal
                        + " type: " + tempType);
            }
            annotation.setName(tempVal);
        } else if (tempType.equalsIgnoreCase("FeatureMethod")) {
            if (debug) {
                System.out.println("FeatureMethod: " + tempVal
                        + " type: " + tempType);
            }
            annotation.setMethod(tempVal);
        }

    }

    /**
     * Iterate through the list and print
     * the content.
     * @throws MobyException when something goes wrong
     */
    @Override
    public final void printData() throws MobyException {

        System.out.println("No of annotations '" + annotations.size() + "'.");

        for (Annotation an : annotations) {
            System.out.println(an.toString());
        }
    }
}
