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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.Arrays;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Rather complex class. It extends ParserXMLtoSW but since it is mostly used
 * when the parent has already been generated, it uses delegation to override
 * the different functions of the parental class to use the parent given in
 * the constructor.
 *
 * This class handles the conversion from XML to RDF of the element XML
 * contained in the MIPS element returned by their getElement web-service.
 *
 * TODO: Here we do not deal with subelement, should we ?
 *
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class ParserMipsElementToSw extends ParserXMLtoSW {

    /** String of the motif to search in the XML
     * (for example: moby:GeneticMarker). */
    private String motif;
    /** Gene ID. */
    private String geneid;
    /** parser from xml to semantic web. */
    private ParserXMLtoSW parser = new ParserXMLtoSW();
    /** Wether to block or not. */
    private boolean block = false;
    /** Element ignored. */
    private List<String> blacklist =
            Arrays.asList(new String[]{"xref", "subelement", "synonym"});

    /**
     * Convert the extracted content from the getElement to RDF.
     */
    public ParserMipsElementToSw() {
    }

    /**
     * Constructor setting a given ParserXMLtoSW parser.
     * @param parserws a ParserXMLtoSW object
     */
    public ParserMipsElementToSw(final ParserXMLtoSW parserws) {
        this.parser = parserws;
    }

    /**
     * Returns the uri used while generating the RDF.
     * Default: http://pbr.wur.nl#
     * @return this.parser.getUri()
     */
    @Override
    public final String getUri() {
        return this.parser.getUri();
    }

    /**
     * Set the uri used while generating the RDF.
     * @param uri a String of the URI
     */
    @Override
    public final void setUri(final String uri) {
        this.parser.setUri(uri);
    }

    /**
     * Get the motif searched in the XML.
     * @return this.parser.getMotif()
     */
    @Override
    public final String getMotif() {
        return this.parser.getMotif();
    }

    /**
     * Set the motif searched in the XML in the startElement function.
     * @param newmotif String of the motif to search in the XML
     */
    @Override
    public final void setMotif(final String newmotif) {
        this.parser.setMotif(newmotif);
    }

    /**
     * Get the Model in which are added the information
     * retrieved from the web-service.
     * @return Model model
     */
    @Override
    public final Model getModel() {
        return this.parser.getModel();
    }

    /**
     * Set the model in which are added the information
     * retrieved from the web-service.
     * @param model Model
     */
    @Override
    public final void setModel(final Model model) {
        this.parser.setModel(model);
    }

    /**
     * Returns the geneid for which information was retrieved.
     * @return geneid
     */
    public final String getGeneid() {
        return geneid;
    }

    /**
     * Set the geneid for which information are retrieved.
     * @param newgeneid a new String of Gene ID
     */
    public final void setGeneid(final String newgeneid) {
        this.geneid = newgeneid;
    }

    /**
     * Set the resource and the property to which the generate resource
     * will be link.
     * @param resource Resource to link to
     * @param property Property to link with
     */
    @Override
    public final void linkResourceTo(final Resource resource,
            final Property property) {
        this.parser.linkResourceTo(resource, property);
    }

    /**
     * Set the resource and the property to which the generate resource
     * will be link.
     * @param resource Resource to link to
     * @param property String used to generate the property to link with
     * (full URI)
     */
    @Override
    public final void linkResourceTo(final Resource resource,
            final String property) {
        this.parser.linkResourceTo(resource, property);
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
        if (debug) {
            System.out.println("New instance: " + qName + "\t" + block);
        }
        if (blacklist.contains(qName)) {
            this.block = true;
        } else {
            //TODO: consider if we should not change the type of this entry
            // to the type returned in the xml (ie: transcript_ITAG_
            gene = this.parser.model.createResource(this.parser.getUri()
                    + geneid);
            gene.addProperty(RDF.type, this.parser.getUri());

            if (this.linkresource != null && this.linkproperty != null) {
                linkresource.addProperty(linkproperty, gene);
            }

            tempType = this.parser.model.createProperty(this.parser.getUri()
                    + qName.toString());
        }
    }

    /**
     * Function called for each closing tag.
     * @param uri a String
     * @param localName a String
     * @param qName the closing tag
     * @throws SAXException when something goes wrong
     */
    @Override
    public final void endElement(final String uri, final String localName,
            final String qName) throws SAXException {

        if ((qName.equalsIgnoreCase("moby:exceptionMessage"))) {
            throw new SAXException(tempVal);
        } else if (!tempVal.trim().isEmpty() && !block && tempType != null) {
            if (debug){
                System.out.println("val: " + tempVal.trim()
                        + "  type: " + tempType);
            }
            gene.addProperty(tempType, tempVal);
        }
        if (blacklist.contains(qName)) {
            this.block = false;
        }
        tempVal = "";
    }
}
