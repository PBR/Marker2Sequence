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

package nl.wur.plantbreeding.logic.soapclient;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.wur.plantbreeding.logic.util.FileOrDirectoryExists;

import org.tulsoft.tools.soap.axis.AxisCall;
import org.apache.axis.client.Call;

/**
 * SoapClient is the class handling the web-service invocation.
 *
 * Some documentations:
 * <a href="http://ws.apache.org/axis/java/user-guide.html">axis user guide</a>
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class SoapClient {

    /**
     * Timeout used by transport sender in milliseconds.
     */
    private int TIMEOUT = 30000;
    /**
     * Place of the keystore used for ssl identification.
     */
    private String keystore = System.getProperty("user.home").replace('\\', '/'
                ) + "/breedb/cert";
    /**
     * Boolean to turn-on/off extra output.
     */
    private boolean debug = false;
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(
                            SoapClient.class.getName());

    /**
     * Write to the system the file named outfile containing
     * the string content
     * @param outfile the name of the file wo which write
     * @param content the content of the file
     * @return the full path of the file
     * @throws IOException when something happens while writing
     */
    public String writeFile(String outfile, final String content)
            throws IOException {

        if (!outfile.startsWith(System.getProperty("java.io.tmpdir"))) {
            outfile = System.getProperty("java.io.tmpdir") + "/" + outfile;
        }
        if (debug) {
            LOG.log(Level.INFO, "Write file: {0}", outfile);
        }
        final FileWriter fstream = new FileWriter(outfile);
        final BufferedWriter outbuf = new BufferedWriter(fstream);
        outbuf.write(content);
        outbuf.close();

        return outfile;
    }

    /**
     * Sets the debug mode of this class.
     * @param debug the boolean to switch to debug mode or not
     */
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    /**
     * Sets the timeout in milliseconds for the web-service.
     *  default to 30000 (30s)
     * @return the int of the time-out parameter
     */
    public int getTIMEOUT() {
        return TIMEOUT;
    }

    /**
     * Gets the timeout used for the web-service.
     * @param TIMEOUT set the time-out parameter for the web-service call
     */
    public void setTIMEOUT(int TIMEOUT) {
        this.TIMEOUT = TIMEOUT;
    }


    /**
     * Write to the system the file named outfile containing
     * the string content.
     * @param content the content of the file
     * @return the string of the full path of the output file
     * @throws IOException when something happens while writting the file
     */
    public String writeFile(final String content)
            throws IOException {
        String outputfile = "output.xml";
        outputfile = System.getProperty("java.io.tmpdir") + "/" + outputfile;

        return this.writeFile(outputfile, content);
    }

    /**
     * Invoke the service named servicename located at serviceurl
     * with the given input and write down the output if writeout
     * is true.
     * @param servicename the name of the web-service
     * @param serviceurl the url of the web-service
     * @param input the xml input
     * @param ssl a boolean specifying if we should use ssl
     * @param writeout a boolean to write out the output or not
     * @return the output returned by the web-service
     * @throws Exception when something happens
     */
    public String callService(
                final String servicename,
                final String serviceurl,
                final String input,
                final boolean ssl,
                final boolean writeout
        ) throws Exception {

        String outputfile = "output.xml";

        final String output = this.callService(servicename, serviceurl,
                                                input, ssl);
        if (writeout) {
            outputfile = this.writeFile(outputfile, output);
        }

        return outputfile;

    }

    /**
     * Invoke the service named servicename located at serviceurl
     * with the given input and write down the output if writeout
     * is true to the filename specified.
     * @param servicename the name of the web-service
     * @param serviceurl the url of the web-service
     * @param input the xml input
     * @param ssl a boolean specifying if we should use ssl
     * @param writeout a boolean to write out the output or not
     * @param outputfile the file in which to write out the output
     * @return the output returned by the web-service
     * @throws Exception when something happens
     */
    public String callService(
                    final String servicename,
                    final String serviceurl,
                    final String input,
                    final boolean ssl,
                    final boolean writeout,
                    String outputfile)
            throws Exception {

        final String output = this.callService(servicename, serviceurl,
                                                input, ssl);
        if (writeout) {
            outputfile = this.writeFile(outputfile, output);
        }

        return outputfile;

    }

    /**
     * Invoke the service named servicename located at serviceurl
     * with the given input.
     * @param servicename the name of the web-service
     * @param serviceurl the url of the web-service
     * @param input the xml input
     * @return the output returned by the web-service
     * @throws Exception when something happens
     */
    public String callService(final String servicename, final String serviceurl,
                    final String input)
            throws Exception {
        final String output = this.callService(servicename, serviceurl,
                                    input, true);
        return output;

    }

    /**
     * Invoke the service named servicename located at serviceurl
     * with the given input.
     * @param servicename the name of the web-service
     * @param serviceurl the url of the web-service
     * @param input the xml input
     * @param ssl a boolean specifying if we should use ssl
     * @return the output returned by the web-service
     * @throws Exception when something happens
     */
    public String callService(final String servicename, final String serviceurl,
                    final String input, final boolean ssl)
            throws Exception {
        if (ssl) {
            this.setSSL();
        }
        if (debug) {
            LOG.log(Level.INFO, "service name: {0}", servicename);
            LOG.log(Level.INFO, "service url: {0}", serviceurl);
            LOG.log(Level.INFO, "input: {0}", input);
        }
        final URL target = new URL(serviceurl);
        final AxisCall call = new AxisCall(target, TIMEOUT);
        call.getCall().setSOAPActionURI(serviceurl + "#" + servicename);
        final String out = filterMobyResponseType(call.doCall(serviceurl,
                servicename, new Object[]{sendingFilter(input, false)}));
        return out;
    }

    /**
     * Invoke the service named servicename located at serviceurl
     * with the given input using authentication (user/password)
     * @param servicename the name of the web-service
     * @param serviceurl the url of the web-service
     * @param input the xml input
     * @param user the username used for authentification
     * @param password the password used for authentification
     * @return the output returned by the web-service
     * @throws Exception when something happens
     */
    public String callService(
                    final String servicename,
                    final String serviceurl,
                    final String input,
                    final String user,
                    final String password)
            throws Exception {
        System.out.println(servicename);
        final URL target = new URL(serviceurl);
        final AxisCall call = new AxisCall(target, TIMEOUT);
        call.getCall().setProperty(Call.USERNAME_PROPERTY, user);
        call.getCall().setProperty(Call.PASSWORD_PROPERTY, password);
        call.getCall().setSOAPActionURI(serviceurl + "#" + servicename);
        final String out = filterMobyResponseType(call.doCall(serviceurl,
                servicename, new Object[]{sendingFilter(input, false)}));
        return out;
    }

    /**
     * Convert the given input string as bytes if boolean is true.
     * @param input the xml input string
     * @param asBytes a boolean specifying if the input should be converted
     * @return an Object
     */
    private Object sendingFilter(String input, boolean asBytes) {
        if (asBytes) {
            return input.getBytes();
        } else {
            return input;
        }
    }

    /**
     * Check if the output from the web-service is either a string of a
     * base64/byte[] chain and retun the output as string.
     * @param result the result Object
     * @return the response from biomoby
     * @throws Exception when something happens
     */
    private String filterMobyResponseType(final Object result)
            throws Exception {
        if (result instanceof String) {
            return (String) result;
        } else if (result instanceof byte[]) {
            return new String((byte[]) result);
        } else {
            throw new Exception("The Biomoby data should be sent/received "
                    + "either as type String or base64/byte[]. "
                    + "But they are of type '" + result.getClass().getName()
                    + "'.");
        }
    }

    /**
     * Set the SSL parameters to invoke the web-service.
     * Code from Richard's BioMoby client.
     */
    private void setSSL() {
        if (debug) {
            LOG.log(Level.INFO, "Check for existence of keystore file: ");
        }
        FileOrDirectoryExists.FileOrDirectoryExists(keystore);
        final Properties systemProps = System.getProperties();
        if (debug) {
            LOG.log(Level.INFO, "TrustStore: {0}", systemProps.getProperty(
                            "javax.net.ssl.trustStore"));
        }
        // in the constructor / repository call?
        System.setProperty("javax.net.ssl.trustStore", keystore);
        System.setProperty("javax.net.ssl.trustStorePassword", "");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
    }
}
