package com.alvanklaveren.utils.email;

import java.util.Properties;

public class HotmailMessage extends EmailMessage {

	public HotmailMessage(String to, String from, String password){
		super();
		setTo(to);
		setFrom(from);
		setPassWord(password);
		setProperties();
	}
	
	private void setProperties(){
	    Properties props = getProperties();
	    props.put( "mail.smtp.auth", 			"true" );
	    props.put( "mail.smtp.starttls.enable", "true" );
	    props.put( "mail.smtp.host", 			"smtp.live.com" );
	    props.put( "mail.smtp.port", 			"587" );
	}

}
