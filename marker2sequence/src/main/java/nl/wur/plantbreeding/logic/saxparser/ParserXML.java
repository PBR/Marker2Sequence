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

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.biomoby.shared.MobyException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Pierre-Yves Chibon -- py@chibon.fr
 * source: http://www.totheriver.com/learn/xml/xmltutorial.html#6.1.2
 */
public class ParserXML extends DefaultHandler {

    /** Print additionnal output or not. */
    protected boolean debug = false;
    /** String containing the value until assigned. */
    protected String tempVal;

    /**
     * Default constructor.
     */
    public ParserXML() {
        super();
    }

    /**
     * Parse of document.
     * @param document String of the document name
     * @throws ParserConfigurationException When the parsing doesn't go
     * correctly
     * @throws SAXException When the parser fails
     * @throws IOException When there is a problem to read the file
     */
    public void parseDocument(final String document)
            throws ParserConfigurationException, SAXException, IOException {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();

        //get a new instance of parser
        SAXParser sp = spf.newSAXParser();

        //parse the file and also register this class for call backs
        sp.parse(document, this);

    }

    /**
     * Return the debug mode of the parser (true/false).
     * @return boolean debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Set the debug mode of the parser.
     * @param debug boolean switching on or off the debugging mode
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
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
    //Event Handlers
    @Override
    public void startElement(final String uri, final String localName,
            final String qName, final Attributes attributes)
            throws SAXException {
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        tempVal += new String(ch, start, length);
    }

    /**
     * Function called for each closing tag.
     * @param uri the Namespace URI, or the empty string if the element has no
     * Namespace URI or if Namespace processing is not being performed
     * @param localName the local name (without prefix), or the empty string if
     * Namespace processing is not being performed
     * @param qName the qualified name (with prefix), or the empty string if
     * qualified names are not available
     * @throws SAXException when something goes wrong
     */
    @Override
    public void endElement(final String uri, final String localName,
            final String qName) throws SAXException {
    }

    /**
     * Print the data.
     * @throws MobyException when something goes wrong.
     */
    public void printData() throws MobyException {
    }
}
