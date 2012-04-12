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
import nl.wur.plantbreeding.datatypes.GeneticMarkers;

import org.biomoby.shared.MobyException;
import org.biomoby.shared.datatypes.GeneticMarker;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Parser for Markers using SAX.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class ParserXMLMarkers extends ParserXML {

    /** A GeneticMarker object. */
    private GeneticMarker marker;
    /** An ArrayList of GeneticMarker. */
    private ArrayList<GeneticMarkers> markers =
            new ArrayList<GeneticMarkers>();

    /**
     * Retrieve the GeneticMarkers.
     * @return markers
     */
    public final ArrayList<GeneticMarkers> getMarkers() {
        return markers;
    }

    /**
     * Set the list of GeneticMarkers.
     * @param markerlist an ArrayList of GeneticMarkers
     */
    public final void setMarkers(final ArrayList<GeneticMarkers> markerlist) {
        this.markers = markerlist;
    }

    /**
     * Function called for each opening tag.
     * @param uri the Namespace URI, or the empty string if the element has no
     * Namespace URI or if Namespace processing is not being performed
     * @param localName the local name (without prefix), or the empty string if
     * Namespace processing is not being performed
     * @param qName the qualified name (with prefix), or the empty string if
     * qualified names are not available
     * @param attributes the attributes attached to the element. If there are
     * no attributes, it shall be an empty Attributes object. The value of
     * this object after startElement returns is undefined
     * @throws SAXException any SAX exception, possibly wrapping another
     * exception
     */
    @Override
    public void startElement(final String uri, final String localName,
            final String qName, final Attributes attributes)
            throws SAXException {
        //reset
        tempVal = "";
        //if( ! qName.equalsIgnoreCase("")) {
        if (qName.equalsIgnoreCase("moby:GeneticMarker")) {
            //System.out.println("New instance: " + qName);
            //System.out.println(attributes.getValue("moby:id"));
            marker = new GeneticMarkers();
            marker.setName(attributes.getValue("moby:id"));
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
    public void endElement(final String uri, final String localName,
            final String qName) throws SAXException {

        if ((qName.equalsIgnoreCase("moby:exceptionMessage"))) {
            throw new SAXException(tempVal);
        } else if (qName.equalsIgnoreCase("moby:GeneticMarker")) {
            //add it to the list
            if (debug) {
                System.out.println("Add Node");
            }
            markers.add((GeneticMarkers) marker);

        } else if (qName.equalsIgnoreCase("moby:Integer")) {
            if (debug) {
                System.out.println("Set Chr: " + tempVal);
            }
            marker.set_Chromosome(new Integer(tempVal));
        } else if (qName.equalsIgnoreCase("moby:Float")) {
            if (debug) {
                System.out.println("Set Pos: " + tempVal);
            }
            marker.set_position(new Double(tempVal));
        } else if (qName.equalsIgnoreCase("moby:String")) {
            if (debug) {
                System.out.println("Set sgn: " + tempVal);
            }
            marker.setId(tempVal);
        }

    }

    /**
     * Iterate through the list and print
     * the contents.
     * @throws MobyException when something goes wrong
     */
    @Override
    public final void printData() throws MobyException {

        System.out.println("No of markers '" + markers.size() + "'.");

        for (GeneticMarker ma : markers) {
            System.out.println(ma.toString());
        }
    }
}
