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

import java.net.HttpURLConnection;
import java.net.URL;
import com.hp.hpl.jena.rdf.model.Model;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 * These tests are testing the querying of the first web-service used by M2S
 * from which information are retrieved and stored in a Jena Model.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class Marker2seqTest {

    /** Default internal endpoint used, changes if needed in the constructor. */
    private String endpoint = "http://sparql.plantbreeding.nl:8080/sparql";

    /** The QueryRdf object used to run the query. */
    private final Marker2seq instance = new Marker2seq();

    /**
     * Constructor.
     * Sets the endpoint to use to retrieve the data from.
     */
    public Marker2seqTest() {
    }

    /**
     * Check wether a given url is available or not.
     * @param url a string of the url to test
     * @return a boolean true if the url is reachable, false otherwise
     */
    private final boolean checkUrl(String url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            //HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Before
    public void setUp() {
        System.out.println("Using endpoint: " + endpoint);
        if (!checkUrl(endpoint)) {
            // Allow the project to run on our local jenkins
            endpoint = "http://sparql-r:8890/sparql/";
            System.out.println("Fallback to: " + endpoint);
            if (!checkUrl(endpoint)) {
                endpoint = "";
                System.out.println("Both urls are not reachable -- tests fail");
            }
        }
        instance.setEndpoint(endpoint);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of retrieveModel method, of class Marker2seq.
     * throws SAXException when the web-service returns an error
     * @throws Exception when the markers map on different chromosome
     */
    @Test
    public final void testRetrieveModel_String() throws Exception {
        System.out.println("retrieveModel");
        final String markers = "TG253,CT109";
        final long expResult = 285;
        // 4- was 371 while using virtuoso
        // 3- was 285 I kicked out the call to the ws
        // 2-was 32722 I removed gene and markers information from the model
        // 1-was 38187
        final Model result = instance.retrieveModel(markers);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of retrieveModel method, of class Marker2seq.
     * throws SAXException when the web-service returns an error
     * @throws Exception when the markers map on different chromosome
     */
    @Test
    public final void testRetrieveModel_StringArr() throws Exception {
        System.out.println("retrieveModel");
        final String[] markers = {"TG253", "CT109"};
        final long expResult = 285;
        // 4- was 371 while using virtuoso
        // 3- was 285 I kicked out the call to the ws
        // 2-was 32722 I removed gene and markers information from the model
        // 1-was 38187
        final Model result = instance.retrieveModel(markers);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of retrieveModelFromLoci method, of class Marker2seq.
     * @throws SAXException when the web-service returns an error
     */
    @Test
    public final void testRetrieveModelFromLoci() throws Exception {
        System.out.println("retrieveModelFromLoci");
        final String locistring = "SL2.31ch06:42268851..42300000";
        final long expResult = 84;
        // 3- was 215 
        // 2-was 45057 I removed gene and markers information from the model
        // 1-was 48404
        final Model result = instance.retrieveModelFromLoci(locistring);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of retrieveModelFromMapLoci method, of class Marker2seq.
     */
    @Test
    public final void testRetrieveModelFromMapLoci() {
        System.out.println("retrieveModelFromMapLoci");
        System.out.println("Not implemented yet");
        //TODO: implement this case
//        String maplocistring = "";
//        Marker2seq instance = new Marker2seq();
//        Model expResult = null;
//        Model result = instance.retrieveModelFromMapLoci(maplocistring);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the
//        // default call to fail.
//        fail("The test case is a prototype.");
    }

}