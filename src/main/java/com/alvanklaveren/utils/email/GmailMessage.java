package com.alvanklaveren.utils.email;

import java.util.Properties;

public class GmailMessage extends EmailMessage {
	public GmailMessage(String to, String from, String password){
		super();

		setFrom(from);
		setPassWord(password);
		setTo(to);
		setProperties();
	}
	
	private void setProperties(){
	    Properties props = getProperties();
	    props.put( "mail.smtp.auth", 			"true" );
	    props.put( "mail.smtp.starttls.enable", "true" );
	    props.put( "mail.smtp.host", 			"smtp.gmail.com" );
	    props.put( "mail.smtp.port", 			"587" );
	}

}
