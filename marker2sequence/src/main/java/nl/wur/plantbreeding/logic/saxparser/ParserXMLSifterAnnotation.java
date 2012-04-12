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
public class ParserXMLSifterAnnotation extends ParserXML {

    /** Count the number of features retrieved. */
    private int featurenumber = 0;
    /** Stores the value until assigned. */
    private String tempType;
    /** Element retrieved. */
    private Annotation annotation;
    /** list containing all the annotations displayed in the table. */
    private ArrayList<Annotation> annotations =
            new ArrayList<Annotation>();

    //TODO: Retrieve more than just the GO terms
    /**
     * Constructor.
     */
    public ParserXMLSifterAnnotation() {
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
     * Retrieve the list of Annotation object.
     * @return an ArrayList of Annotation
     */
    public final ArrayList<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * Set the list of Annotation to fill.
     * @param annotationlist an ArrayList of Annotation
     */
    public final void setAnnotations(
            final ArrayList<Annotation> annotationlist) {
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
    public final void startElement(final String uri, final String localName,
            final String tagname, final Attributes attributes)
            throws SAXException {

        //reset
        tempVal = "";
        if (tagname.equalsIgnoreCase("moby:Annotated_GO_Term")) {
            if (debug) {
                System.out.println("New instance: " + tagname);
                System.out.println(attributes.getValue("moby:id"));
            }
            featurenumber += 1;
            annotation = new Annotation();
            annotation.setScafoldname("GO:" + attributes.getValue("moby:id"));
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
    public final void endElement(final String uri, final String localName,
            final String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("moby:exceptionMessage")) {
            throw new SAXException(tempVal);
        } else if (qName.equalsIgnoreCase("moby:Annotated_GO_Term")) {
            //add it to the list

            if (debug) {
                System.out.println("Add Node");
            }

            annotations.add(annotation);
            tempType = null;
        } else if (tempType == null) {
            return;
        } else if (tempType.equalsIgnoreCase("Definition")) {
            if (debug) {
                System.out.println("Definition: " + tempVal
                        + " type: " + tempType);
            }
            annotation.setName(tempVal);
        } else if (tempType.equalsIgnoreCase("process")) {
            if (debug) {
                System.out.println("process: '" + tempVal
                        + "' type: " + tempType);
            }
            annotation.setType(tempVal);
        } else if (tempType.equalsIgnoreCase("detailed_description")) {
            if (debug) {
                System.out.println("detailed_description: "
                        + tempVal + " type: " + tempType);
            }
            annotation.setMethod(tempVal);
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
