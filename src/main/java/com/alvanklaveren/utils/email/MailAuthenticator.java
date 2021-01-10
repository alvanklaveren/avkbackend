package com.alvanklaveren.utils.email;


import lombok.extern.slf4j.Slf4j;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

@Slf4j
public class MailAuthenticator extends Authenticator {
     String user;
     String pw;

     public MailAuthenticator (String username, String password)
     {
        super();
        this.user = username;
        this.pw = password;
     }
     
     public MailAuthenticator (EmailMessage emailMessage){
		 super();
		 user = emailMessage.getFrom();
		 pw = emailMessage.getPassWord();
     }
     
    public PasswordAuthentication getPasswordAuthentication()
    {
       return new PasswordAuthentication(user, pw);
    }
}