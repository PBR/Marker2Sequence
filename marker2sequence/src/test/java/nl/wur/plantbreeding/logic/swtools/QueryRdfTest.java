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

package nl.wur.plantbreeding.logic.swtools;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.net.InetAddress;
import nl.wur.plantbreeding.datatypes.MarkerSequence;
import nl.wur.plantbreeding.datatypes.Markerws;
import nl.wur.plantbreeding.datatypes.Annotation;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import nl.wur.plantbreeding.logic.saxparser.ParserXMLMarkersToSW;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.wur.plantbreeding.logic.soapclient.SoapClient;
import nl.wur.plantbreeding.logic.util.FileName;
import nl.wur.plantbreeding.logic.xmlgenerator.XMLGeneratorMarkers;
import com.hp.hpl.jena.rdf.model.Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nl.wur.plantbreeding.datatypes.GeneticMarkers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Pierre-Yves Chibon
 */
public class QueryRdfTest {

    /** Internal endpoint to query during the tests. */
    private String endpoint = "http://sparql.plantbreeding.nl:8080/sparql";
    /** Name of the chr to use in the tests. */
    private final String chr = "SL2.31ch06";
    /** Name of the scaffold used in the tests. */
    private final String scaffoldname = "http://pbr.wur.nl/SCAFFOLD#" + chr;
    /** Start and stop position of the interval looked in the scaffold. */
    private final int[] ext = {42268851, 42317124};
    /** Gene for which information are retrieved for the tests. */
    private final String geneid = "Solyc04g079880.1.1";
    /** The QueryRdf object used to run the query. */
    private final QueryRdf instance = new QueryRdf();

    /**
     * Constructor, sets the endpoint to use.
     */
    public QueryRdfTest() {
    }

    /**
     * Check wether a given url is available or not.
     * @param url a string of the url to test
     * @return a boolean true if the url is reachable, false otherwise
     */
    private boolean checkUrl(final String url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            //HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            return ( con.getResponseCode() == HttpURLConnection.HTTP_OK );
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Browse all the network interface, retrieve all the IPs and checks if
     * one of them is in the 137.224 or 10.73 range.
     * Returns true if it is, false otherwise.
     * @return a boolean showing if the run if perform in the allowed network
     * range.
     * @throws SocketException if something goes wrong while trying to retrieve
     * the IP address.
     */
    private boolean allowedNetwork() throws SocketException {
        boolean allowed = false;
        Enumeration e = NetworkInterface.getNetworkInterfaces();

        while (e.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) e.nextElement();
//            System.out.println("Net interface: " + ni.getName());

            Enumeration e2 = ni.getInetAddresses();

            while (e2.hasMoreElements()) {
                InetAddress ipad = (InetAddress) e2.nextElement();
                String ip = ipad.toString();
//                System.out.println("IP address: " + ip);
                if (ip.startsWith("/137.224") || ip.startsWith("/10.73") ||
                        ip.startsWith("/192.168.41")) {
                    allowed = true;
                }
            }
        }
        return allowed;
    }

    /**
     *
     */
    @Before
    public final void setUp() throws SocketException {
        
        if (this.allowedNetwork()) {
            System.out.println("Using endpoint: " + endpoint);
            if (!checkUrl(endpoint)) {
                endpoint = "http://sparql-r:8890/sparql/";
                System.out.println("Fallback to: " + endpoint);
                if (!checkUrl(endpoint)) {
                    endpoint = null;
                    System.out.println("Both urls are not reachable -- tests fail");
                }
            }
        } else {
            System.out.println("Ip not in the allowed range");
            endpoint = null;
        }
        if (endpoint == null){
            assertNotNull(endpoint);
        }
        instance.endpoint = endpoint;
    }

    /**
     *
     */
    @After
    public final void tearDown() {
    }

