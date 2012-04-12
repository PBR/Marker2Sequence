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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.wur.plantbreeding.datatypes.Annotation;
import nl.wur.plantbreeding.logic.saxparser.ParserXMLGetElementAnnotation;
import nl.wur.plantbreeding.logic.saxparser.ParserXMLSifterAnnotation;
import nl.wur.plantbreeding.logic.soapclient.SoapClient;
import nl.wur.plantbreeding.logic.swtools.QueryRdf;
import nl.wur.plantbreeding.logic.util.FileName;
import nl.wur.plantbreeding.logic.xmlgenerator.XMLGeneratorGetAnnotation;

/**
 * This class handles the querying of the annotation from the web-services of
 * Cologn or Munich or from the virtuoso.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class AnnotationRetriever {

    /** Logger. */
    private static final Logger LOG =
                    Logger.getLogger(AnnotationRetriever.class.getName());
    /** The QueryRdf object used to run the query. */
    private final QueryRdf query = new QueryRdf();
    /** Base graph used for the queries. */
    private String basegraph;
    /** default URL to virtuoso. */
    private String endpoint = "http://sparql.plantbreeding.nl:8080/sparql/";

    /**
     * Constructor accepting the basegraph as parameter.
     * @param graph String used as basegraph in the class.
     */
    public AnnotationRetriever(String graph){
        this.basegraph = graph;
        query.setBasegraph(graph);
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
    }

    /**
     * Retrieve the GO term assigned by Sifter and made available by Cologn
     * through their web-service.
     * @param geneid the gene ID to query
     * @param ssl wether the web-service uses ssl encryption or not
     * @param debug print additionnal output or not
     * @return a list of Annotation object containning the GO term information
     * @throws Exception if something else goes wrong (write the file ?)
     */
    public final List<Annotation> getGoTermFromSifter(final String geneid,
                    final boolean ssl,
                    final boolean debug)
                throws Exception {
        // First Web-service:
        //  Retrieve the markers in between the two given markers
        final String xmlinput =
                XMLGeneratorGetAnnotation.generateXML(geneid, "MIPS_GE_Tomato");
        final SoapClient client = new SoapClient();
        client.setDebug(debug);
        final String name = "GetSifterPredictedFunctionTermsByProteinID";
        final String url = "http://bioinfo.mpiz-koeln.mpg.de/axis/services/"
                                + name;
        String outputfile = null;

        // Call the service, keep the output to memory
        // (given as input to the second ws)
        // and write it down to a file for the parsing
        try {
            final String filename = "ws-annot-mpiz-"
                    + FileName.generateFileNameByTime();
            outputfile = client.callService(name, url, xmlinput,
                            ssl, true, filename);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        // Parse the xml and extract the list of GeneticMarkers
        final ParserXMLSifterAnnotation xmlmarkers =
                    new ParserXMLSifterAnnotation();

        xmlmarkers.parseDocument(outputfile);

        return xmlmarkers.getAnnotations();
    }

    /**
     * Retrieve the gene information available in PlantDB and made available
     * by Munich through their web-service.
     * @param geneid the gene ID to query
     * @param ssl wether the web-service uses ssl encryption or not
     * @param debug print additionnal output or not
     * @return a list of Annotation object containning the GO term information
     * @throws Exception if something else goes wrong (write the file ?)
     */
    public final List<Annotation> getAnnotationFromPlantDBMunich(
                    final String geneid,
                    final boolean ssl,
                    final boolean debug)
            throws Exception {
        final String name = "getElementAnnotation";
        final String url = "http://mips.gsf.de/proj/plant/webapp/axis/services/"
                + name;
        final String xmlinput = XMLGeneratorGetAnnotation.generateXML(
                                            geneid, "MIPS_GE_Tomato", "input");
        final SoapClient client = new SoapClient();
        client.setDebug(debug);
        String outputfile = null;

        // Call the service, keep the output to memory (given as input
        // to the second ws) and write it down to a file for the parsing
        try {
            final String filename = "ws-annot-mips-"
                        + FileName.generateFileNameByTime();
            outputfile = client.callService(name, url, xmlinput,
                            ssl, true, filename);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        // Parse the xml and extract the list of GeneticMarkers
        final ParserXMLGetElementAnnotation xmlannot =
                    new ParserXMLGetElementAnnotation();
        xmlannot.setDebug(debug);

        xmlannot.parseDocument(outputfile);

        return xmlannot.getAnnotations();
    }

    /**
     * Retrieve the protein information from a gene id.
     * @param geneid the gene for which retrieve protein information
     * @return the list of hashmap containing the protein information
     * (ie: protein uri - protein ID - protein description - protein alternative
     * description - review status)
     */
    public final List<HashMap<String, String>> getProteinInfoForGeneId(
            final String geneid) {
        ArrayList<HashMap<String, String>> protinfo =
                query.getProteinInfoForGene(geneid);
        return protinfo;
    }

    /**
     * Retrieve the GO terms known in the sparql endpoint for a given gene and
     * add them to the given gotermlist.
     * @param geneid the ID of the gene ofr which the GO terms are searched
     * @param gotermlist the list to which adding the new GO terms
     * @return the list of go term enriched with new terms
     */
    public final ArrayList<HashMap<String, String>> getGoTermFromSparql(
            final String geneid,
            final ArrayList<HashMap<String, String>> gotermlist) {
        ArrayList<HashMap<String, String>> goinfo =
                query.getGoOfGene(geneid);
//        System.out.println(goinfo.size());
        for (HashMap<String, String> go : goinfo) {
            go.put("source", "itag");
            gotermlist.add(go);
        }
        return gotermlist;
    }

    /**
     * Retrieve the literature references known in the sparql endpoint for a
     * given gene.
     * @param geneid the ID of the gene for which the GO terms are searched
     * @return the list of all pubmed references found for the gene
     */
    public final ArrayList<HashMap<String, String>> getLiteratureFromSparql(
            final String geneid) {
        return query.getLiteratureOfGene(geneid);
    }

    /**
     * Retrieve the known pathway for the protein linked to the given gene
     * in the sparql endpoint.
     * @param geneid the ID of the gene for which pathway will be retrieved
     * @return the list of all pathway found
     */
    public final ArrayList<HashMap<String, String>> getPathwayFromSparql(
            final String geneid) {
        return query.getPathwayOfGene(geneid);
    }

    /**
     * Return the gene information into an Annotation object using sparql.
     * This query is used to generate the small table at the top of the
     * annotation page
     * @param geneid the String of the geneid to query.
     * @return an Annotation object containning the desired information
     */
    public final Annotation getGeneAnnotation(final String geneid) {
        return query.getGeneInfoInAnnotation(geneid);
    }
}
