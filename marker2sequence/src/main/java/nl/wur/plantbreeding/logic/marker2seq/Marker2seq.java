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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.wur.plantbreeding.datatypes.GeneticMarkers;
import nl.wur.plantbreeding.exceptions.SeveralMappedPositionException;
import nl.wur.plantbreeding.logic.saxparser.ParserXMLMarkersToSW;
import nl.wur.plantbreeding.logic.soapclient.SoapClient;
import nl.wur.plantbreeding.logic.swtools.QueryRdf;
import nl.wur.plantbreeding.logic.util.FileName;
import nl.wur.plantbreeding.logic.xmlgenerator.XMLGeneratorMarkers;
import org.xml.sax.SAXException;

/**
 * This class handles the calling of the web-service and the generation of the
 * Jena model from the information given by the user.
 *
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class Marker2seq {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(
            Marker2seq.class.getName());
    /** URI used for reference. */
    private static String uri = "http://pbr.wur.nl/";
    /** List of GeneticMarkers. */
    private static List<GeneticMarkers> markerlist = null;
    /** default URL to virtuoso. */
    private String endpoint = "http://sparql.plantbreeding.nl:8080/sparql/";
    /** Name of the scaffold up to the chromosome. */
    private String scaffold = "SL2.31ch";
    /** The QueryRdf object used to run the query. */
    private final QueryRdf query = new QueryRdf();
    /** Base graph used for the queries. */
    private String basegraph;

    /**
     * Retrieve the marker list of this object.
     * @return a List of GeneticMarkers retrieved from the output of the
     * web-service
     */
    public final List<GeneticMarkers> getMarkerlist() {
        return markerlist;
    }

    /**
     * Return the endpoint used for querying when input is a Loci or a MapLoci.
     * @return a string of the endpoint's url which will be used to query
     */
    public final String getEndpoint() {
        return endpoint;
    }

    /**
     * Sets the endpoint used for querying when input is a Loci or a MapLoci.
     * @param endp a string of the url used for querying
     */
    public final void setEndpoint(final String endp) {
        this.endpoint = endp;
        query.setService(endp);
        LOG.log(Level.INFO, "Endpoint: {0}", this.endpoint);
    }

    /**
     * Return the base graph used in the queries.
     * @return a string of the base graph used in the queries.
     */
    public final String getBasegraph() {
        return this.basegraph;
    }

    /**
     * Sets the base graph used in the queries.
     * @param bg a string of the basic graph(s) to used for querying
     */
    public final void setBasegraph(final String bg) {
        this.basegraph = bg;
        query.setBasegraph(bg);
    }

    /**
     * For a given outputfile of the web-service
     * 'getMarkersForRegionByQueryMarkerList', parse the output and generate a
     * model out of it (and return this model).
     * @param outputfile a string containing the full name to the output file
     * @return a Jena model with the information returned by the web-service
     * @throws SAXException when the web-service returns an error
     */
    private Model getModelFromWSOutput(final String outputfile)
            throws SAXException {
        Model model = ModelFactory.createDefaultModel();

        final ParserXMLMarkersToSW parser = new ParserXMLMarkersToSW();
        parser.setModel(model);
        parser.setUri(uri + "MARKER#");

        // Parse the output and add it to the graph
        parser.setMotif("moby:GeneticMarker");
//        parser.setDebug(true);
        try {

            parser.parseDocument(outputfile);
        }
        catch (SAXException ex) {
            throw new SAXException(ex);
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, "Could not parse document : "
                    + outputfile, ex);
            return model;
        }

        // Retrieve the model containing all the (genetics) markers
        model = parser.getModel();

        return model;
    }

    /**
     * Call the web-services to extract the list of markers between the given
     * markers.
     * @param markerslist String array with the markers in the interval
     * @return String giving the full path of the output file
     */
    private String callWebservice(final String[] markerslist) {
        final SoapClient client = new SoapClient();
        String output = null;
        String outputfile = null;
        //  Retrieve the markers in between the two given markers
        final String xmlinput = XMLGeneratorMarkers.generateXML(markerslist);
        final String name = "getMarkersForRegionByQueryMarkerList";
        final String url = "https://www.eu-sol.wur.nl/axis/services/" + name;

        // Call the service, keep the output to memory (given as input to
        // the second ws) and write it down to a file for the parsing
        try {
            output = client.callService(name, url, xmlinput, true);
            final String filename = "ws1.1-"
                    + FileName.generateFileNameByTime();
            outputfile = client.writeFile(filename, output);
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return outputfile;
    }

    /**
     * For a given string representing comma separated marker names, this
     * function returns the model containning all information about all the
     * markers in the interval.
     *
     * @param markers a string of comma separated marker names
     * @return a Jena Model
     * throws SAXException when the web-service returns an error
     * @throws Exception when the markers map on several chromosome.
     */
    public final Model retrieveModel(final String markers)
            throws Exception {
        LOG.log(Level.INFO, "Got markers list from the URL, Markers: {0}",
                markers);

        String[] markerslist = markers.split(",");
        for (int i = 0; i < markerslist.length; i++) {
            markerslist[i] = markerslist[i].trim();
        }
        return this.retrieveModel(markerslist);
    }

    /**
     * For a given array of marker names, this function returns the model
     * containning all information about all the markers in the interval.
     *
     * @param markers an array of marker names
     * @return a Jena Model
     * throws SAXException when the web-service returns an error
     * @throws SAXException when the markers map on several chromosome.
     */
    public final Model retrieveModel(final String[] markers)
            throws SAXException {
        LOG.log(Level.INFO, "Markers array: {0}", markers[0]
                + ", " + markers[1]);
//        final String[] info = query.getChrAndPositionFromInputMarkers(markers);
//        return query.getModelFromInputMarkers(info);
        final String outputfile = this.callWebservice(markers);
        return this.getModelFromWSOutput(outputfile);

    }

    /**
     * For a given array of marker names, this function returns the model
     * containning all information about all the markers in the interval.
     *
     * @param markers an array of marker names
     * @return a Jena Model
     * throws SAXException when the web-service returns an error
     * @throws SeveralMappedPositionException when the markers map on several
     * chromosome.
     */
    public final Model retrieveModelFromVirtuoso(final String[] markers)
            throws SeveralMappedPositionException {
        LOG.log(Level.INFO, "Markers array: {0}", markers[0]
                + ", " + markers[1]);
        final String[] info = query.getChrAndPositionFromInputMarkers(markers);
        return query.getModelFromInputMarkers(info);
    }

    /**
     * For a given array of marker names, this function returns the model
     * containning all information about all the markers in the interval.
     *
     * @param markers an array of marker names
     * @param chr the chromosome number
     * @return a Jena Model
     * throws SAXException when the web-service returns an error
     * @throws SeveralMappedPositionException when the markers map on several
     * chromosome.
     */
    public final Model retrieveModel(final String[] markers, final String chr)
            throws SeveralMappedPositionException {
        LOG.log(Level.INFO, "Markers array: {0}", markers);

        final String[] info = query.getPositionFromInputMarkers(markers, chr);
        return query.getModelFromInputMarkers(info);
    }

    /**
     * For a given physical interval, this function returns the model
     * containning all information about all the markers in the interval.
     * 
     * @param locistring a String giving the interval to investigate in a
     * gbrowse like format (ie: SL2.31ch06:42289201..42292100)
     * @return a Jena Model with the information returned by the web-service
     * throws SAXException when the web-service returns an error
     * @throws SeveralMappedPositionException when the markers map on several
     * chromosome.
     */
    public final Model retrieveModelFromLoci(final String locistring)
            throws SeveralMappedPositionException {
        LOG.log(Level.INFO, "Loci string: {0}", locistring);
        final String[] loci = locistring.trim().split(",");
        String[] markers = null;
        String chr = "";

        for (String locus : loci) {
            chr = locus.split(":")[0].trim();
            final String[] pos = locus.split(":")[1].split("\\.\\.");
            markers = query.getClosestMarkers(chr, pos[0].trim(),
                    pos[1].trim());
        }
        LOG.log(Level.INFO, "Number of markers retrieved: {0} ",
                markers.length);
        if (markers.length == 0) {
            return ModelFactory.createDefaultModel();
        } else {
            chr = new Integer(chr.split("ch")[1]).toString();
            return this.retrieveModel(markers, chr);
        }
    }

    /**
     * For a given genetic interval, this function returns the model
     * containning all information about all the genes in the interval and their
     * associated GO term.
     * @param maplocistring a String giving the interval to investigate in a
     * gbrowse like format (ie: chr06:10..50)
     * @return a Jena Model with the information returned by the web-service
     * throws SAXException when the web-service returns an error
     * @throws SeveralMappedPositionException when the markers map on several
     * chromosome.
     */
    public final Model retrieveModelFromMapLoci(final String maplocistring)
            throws SeveralMappedPositionException, SAXException {
        LOG.log(Level.INFO, "Map loci: {0}", maplocistring);
        String[] markers = null;
        final String chr = maplocistring.split(":")[0].split("chr")[1];
        final String[] pos = maplocistring.split(":")[1].split("\\.\\.");
        markers = query.getClosestMarkersFromMap(chr, pos[0], pos[1]);
        LOG.log(Level.INFO, "Number of markers retrieved: {0} ",
                markers.length);
        return this.retrieveModel(markers);
    }

    /**
     * Add additionnal info to the current model containing only the markers
     * information. This methods add information about the markers and the genes
     * to the given model and return this new model.
     * @param model a Jena Model containing markers information from the
     * web-service or the virtuoso.
     * @param locus the locus string given as input used when no information
     * could be retrieved using the neighbooring markers. The string should be
     * of the form: SL2.31ch06:start..stop
     * @return a Jena Model with additionnal information about the genes and the
     * markers.
     */
    public final Model addInfoToModel(Model model, final String locus)
            throws NumberFormatException {
        if (model.isEmpty() && locus != null) {
            LOG.log(Level.INFO, "No genomic marker found in this interval,"
                    + "try to  use locus information");
            final String chr = "http://pbr.wur.nl/SCAFFOLD#"
                    + locus.trim().split(":")[0];
            final String[] stringpos = locus.trim().split(":")[1].split("\\.\\.");
            final int[] pos = {Integer.parseInt(stringpos[0].trim()),
                Integer.parseInt(stringpos[1].trim())};
            model = query.getMarkerInfo(model, chr, pos);
            model = query.getGeneInfo(model, chr, pos);
        } else {
            LOG.log(Level.INFO, "Add gene and marker information for known"
                    + " marker");
            Marker2seq.markerlist = query.getGeneticMap(model);
            LOG.log(Level.INFO, "markers: {0}", Marker2seq.markerlist.size());
            // Add marker and position for all markers
            model = query.getMarkerInfo(model, Marker2seq.markerlist);
            LOG.log(Level.INFO, "model: {0}", model.size());
            // Add genes within the borders
            model = query.getGenesInfo(model);
            LOG.log(Level.INFO, "model: {0}", model.size());
        }
        return model;
    }

    public String getLociString(String[] markers)
            throws SeveralMappedPositionException {
        LOG.log(Level.INFO, "Retrieve max interval for markers : {0}", markers);
        String[] positions = query.getPhysicalChrAndPositionFromInputMarkers(markers);
        String out = "";
        if (positions.length > 0) {
            out = out + positions[0].split("#")[1];
            out = out + ":" + positions[1];
            out = out + ".." + positions[2];
        }

        LOG.log(Level.INFO, "Loci string generated: {0} ", out);
        return out;
    }
}
