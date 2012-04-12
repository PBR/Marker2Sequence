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

import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;

/**
 * Handles sending an email to the user.
 * @author Richard Finkers richard.finkers@gmail.com
 */
public class Email {

    /** The logger */
    private static final Logger LOG = Logger.getLogger(Email.class.getName());

    /**
     * Obtain the administrators email address from the context parameters.
     * @param context
     * @return
     */
    public String getEmailAdress(ServletContext context) {
        return context.getInitParameter("adminEmail");
    }

    /**
     * Obtain the smtp server from the context parameters.
     * @param context
     * @return
     */
    public String getSmtpServer(ServletContext context) {
        return context.getInitParameter("smtp");
    }

    /**
     * Generic wrapper to send an email address from the BreeDB administrator to
     * the administrator.
     * @param context
     * @param subject
     * @param emailBody
     * @throws AddressException
     * @throws MessagingException
     */
    protected void sendEmail(ServletContext context, String subject, String emailBody) throws AddressException, MessagingException {
        Properties props = new Properties();

        props.put(getSmtpServer(context), getSmtpServer(context));

        Session session1 = Session.getDefaultInstance(props, null);
        Message message = new MimeMessage(session1);
        message.setFrom(new InternetAddress(getEmailAdress(context)));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getEmailAdress(context), false));
        message.setSubject(subject);

        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

        // Fill the message
        messageBodyPart.setText(emailBody);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Put parts in message
        message.setContent(multipart);

        Transport.send(message);
        LOG.info("Email send");
    }

    /**
     * Send a email containing the licensefile as an attachment to the user.
     * @param fromEmail Email from the sender. This is read from the context.
     * @param toEmail Email of the recipient.
     * @param subject subject of the email
     * @param emailBody
     * @param smtpServer smtp server to be used for sending the email.
     * @throws MessagingException Error creating or sending the email.
     */
    public void sendEmail(String fromEmail, String toEmail, String subject, String emailBody, String smtpServer) throws MessagingException {

        Properties props = new Properties();

        props.put(smtpServer, smtpServer);

        Session session1 = Session.getDefaultInstance(props, null);

        Message message = new MimeMessage(session1);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        message.setSubject(subject);

        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

        // Fill the message
        messageBodyPart.setText(emailBody);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        //messageBodyPart = new MimeBodyPart();
        //DataSource source = new FileDataSource("/home/finke002/sources.list");
        //messageBodyPart.setDataHandler(new DataHandler(source));
        //messageBodyPart.setFileName("test");
        //multipart.addBodyPart(messageBodyPart);

        // Put parts in message
        message.setContent(multipart);

        Transport.send(message);
        LOG.info("Email send");
    }
}
