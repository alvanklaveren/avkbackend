package com.alvanklaveren.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringLogic {

	static{ new StringLogic(); }

	private StringLogic(){ }
	
	public static String nvl(String aString){
		return StringLogic.nvl(aString, "");
	}
	
	public static String nvl(String aString, String defaultString){
		if( StringLogic.isEmpty(defaultString) ){ defaultString = ""; }
		if( StringLogic.isEmpty(aString) ){ aString = defaultString; }

		return aString;
	}
	
	public static boolean isEmpty( String aString ){
		if( aString == null || aString.trim().equals( "" ) ){
			return true;
		}
		return false;
	}

	public static boolean isNumeric(String aString){
		try {
				Integer.parseInt(aString);	
		} 
		catch(NumberFormatException nfe) {
				return false;			
		}

		return true;
	}
	
	public String urlEncode(String message){
		String encodedMessage = "";

		try {
			encodedMessage = URLEncoder.encode( message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		return encodedMessage; 
	}

	public String urlDecode(String message){
		String decodedMessage = "";

		try {
			decodedMessage = URLDecoder.decode( message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		return decodedMessage; 
	}

	/*
	 *  This function does the setEscapeModelStrings( true ) for text that is in HTML. However, because I
	 *  introduced a possibility to present messages in bold, italic, and more, we should enforce this by
	 *  deciding ourselves what is allowed and what is not
	 */
	public static String prepareMessage( String messageText ){
		String preparedText = messageText;
		preparedText = preparedText.replaceAll( "<", "&lt" );
		preparedText = preparedText.replaceAll( ">", "&gt" );
		preparedText = preparedText.replaceAll( "\n", "<br>" );

		preparedText = convertToHTML( preparedText, "`", "<mark>", "</mark>" );
		preparedText = convertToHTML( preparedText, "***", "<b><i>", "</i></b>" );
		preparedText = convertToHTML( preparedText, "**", "<b>", "</b>" );
		preparedText = convertToHTML( preparedText, "*", "<i>", "</i>" );

		preparedText = setHyperLink( preparedText );

		return preparedText;
	}
	
	public static String convertToHTML( String messageText, String source, String targetStart, String targetEnd ){
		boolean firstTag = true;
		StringBuilder buildString = new StringBuilder();
		
		for( int i = 0; i < messageText.length(); i++ ){
			if( i < messageText.length() - ( source.length() - 1 ) && messageText.substring( i, i + source.length() ).equals( source ) ) {
				if(firstTag ){
					buildString.append( targetStart );
					firstTag = false;
				}else{
					buildString.append( targetEnd );				
					firstTag = true;
				}
				i += ( source.length() - 1 );
			}else{
				buildString.append( messageText.charAt( i ) );				
			}
		}	
		return( buildString.toString().trim() );

	}

	public static String setHyperLink( String messageText ){

		String modifiedMessageText = messageText;
		
		String hrefCoded = findFirst(modifiedMessageText, "[h:", "]");

		while (hrefCoded.length() > 0) { 
			String[] content = hrefCoded.replace("[h:", "").replaceAll("]", "").split(";");
			
			String href = "";
			String hrefText = "";
			
			switch (content.length) {
				case 1 -> {
					href = content[0];
					hrefText = href;
				}

				case 2 -> {
					href = content[0];
					hrefText = content[1];
				}
			}
				
			if(!href.startsWith("http"))
				href = "http://" + href; 
				
			String hyperlink = "<a target=\"_blank\" rel=\"nofollow\" href=\"" + href + "\">" + hrefText + "</a>";;
			
			modifiedMessageText = modifiedMessageText.replace(hrefCoded, hyperlink);
			
			hrefCoded = findFirst(modifiedMessageText, "[h:", "]");
		}
				
		return modifiedMessageText.trim();

	}

	public static String findFirst(String source, String start, String end) {
		int startPos = source.indexOf(start);
		if (startPos == -1) {
			return "";
		}
		
		int endPos = source.indexOf(end, startPos + 1);
		if (endPos == -1) {
			return ""; 
		}
			
		return source.substring(startPos, endPos + end.length());
	}
	
	/*
	 *  This function does the setEscapeModelStrings( true ) for text that is in HTML. 
	 *  To make editing easier, **** will be recognised as <table class=sourcetext>
	 */
	public static String prepareSourceText( String messageText ){
		String preparedText = messageText;
		preparedText = preparedText.replaceAll( " ", "&nbsp;" );
		preparedText = preparedText.replaceAll( "%n", "<br>" );
		preparedText = convertToHTML( preparedText, "****", "<table class=sourcetext><tr><td class=sourcetext>", "</td></tr></table>" );
		
		return preparedText;
	}

	/**
	 * Validate hex with regular expression
	 * 
	 * @param hex
	 *            hex for validation
	 * @return true valid hex, false invalid hex
	 */
	public static boolean validateEmailAddress( final String hex ){
		Pattern pattern;
		Matcher matcher;
	 
		final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	 
		pattern = Pattern.compile( EMAIL_PATTERN );
		matcher = pattern.matcher( hex );
		return matcher.matches();	 
	}

	// translate (roman) numbers at the end of a string, if any. E.g. a 2 becomes II, and a IV becomes 4
	public static String convertVersionNumbers(String str){
		if(str.lastIndexOf(" ", str.length()) <= 0){
			return str;
		}
		String strStart = str.substring(0, str.lastIndexOf(" ", str.length()) + 1);
		String strEnd   = str.substring(str.lastIndexOf(" ", str.length()), str.length());

		strStart += switch(strEnd.trim()) {
	    	case "I" -> 	"1";
	    	case "II" -> 	"2";
	    	case "III" -> 	"3";
	    	case "IV" -> 	"4";
	    	case "V" -> 	"5";
	    	case "VI" -> 	"6";
	    	case "VII" -> 	"7";
	    	case "VIII" -> 	"8";
	    	case "IX" -> 	"9";
	    	case "X" -> 	"10";
	    	case "XI" -> 	"11";
	    	case "XII" -> 	"12";
	    	case "XIII" -> 	"13";
	    	case "XIV" -> 	"14";
	    	case "XV" -> 	"15";
	    	case "XVI" -> 	"16";
	    	case "XVII" -> 	"17";
	    	case "XVIII" -> "18";
	    	case "XIX" ->	"19";
	    	case "XX" -> 	"20";
	
	    	/*case "1" -> 	"I"; // this will almost never happen (a version 1). So to prevent find result on II, III, etc. SKIP*/
	    	case "2" ->		"II";
	    	case "3" ->		"III";
	    	case "4"->		"IV";
	    	case "5"->		"V";
	    	case "6"->		"VI";
	    	case "7"->		"VII";
	    	case "8"->		"VIII";
	    	case "9"->		"IX";
	    	case "10"->		"X";
	    	case "11"->		"XI";
	    	case "12"->		"XII";
	    	case "13"->		"XIII";
	    	case "14"->		"XIV";
	    	case "15"->		"XV";
	    	case "16"->		"XVI";
	    	case "17"->		"XVII";
	    	case "18"->		"XVIII";
	    	case "19"->		"XIX";
	    	case "20"->		"XX";
	    	default->		strEnd;
		};

		// next, find version numbers that are somewhere in the middle of a string
		String origStrStart = new String(strStart);
		strStart = strStart.replace(" II ", 		" 2 ");
		strStart = strStart.replace(" III ", 	" 3 ");
		strStart = strStart.replace(" IV ", 		" 4 ");
		strStart = strStart.replace(" V ", 		" 5 ");
		strStart = strStart.replace(" VI ", 		" 6 ");
		strStart = strStart.replace(" VII ",		" 7 ");
		strStart = strStart.replace(" VIII ",	" 8 ");
		strStart = strStart.replace(" IX ", 		" 9 ");
		strStart = strStart.replace(" X ",		" 10 ");
		strStart = strStart.replace(" XI ", 		" 11 ");
		strStart = strStart.replace(" XII ", 	" 12 ");
		strStart = strStart.replace(" XIII ", 	" 13 ");
		strStart = strStart.replace(" XIV ", 	" 14 ");
		strStart = strStart.replace(" XV ", 		" 15 ");
		strStart = strStart.replace(" XVI ", 	" 16 ");
		strStart = strStart.replace(" XVII ", 	" 17 ");
		strStart = strStart.replace(" XVIII ", 	" 18 ");
		strStart = strStart.replace(" XIX ", 	" 19 ");
		strStart = strStart.replace(" XX ", 		" 20 ");

		strStart = strStart.replace(" II-", 		" 2 ");
		strStart = strStart.replace(" III-", 	" 3 ");
		strStart = strStart.replace(" IV-", 		" 4 ");
		strStart = strStart.replace(" V-", 		" 5 ");
		strStart = strStart.replace(" VI-", 		" 6 ");
		strStart = strStart.replace(" VII-", 	" 7 ");
		strStart = strStart.replace(" VIII-", 	" 8 ");
		strStart = strStart.replace(" IX-", 		" 9 ");
		strStart = strStart.replace(" X-", 		" 10 ");
		strStart = strStart.replace(" XI-", 		" 11 ");
		strStart = strStart.replace(" XII-", 	" 12 ");
		strStart = strStart.replace(" XIII-", 	" 13 ");
		strStart = strStart.replace(" XIV-", 	" 14 ");
		strStart = strStart.replace(" XV-", 		" 15 ");
		strStart = strStart.replace(" XVI-", 	" 16 ");
		strStart = strStart.replace(" XVII-", 	" 17 ");
		strStart = strStart.replace(" XVIII-", 	" 18 ");
		strStart = strStart.replace(" XIX-", 	" 19 ");
		strStart = strStart.replace(" XX-", 		" 20 ");

		// and vice versa (but only if the above did not already change it)
		if(origStrStart.equals(strStart)) {
			strStart = strStart.replace(" 2 ", 	" II ");
			strStart = strStart.replace(" 3 ", 	" III ");
			strStart = strStart.replace(" 4 ", 	" IV ");
			strStart = strStart.replace(" 5 ", 	" V ");
			strStart = strStart.replace(" 6 ", 	" VI ");
			strStart = strStart.replace(" 7 ", 	" VII ");
			strStart = strStart.replace(" 8 ", 	" VIII ");
			strStart = strStart.replace(" 9 ", 	" IX ");
			strStart = strStart.replace(" 10 ", 	" X ");
			strStart = strStart.replace(" 11 ", 	" XI ");
			strStart = strStart.replace(" 12 ", 	" XII ");
			strStart = strStart.replace(" 13 ", 	" XIII ");
			strStart = strStart.replace(" 14 ", 	" XIV ");
			strStart = strStart.replace(" 15 ", 	" XV ");
			strStart = strStart.replace(" 16 ", 	" XVI ");
			strStart = strStart.replace(" 17 ", 	" XVII ");
			strStart = strStart.replace(" 18 ", 	" XVIII ");
			strStart = strStart.replace(" 19 ", 	" XIX ");
			strStart = strStart.replace(" 20 ", 	" XX ");
		}
		
		return strStart;
	}
}
