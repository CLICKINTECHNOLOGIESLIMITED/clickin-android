package com.sourcefuse.clickinandroid.view;

/**
 * Created by akshit on 16/2/15.
 */
/*
* This Class is created to
* send the crash log report through mail, checking password authentication.
* It extends javax.mail.Authenticator,
* Properties class is used to declare port no to be used,
* protocol to be used (smtp) sendMail method sets the sender,
* recipients, body, subject
* */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class GmailCrashLogSender extends javax.mail.Authenticator {

    private String mailhost = "smtp.gmail.com";

    private String user;

    private String password;

    private Session session;


    private Multipart _multipart = new MimeMultipart();

    static {

        Security.addProvider(new JSSLProvider());

    }

    public GmailCrashLogSender(String user, String password) {

        this.user = user;

        this.password = password;

        Properties props = new Properties();

        props.setProperty("mail.transport.protocol", "smtp");

        props.setProperty("mail.host", mailhost);

        props.put("mail.smtp.auth", "true");

        props.put("mail.smtp.port", "465");//port no of gmail,If using some other messenger then change the port no.

        props.put("mail.smtp.socketFactory.port", "465");

        props.put("mail.smtp.socketFactory.class",

                "javax.net.ssl.SSLSocketFactory");

        props.put("mail.smtp.socketFactory.fallback", "false");

        props.setProperty("mail.smtp.quitwait", "false");


        session = Session.getDefaultInstance(props, this);

    }


    protected PasswordAuthentication getPasswordAuthentication() {//this will authenticate sender credentials

        return new PasswordAuthentication(user, password);

    }


    public synchronized void sendMail(String subject, String body,

                                      String sender) throws Exception {//send mail from here



        MimeMessage message = new MimeMessage(session);

        DataHandler handler = new DataHandler(new ByteArrayDataSource(

                body.getBytes(), "text/plain"));

        message.setSender(new InternetAddress(sender));

        message.setSubject(subject);

        message.setDataHandler(handler);

        BodyPart messageBodyPart = new MimeBodyPart();

        messageBodyPart.setText(body);

        _multipart.addBodyPart(messageBodyPart);


        // Put parts in message

        message.setContent(_multipart);


        String recipients = "monika.bindal@sourcefuse.com,prafull.singh@sourcefuse.com,akshit.sharma@sourcefuse.com";//adding recipients

        String[] recipientList = recipients.split(",");
        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
        int counter = 0;
        for (String recipient : recipientList) {
            recipientAddress[counter] = new InternetAddress(recipient.trim());
            counter++;
        }
        message.setRecipients(Message.RecipientType.TO, recipientAddress);

        Transport.send(message);//Using java mail api class (Transport) to send composed msg.

    }

    public class ByteArrayDataSource implements DataSource {

        private byte[] data;

        private String type;


        public ByteArrayDataSource(byte[] data, String type) {

            super();

            this.data = data;

            this.type = type;

        }


        public ByteArrayDataSource(byte[] data) {

            super();

            this.data = data;

        }


        public void setType(String type) {

            this.type = type;

        }


        public String getContentType() {

            if (type == null)

                return "application/octet-stream";

            else

                return type;

        }


        public InputStream getInputStream() throws IOException {

            return new ByteArrayInputStream(data);

        }


        public String getName() {

            return "ByteArrayDataSource";

        }


        public OutputStream getOutputStream() throws IOException {

            throw new IOException("Not Supported");

        }


    }
}
