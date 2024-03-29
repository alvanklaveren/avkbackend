package com.alvanklaveren.utils.email;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Slf4j
public abstract class EmailMessage {

	protected String from = "";
	protected String passWord = "";
    protected final Properties properties;

	protected String subject = "";
	protected String body = "";
	protected String to = "";

	protected final Multipart multiPart;

	public EmailMessage(){
		properties = new Properties();
		multiPart = new MimeMultipart();
    }

	public void send() throws MessagingException {

	    Session session = Session.getInstance(properties, new MailAuthenticator(this) );

		Message msg = new MimeMessage(session);
		msg.setContent(multiPart);
		msg.setFrom(new InternetAddress(from));
		msg.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));
		msg.setSubject(subject);
		msg.setText(body);

		Transport.send(msg);
	}

	public boolean addAttachment(String filename){

		BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);

        try {

			messageBodyPart.setDataHandler(new DataHandler(source));
	        messageBodyPart.setFileName(filename);
	        multiPart.addBodyPart(messageBodyPart);
		} catch (MessagingException e) {

			e.printStackTrace();
			return false;
		}

        return true;
	}
}
