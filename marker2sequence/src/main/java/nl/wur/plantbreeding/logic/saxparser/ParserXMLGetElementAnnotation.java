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
import nl.wur.plantbreeding.datatypes.Annotation;
import org.biomoby.shared.MobyException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class handles the parsing of the output coming from the web-service:
 * "GetSifterPredictedFunctionTermsByProtein".
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class ParserXMLGetElementAnnotation extends ParserXML {

    /** Count the number of features retrieved. */
    private int featurenumber = 0;
    /** Store the value until assigned. */
    private String tempType;
    /** Annotation object (which is retrieved from the xml). */
    private Annotation annotation;
    /** list containing all the annotations displayed in the table. */
    private ArrayList<Annotation> annotations =
            new ArrayList<Annotation>();

    //TODO: Retrieve more than just the GO terms
    /**
     * Constructor.
     */
    public ParserXMLGetElementAnnotation() {
        super();
    }

    /**
     * Returns the number of features found while parsing the xml.
     * @return featurenumber
     */
    public final int getAnnotationNumber() {
        return featurenumber;
    }

    /**
     * Returns the annotation object found/generated.
     * @return annotations an ArrayList of Annotation
     */
    public final ArrayList<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * Set the annotation list which will be filled.
     * @param annolist the ArrayList of Annotation
     */
    public final void setAnnotations(final ArrayList<Annotation> annolist) {
        this.annotations = annolist;
    }

    /**
     * Method invocated when the parser meets a start element tag.
     * @param uri uri met
     * @param localName String
     * @param tagname the name of the attribute met
     * @param attributes the value of the attribue met
     * @throws SAXException when something goes wrong
     */
    //Event Handlers
    @Override
    public final void startElement(final String uri, final String localName,
            final String tagname, final Attributes attributes)
            throws SAXException {

        //reset
        tempVal = "";
        if (tagname.equalsIgnoreCase("moby:GeneticElement")) {
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
     * Method invocated every time the parser meets a closing tag.
     * @param uri the uri
     * @param localName the
     * @param qName the attribute closed
     * @throws SAXException when something has gone wrong (ws returned an error)
     */
    @Override
    public final void endElement(final String uri, final String localName,
            final String qName)
            throws SAXException {

        if ((qName.equalsIgnoreCase("moby:exceptionMessage"))) {
            throw new SAXException(tempVal);
        } else if (qName.equalsIgnoreCase("moby:GeneticElement")) {
            //add it to the list

            if (debug) {
                System.out.println("Add Node");
            }

            annotations.add(annotation);
            tempType = null;
        } else if (tempType == null) {
            return;
        } else if (tempType.equalsIgnoreCase("name")) {
            if (debug) {
                System.out.println("NAme: " + tempVal + " type: " + tempType);
            }
            annotation.setName(tempVal);
        } else if (tempType.equalsIgnoreCase("type")) {
            if (debug) {
                System.out.println("Type: '" + tempVal + "' type: " + tempType);
            }
            annotation.setType(tempVal);
        } else if (tempType.equalsIgnoreCase("description")) {
            if (debug) {
                System.out.println("description: " + tempVal
                                    + " type: " + tempType);
            }
            annotation.setDescription(tempVal);
        } else if (tempType.equalsIgnoreCase("start")) {
            if (debug) {
                System.out.println("start: " + tempVal + " type: " + tempType);
            }
            annotation.setSeq_position_start(new Integer(tempVal));
        } else if (tempType.equalsIgnoreCase("stop")) {
            if (debug) {
                System.out.println("stop: " + tempVal + " type: " + tempType);
            }
            annotation.setSeq_position_end(new Integer(tempVal));
        } else if (tempType.equalsIgnoreCase("strand")) {
            if (debug) {
                System.out.println("strand: " + tempVal + " type: " + tempType);
            }
            annotation.setStrand(tempVal);
        }

    }

    /**
     * Iterate through the list and print the content.
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
