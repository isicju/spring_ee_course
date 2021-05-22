package com.example.demo.services;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.StringWriter;
import java.util.Properties;

@Log4j2
@AllArgsConstructor
@Service
public class MailService {

    @Qualifier("emailProperties")
    @Autowired
    private final Properties emailProperties;

    public String sendEmail(String toEmail) {
        try {
            String fromEmail = emailProperties.getProperty("fromEmail");
            String yahooAccountAppPassword = emailProperties.getProperty("appPassword");

            Session session = Session.getInstance(emailProperties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, yahooAccountAppPassword);
                }
            });

            session.setDebug(true);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toEmail));
            message.setSubject("Report about employee");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("In this email please find report about employee. " +
                    "Please check attachments for pdf file");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            String filename = "hello.pdf";
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            send(message);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            String stackTrace = sw.toString();
            log.error("mail sender failed " + stackTrace);
            throw new RuntimeException("mail sender failed" + e.getMessage());
        }
        return "Email sent!";
    }

    public void send(Message message) throws Exception{
        Transport.send(message);
    }

}