    /**
     * Test of getGeneList method, of class QueryRdf.
     */
    @Test
    public final void testGetGeneList() {
        System.out.println("getGeneList");
        Model model = this.generateModel();
        model = instance.getGeneInfo(model, scaffoldname, ext);

        final int expResult = 10;
        final ArrayList<Annotation> result = instance.getGeneList(model);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getAnnotationList method, of class QueryRdf.
     */
    @Test
    public final void testGetAnnotationList() {
        System.out.println("getAnnotationList");
        Model model = this.generateModel();
        model = instance.getGeneInfo(model, scaffoldname, ext);

        int expResult = 10;
        final ArrayList<Annotation> result = instance.getAnnotationList(model);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getPhysicalMap method, of class QueryRdf.
     */
    @Test
    public final void testGetPhysicalMap() {
        System.out.println("getPhysicalMap");
        Model model = this.generateModel();
        model = instance.getGeneInfo(model, scaffoldname, ext);
        model = instance.getMarkerInfo(model, this.generateGeneticMarkers());

        int expResult = 2; // was 1
        final ArrayList<Markerws> result = instance.getPhysicalMap(model);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getGeneticMap method, of class QueryRdf.
     */
    @Test
    public final void testGetGeneticMap() {
        System.out.println("getGeneticMap");
        Model model = this.generateModel();
        int expResult = 87;
        final List<GeneticMarkers> result = instance.getGeneticMap(model);
        assertEquals(expResult, result.size());

    }

    /**
     * Test of getMarkerSequence method, of class QueryRdf.
     */
    @Test
    public final void testGetMarkerSequence() {
        System.out.println("getMarkerSequence");
        Model model = this.generateModel();
        model = instance.getGeneInfo(model, scaffoldname, ext);
        model = instance.getMarkerInfo(model, this.generateGeneticMarkers());

        int expResult = 1;
        final ArrayList<MarkerSequence> result =
                instance.getMarkerSequence(model);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getMarkerScaffold method, of class QueryRdf.
     */
    @Test
    public final void testGetMarkerScaffold() {
        System.out.println("getMarkerScaffold");
        Model model = this.generateModel();
        model = instance.getMarkerInfo(model, this.generateGeneticMarkers());

        final int expResult = 2; // was 1
        final ArrayList<Markerws> result =
                instance.getMarkerScaffold(model, scaffoldname);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getRestrictedModel method, of class QueryRdf.
     * @throws IOException when something goes wrong while trying to write
     */
    @Test
    public final void testGetRestrictedModel() throws IOException {
        System.out.println("getRestrictedModel");
        Model model = this.generateModel();
        final String kw = "Lycopene";
        model = instance.getGeneInfo(model, scaffoldname, ext);
        model = instance.getMarkerInfo(model, this.generateGeneticMarkers());

        final long expResult = 31; // was 38
        final Model result = instance.getRestrictedModel(model, kw);

        assertEquals(expResult, result.size());
    }

    /**
     * Test of askGene method, of class QueryRdf.
     */
    @Test
    public final void testAskGene() {
        System.out.println("askGene");
        final Model model = this.generateModel();
        final boolean expResult = false;
        final boolean result = instance.askGene(geneid, model);
        assertEquals(expResult, result);
    }

    /**
     * Test of getGoUri method, of class QueryRdf.
     */
    @Test
    public final void testGetGoUri() {
        System.out.println("getGoUri");
        Model model = this.generateModel();
        model = instance.getGeneInfo(model, scaffoldname, ext);

        final int expResult = 7; // was 11
        final ArrayList<String> result = instance.getGoUri(model);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getGoDescribeQuery method, of class QueryRdf.
     */
    @Test
    public final void testGetGoDescribeQuery() {
        System.out.println("getGoDescribeQuery");
        final Model model = this.generateModel();
        final String result = instance.getGoDescribeQuery(model);
        assertNotNull(result);
    }

    /**
     * Test of getMarkerInfo method, of class QueryRdf.
     */
    @Test
    public final void testGetMarkerInfo() {
        System.out.println("getMarkerInfo");
//        Model model = null;
//        List<GeneticMarkers> markerlist = null;
//        Model expResult = null;
//        Model result = instance.getMarkerInfo(model, markerlist);
//        assertEquals(expResult, result);
//  // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");


    }

    /**
     * Test of getGenesInfo method, of class QueryRdf.
     */
    @Test
    public final void testGetGenesInfo() {
        System.out.println("getGenesInfo");
        final Model model = this.generateModel();
        final long expResult = 435;
        final Model result = instance.getGenesInfo(model);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getStartAndStopPosition method, of class QueryRdf.
     */
    @Test
    public final void testGetStartAndStopPosition() {
        System.out.println("getStartAndStopPosition");
        Model model = this.generateModel();
        model = instance.getMarkerInfo(model, this.generateGeneticMarkers());

        final int[] expResult = {45309139, 45310269};
        final int[] result =
                instance.getStartAndStopPosition(model, scaffoldname);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getGeneInfo method, of class QueryRdf.
     */
    @Test
    public final void testGetGeneInfo() {
        System.out.println("getGeneInfo");
        final Model model = this.generateModel();
        final long expResult = 3424; // was 3337; // was 2612
        final Model result = instance.getGeneInfo(model, scaffoldname, ext);
        assertEquals(expResult, result.size());


    }

    /**
     * Test of getGoDescribeInfo method, of class QueryRdf.
     */
    @Test
    public final void testGetGoDescribeInfo() {
        System.out.println("getGoDescribeInfo");
        Model model = this.generateModel();
        model = instance.getGeneInfo(model, scaffoldname, ext);

        final long expResult = 4475; // was 4300; // was 5910
        final Model result = instance.getGoDescribeInfo(model);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getGeneDescriptionInModel method, of class QueryRdf.
     */
    @Test
    public final void testGetGeneDescriptionInModel_Model_int() {
        System.out.println("getGeneDescriptionInModel");
        Model model = this.generateModel();
        final int limit = 50;
        model = instance.getGeneInfo(model, scaffoldname, ext);

        final long expResult = 50; // was 51
        final Model result = instance.getGeneDescriptionInModel(model, limit);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getGeneDescriptionInModel method, of class QueryRdf.
     */
    @Test
    public final void testGetGeneDescriptionInModel_Model() {
        System.out.println("getGeneDescriptionInModel");
        Model model = this.generateModel();
        final long expResult = 50; // was 51
        model = instance.getGeneInfo(model, scaffoldname, ext);
        final Model result = instance.getGeneDescriptionInModel(model);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getClosestMarkers method, of class QueryRdf.
     */
    @Test
    public final void testGetClosestMarkers() {
        System.out.println("getClosestMarkers");
        final String start = "42268851";
        final String stop = "42317124";
        final String[] expResult = {"cLET-19-J2", "C2_At1g08370", "Bcyc_868",
            "Bcyc_Promoter_SNP1", "Bcyc_Promoter_SNP3", "Bcyc_Promoter_SNP2",
            "seq-rs3627", "T0055"};
        final String[] result = instance.getClosestMarkers(chr, start, stop);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getClosestMarkersFromMap method, of class QueryRdf.
     */
    @Test
    public final void testGetClosestMarkersFromMap() {
        System.out.println("getClosestMarkersFromMap");
        //TODO: implement when the code will be working
//        final String chr = "6";
//        final String start = "0";
//        final String stop = "15";
//        final int expResult = 4;
//    al String[] result = instance.getClosestMarkersFromMap(chr, start, stop);
//        assertEquals(expResult, result.length);


    }

    /**
     * Test of getGoNameSpaceDistribution method, of class QueryRdf.
     */
    @Test
    public final void testGetGoNameSpaceDistribution() {
        System.out.println("getGoNameSpaceDistribution");
        Model model = this.generateModel();
        model = instance.getGeneInfo(model, scaffoldname, ext);

        final int expResult = 2;
        final HashMap<String, Integer> result =
                instance.getGoNameSpaceDistribution(model);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getGoSynonymDistribution method, of class QueryRdf.
     */
    @Test
    public final void testGetGoSynonymDistribution() {
        System.out.println("getGoSynonymDistribution");
        Model model = this.generateModel();
        model = instance.getGeneInfo(model, scaffoldname, ext);

        final int expResult = 6; // was 14
        final HashMap<String, Integer> result =
                instance.getGoSynonymDistribution(model);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getGoDistribution method, of class QueryRdf.
     */
    @Test
    public final void testGetGoDistribution() {
        System.out.println("getGoDistribution");
        Model model = this.generateModel();
        model = instance.getGeneInfo(model, scaffoldname, ext);

        final int expResult = 3; // was 4
        final HashMap<String, Integer> result =
                instance.getGoDistribution(model);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getProteinInfoForGene method, of class QueryRdf.
     */
    @Test
    public final void testGetProteinInfoForGene() {
        System.out.println("getProteinInfoForGene");
        final int expResult = 12;
        ArrayList<HashMap<String, String>> result =
                instance.getProteinInfoForGene(geneid);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getGoOfGene method, of class QueryRdf.
     */
    @Test
    public final void testGetGoOfGene() {
        System.out.println("getGoOfGene");
        final int expResult = 3; // was 2
        ArrayList<HashMap<String, String>> result =
                instance.getGoOfGene(geneid);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getLiteratureOfGene method, of class QueryRdf.
     */
    @Test
    public final void testGetLiteratureOfGene() {
        System.out.println("getLiteratureOfGene");
        final int expResult = 20; // was 18;
        ArrayList<HashMap<String, String>> result =
                instance.getLiteratureOfGene(geneid);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getPathwayOfGene method, of class QueryRdf.
     */
    @Test
    public final void testGetPathwayOfGene() {
        System.out.println("getPathwayOfGene");
        int expResult = 1;
        ArrayList<HashMap<String, String>> result =
                instance.getPathwayOfGene(geneid);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getGeneInfoInAnnotation method, of class QueryRdf.
     */
    @Test
    public final void testGetGeneInfoInAnnotation() {
        System.out.println("getGeneInfoInAnnotation");
        Integer start = 61791186;
        String desc = "1-acyl-sn-glycerol-3-phosphate acyltransferase";
        Annotation result = instance.getGeneInfoInAnnotation(geneid);
        assertEquals(start, result.getSeq_position_start());
        assertTrue(result.getDescription().startsWith(desc));
    }

    /**
     * Test of getModelFromInputMarkers method, of class QueryRdf.
     * @throws Exception When something goes wrong
     */
    @Test
    public final void testGetModelFromInputMarkers() throws Exception {
        System.out.println("getModelFromInputMarkers");
        final String[] markers = {"TG253", "TG314"};
        final String[] info =
                instance.getChrAndPositionFromInputMarkers(markers);
        final long expResult = 270;
        final Model result = instance.getModelFromInputMarkers(info);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getChrAndPositionFromInputMarkers method, of class QueryRdf.
     * @throws Exception When something goes wrong
     */
    @Test
    public final void testGetChrAndPositionFromInputMarkers() throws Exception {
        System.out.println("getChrAndPositionFromInputMarkers");
        String[] markers = {"TG253", "TG314"};
        String[] expResult = {"6", "55", "101"};
        String[] result = instance.getChrAndPositionFromInputMarkers(markers);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getGeneAssociatedWithKw method, of class QueryRdf.
     */
    @Test
    public final void testGetGeneAssociatedWithKw() {
        System.out.println("getGeneAssociatedWithKw");
        String kw = "carotene";
        final long expResult = 5873; // was 11504; Oo
        Model result = instance.getGeneAssociatedWithKw(kw);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getProteineAssociatedWithKw method, of class QueryRdf.
     */
    @Test
    public final void testGetProteineAssociatedWithKw() {
        System.out.println("getProteineAssociatedWithKw");
        String kw = "carotene";
        final long expResult = 116; // was 118; Oo
        Model result = instance.getProteineAssociatedWithKw(kw);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getPathwayAssociatedWithKw method, of class QueryRdf.
     */
    @Test
    public final void testGetPathwayAssociatedWithKw() {
        System.out.println("getPathwayAssociatedWithKw");
        String kw = "carotene";
        final long expResult = 126; // was 155; Oo
        Model result = instance.getPathwayAssociatedWithKw(kw);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getGeneAssociatedWithKwFromAnnotation method, of class QueryRdf.
     */
    @Test
    public final void testGetGeneAssociatedWithKwFromAnnotation() {
//        System.out.println("getGeneAssociatedWithKwFromAnnotation");
//        String kw = "carotene";
//        final long expResult = 7966;
//        Model result = instance.getGeneAssociatedWithKwFromAnnotation(kw);
//        assertEquals(expResult, result.size());
    }

    /**
     * Generate a Jena Model which should enable us to test some methods here.
     * @return a Jena Model to test our functions
     */
    private Model generateModel() {
        final String[] markerslist = {"TG253", "TG279", "TG314"};
        final SoapClient client = new SoapClient();
        String output = null;
        String outputfile = null;
        final String uri = "http://pbr.wur.nl/";

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
            Logger.getLogger(QueryRdfTest.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

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
        catch (Exception ex) {
            Logger.getLogger(QueryRdfTest.class.getName()).log(Level.SEVERE,
                    "Could not parse document : " + outputfile, ex);
        }

        // Retrieve the model containing all the (genetics) markers
        model = parser.getModel();
        return model;
    }

    /**
     * Generate a list of GeneticMarkers for testing.
     * @return an ArrayList of GeneticMarkers
     */
    private ArrayList<GeneticMarkers> generateGeneticMarkers() {
        final ArrayList<GeneticMarkers> markerlist =
                new ArrayList<GeneticMarkers>();
        final GeneticMarkers gmk = new GeneticMarkers();
        gmk.setId("SGN-M167");
        gmk.setName("TG253");
        markerlist.add(gmk);

        gmk.setId("SGN-M216");
        gmk.setName("TG279");
        markerlist.add(gmk);

        gmk.setId("SGN-M130-2");
        gmk.setName("TG314");
        markerlist.add(gmk);

        return markerlist;

    }
}
