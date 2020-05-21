package utils.email;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import java.util.Properties;

public abstract class EmailMessage {
	
	private String from 		= "";
	private String pass 		= "";
    private Properties props;

    private String subject 		= "";
	private String msgBody  	= "";
	private String to			= ""; 

    private Multipart multiPart = new MimeMultipart();

	public EmailMessage(){
		props = new Properties();
    }

	public void send() throws AddressException, MessagingException{
	    Session session = Session.getInstance( props, new MailAuthenticator(this) );
	        Message msg = new MimeMessage(session);
	        msg.setContent(multiPart);
	        msg.setFrom( new InternetAddress( from ) );
			msg.setRecipients(RecipientType.TO, InternetAddress.parse(to));
	        msg.setSubject( subject );
	        msg.setText( msgBody );
	        Transport.send(msg);	
	}

	public void setSubject(String subject){
		this.subject = subject;
	}

	public void setBody(String msgBody){
		this.msgBody = msgBody;
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

	protected Properties getProperties() {
		return props;
	}

	protected String getFrom() {
		return from;
	}

	protected String getPassWord() {
		return pass;
	}

	protected String getSubject() {
		return subject;
	}

	protected String getMsgBody() {
		return msgBody;
	}

	protected void setTo(String to) {
		this.to = to;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	protected void setPassWord(String pass) {
		this.pass = pass;
	}

}
