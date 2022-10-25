package com.alvanklaveren.utils.email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class GmailMessage extends EmailMessage {
	public GmailMessage(String to, String from, String password){
		super();

		setFrom(from);
		setPassWord(password);
		setTo(to);
		setProperties();
	}
	
	private void setProperties() {

	    Properties props = getProperties();
		props.put( "mail.smtp.host", 			"smtp.gmail.com" );
		props.put( "mail.smtp.port", 			"465" );
	    props.put( "mail.smtp.auth", 			"true" );
		props.put( "mail.smtp.ssl.enable", 		"true" );
	    //props.put( "mail.smtp.starttls.enable", "true" );
	}

	@Override
	public void send() throws MessagingException {

		Session session = Session.getInstance(properties, new javax.mail.Authenticator(){
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, passWord);
			}
		});

		session.setDebug(true);

		MimeMessage msg = new MimeMessage(session);
		msg.setContent(multiPart);
		msg.setFrom(new InternetAddress(from));
		msg.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));
		msg.setSubject(subject);
		msg.setText(body);

		Transport.send(msg);
	}


}
