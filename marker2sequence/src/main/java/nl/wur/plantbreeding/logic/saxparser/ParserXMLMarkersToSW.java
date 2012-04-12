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
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class is a trial to get an automatic conversion from XML element
 * to RDF.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class ParserXMLMarkersToSW extends ParserXML {

    /** String of the motif to search in the XML
     * (for example: moby:GeneticMarker). */
    private String motif;
    /** URI which is used while adding the data to the RDF graph. */
    protected String URI = "http://pbr.wur.nl/";
    /** RDF graph to add the information to. */
    protected Model model;
    /** HashMap containing the info get in the xml for the marker. */
    protected HashMap<String, String> markerinfo;
    /** string which contains the type of the information. */
    protected String tempType;
    
    /**
     * Resource to which the generated resource should be linked
     * to (if not null).
     */
    protected Resource linkresource = null;
    /**
     * property with which the generated resource should be linked
     * to (if not null).
     */
    protected Property linkproperty = null;

    /**
     * Returns the uri used while generating the RDF.
     * Default: http://pbr.wur.nl#
     * @return URI
     */
    public final String getUri() {
        return URI;
    }

    /**
     * Set the uri used while generating the RDF.
     * @param newuri the new URI
     */
    public final void setUri(final String newuri) {
        this.URI = newuri;
    }

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
     * Set the resource and the property to which the generate resource
     * will be link.
     * @param resource Resource to link to
     * @param property Property to link with
     */
    public final void linkResourceTo(final Resource resource,
            final Property property) {
        this.linkresource = resource;
        this.linkproperty = property;
    }

    /**
     * Set the resource and the property to which the generate resource
     * will be link.
     * @param resource Resource to link to
     * @param property String used to generate the property to link with
     * (full URI)
     */
    public final void linkResourceTo(final Resource resource,
            final String property) {
        Property prop = model.createProperty(property);
        this.linkResourceTo(resource, prop);
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
            markerinfo = new HashMap<String, String>();
            markerinfo.put("MarkerName", attributes.getValue("moby:id"));

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
            final String qName) throws SAXException {

        if ((qName.equalsIgnoreCase("moby:exceptionMessage"))) {
            throw new SAXException(tempVal);
        } else if (qName.equalsIgnoreCase(this.motif)) {
            // Add to the graph
            Resource marker = model.createResource(URI + "SGN-M"
                    + markerinfo.get("SGN-MarkerID"));
            marker.addProperty(RDF.type, URI);
            marker.addProperty(model.createProperty(URI + "MarkerName"),
                    markerinfo.get("MarkerName"));
            marker.addProperty(model.createProperty(URI + "Chromosome"),
                    markerinfo.get("Chromosome"));
            marker.addProperty(model.createProperty(URI + "mapPosition"),
                    markerinfo.get("position"));
            marker.addProperty(model.createProperty(URI + "SGN-ID"),
                    "SGN-M" + markerinfo.get("SGN-MarkerID"));

            // Handles the attributes
            tempType = null;
        } else if (tempType == null) {
            return;
        } else if (tempVal != null && !tempVal.trim().equals("")
                && tempType != null) {
            if (debug) {
                System.out.print("val: " + tempVal + " type: " + tempType);
            }
            markerinfo.put(tempType, tempVal);
        }
    }
}
