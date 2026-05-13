package cakes.text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class StringHash {

	private static MessageDigest md5;

	public static String hashMD5 (String input) {
		
		String output = null;
		
		try {
			md5 = MessageDigest.getInstance("MD5");
			String text = input.replaceAll("\\s+", " ").toLowerCase().trim();
			byte[] hash = md5.digest(text.getBytes());
			output = DatatypeConverter.printHexBinary(hash);
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return output;		
	}
}
