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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Extract the content (which is itself an XML file) from the getElement answer.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class ParserMipsElementToXml extends ParserXML {

    /** String of the motif to search in the XML (
     * for example: moby:GeneticMarker). */
    private String motif;
    /** String containing the content. */
    private String content;
    /** String containing the element. */
    private String element;

    /**
     * Get the motif searched in the XML.
     * @return motif
     */
    public final String getMotif() {
        return motif;
    }

    /**
     * Set the motif searched in the XML in the startElement function.
     * @param newmotif String of the motif to search in the XML
     */
    public final void setMotif(final String newmotif) {
        this.motif = newmotif;
    }

    /**
     * Returns the content extract from the XML of the GetElement ws from MIPS.
     * @return content
     */
    public final String getContent() {
        return content;
    }

    /**
     * Returns the name of the element about which are the information
     * retrieved.
     * @return element
     */
    public final String getElement() {
        return element;
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
    public final void startElement(final String uri, final String localName,
            final String qName, final Attributes attributes)
            throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase(this.motif)) {
            if (debug) {
                System.out.println("New instance: " + qName);
            }
            this.element = attributes.getValue("moby:id");
        }
    }

    /**
     * Function called for each new closing tag.
     * @param uri the Namespace URI, or the empty string if the element has no
     * Namespace URI or if Namespace processing is not being performed
     * @param localName the local name (without prefix), or the empty string if
     * Namespace processing is not being performed
     * @param qName the qualified name (with prefix), or the empty string if
     * qualified names are not available
     * @throws SAXException when something goes wrong
     */
    @Override
    public final void endElement(final String uri, final String localName,
            final String qName) throws SAXException {

        if ((qName.equalsIgnoreCase("moby:exceptionMessage"))) {
            throw new SAXException(tempVal);
        } else if (qName.equalsIgnoreCase(this.motif)) {
            if (debug) {
                System.out.println("val1: " + tempVal);
            }
            this.content = tempVal;
        }
    }
}
