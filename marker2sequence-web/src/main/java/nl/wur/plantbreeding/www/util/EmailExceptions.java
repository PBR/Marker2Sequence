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

package nl.wur.plantbreeding.www.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Email's exceptions to the the database administrator. The administrator name
 * & email is set via a context parameter. These email's will only be send in an
 * production environment.
 *
 * @author Richard Finkers
 * @since 0.3
 */
public class EmailExceptions extends Email {

    /**
     * The logger
     */
    private static final Logger LOG = Logger.getLogger(
            EmailExceptions.class.getName());

    /**
     * Prepares the exception email which will be send to the registered
     * administrator.
     * @param context The servlet context.
     * @param req The HTTP request
     * @param exception The exception which was thrown.
     * @param customMessage our own message.
     */
    public static void sendExceptionEmail(ServletContext context,
            HttpServletRequest req,
            Exception exception, String customMessage) {

        String productionServer = "true";
        if (context.getInitParameter("productionServer") != null) {
            productionServer = context.getInitParameter("productionServer");
        }

        String emailBody;
        Email email = new Email();
        //Email body shoudl consist of:
        emailBody = "Dear " + context.getInitParameter("adminName") + ",\n\n"
                + "BreeDB has encountered an exception.\n"
                + "page         : " + req.getRequestURI()
                + " (" + req.getRequestURL() + ")\n"
                + "query        : " + req.getQueryString() + "\n"
                + "database     : " + context.getInitParameter("theme") + "\n"
                + "user         : " + req.getRemoteUser() + "\n"
                + "address      : " + req.getRemoteAddr() + "\n";

        if (exception != null) {
            try {
                if (exception.getMessage() != null) {
                    emailBody += "exception    : " + exception.getMessage()
                            + "\n";
                } else {
                    emailBody += "exception    : NULL\n";
                }
            }
            catch (Exception e) {
                emailBody += "exception    : Catched exception while reading "
                        + "exception message.\n";
                e.printStackTrace();
            }
            try {
                if (exception.getCause() != null) {
                    emailBody += "cause        : "
                            + exception.getCause().toString() + "\n";
                } else {
                    emailBody += "cause        : NULL\n";
                }
            }
            catch (Exception e) {
                emailBody += "cause        : Catched exception while reading "
                        + "the cause.\n";
                e.printStackTrace();
            }

        }
        if (customMessage != null) {
            emailBody += "Custom error : " + customMessage + "\n";
        }
        emailBody += "server       : " + context.getServerInfo()
                + " (" + req.getLocalAddr() + ")\n";
        try {
            if (productionServer.equals("true") && req.getLocalAddr() != null) {
                if (req.getLocalAddr().equals("127.0.0.1")
                        || req.getLocalAddr().equals("0:0:0:0:0:0:0:1")) {
                    LOG.info("Log exception instead of sending email");
                    if (exception != null) {
                        LOG.severe(exception.getLocalizedMessage());
                        exception.printStackTrace();
                    }
                } else {
                    email.sendEmail(context, "Exception ", emailBody);
                    LOG.info("Exception email send");
                }
            } else if (productionServer.equals("true")) {
                email.sendEmail(context, "Exception ", emailBody);
                LOG.info("Exception email send");
                if (exception != null) {
                    LOG.severe(exception.getLocalizedMessage());
                }
            } else {
                LOG.info("Log exception instead of sending email");
                if (req.getLocalAddr() == null) {
                    LOG.severe("req.getLocalAddr is null");
                }
                if (exception != null) {
                    LOG.severe("Exception Localized Message:");
                    LOG.severe(exception.getLocalizedMessage());
                    LOG.severe("Exception Cause:");
                    LOG.severe(exception.getCause().toString());
                }
            }
        }
        catch (AddressException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        catch (MessagingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Send exception email template which sends the thrown exception to the
     * administrator.
     * @param context The servlet context.
     * @param req The request.
     * @param exception The exception which was thrown.
     */
    public static void sendExceptionEmail(ServletContext context,
            HttpServletRequest req,
            Exception exception) {
        sendExceptionEmail(context, req, exception, null);
    }

    /**
     * Send exception email with custom text to the administrator.
     * @param context The servlet context.
     * @param req The request.
     * @param customMessage Message to be send.
     */
    public static void sendExceptionEmail(ServletContext context,
            HttpServletRequest req, String customMessage) {
        sendExceptionEmail(context, req, null, customMessage);
    }
}
