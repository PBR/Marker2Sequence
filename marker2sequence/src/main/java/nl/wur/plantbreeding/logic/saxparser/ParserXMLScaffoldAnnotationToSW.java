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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class is a trial to get an automatic conversion from XML element
 * to RDF.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class ParserXMLScaffoldAnnotationToSW extends ParserXML {

    /** String of the motif to search in the XML
     * (for example: moby:GeneticMarker). */
    private String motif;
    /** URI which is used while adding the data to the RDF graph. */
    protected String URI = "http://pbr.wur.nl/";
    /** RDF graph to add the information to. */
    protected Model model;
    /** scaffold for which we retrieve information. */
    protected Resource scaffold;
    /** feature available for a scaffold. */
    protected Resource feature;
    /** string which contains the type of the information. */
    protected Property tempType;

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
     * Get the Model in which are added the information
     * retrieved from the web-service.
     * @return Model model
     */
    public final Model getModel() {
        return model;
    }

    /**
     * Set the model in which are added the information
     * retrieved from the web-service.
     * @param newmodel Model
     */
    public final void setModel(final Model newmodel) {
        this.model = newmodel;
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
                System.out.print("New instance: " + qName + "  ");
                System.out.println(attributes.getValue("moby:id"));
            }
            scaffold = model.createResource(URI + "SCAFFOLD#"
                    + attributes.getValue("moby:id"));
            scaffold.addProperty(RDF.type, URI + "SCAFFOLD#");
            scaffold.addProperty(tempType, tempVal);

            if (debug) {
                System.out.println(scaffold.toString());
            }

            tempType = null;
        } else {
            tempType = model.createProperty(URI
                    + attributes.getValue("moby:articleName"));
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
            final String qName) throws SAXException {

        if (qName.equalsIgnoreCase("moby:exceptionMessage")) {
            throw new SAXException(tempVal);
        } else if (qName.equalsIgnoreCase(this.motif)) {
            // Add to the graph
            if (feature != null) {
                scaffold.addProperty(model.createProperty(URI
                        + "HasFeature"), feature);
            }
            // Handles the attributes
            tempType = null;
        } else if (tempType == null) {
            return;
        } else if (tempType.toString().
                equals("http://pbr.wur.nl/FeatureName")) {
            if (tempVal.startsWith("SL")) {
                feature = model.createResource(URI + "GENE#" + tempVal + ".1");
                feature.addProperty(RDF.type, URI + "GENE#");
                feature.addProperty(tempType, tempVal + ".1");
            } else {
                feature = model.createResource(URI + "MARKER#" + tempVal);
                feature.addProperty(RDF.type, URI + "MARKER#");
                feature.addProperty(tempType, tempVal);
            }
            if (debug) {
                System.out.println("feature: " + feature.toString());
            }
        } else if (tempVal != null && !tempVal.trim().equals("")
                && tempType != null) {
            if (debug) {
                System.out.print("val: " + tempVal + " type: " + tempType);
            }
            if (feature != null) {
                feature.addProperty(tempType, tempVal);
            }
        }
    }
}
