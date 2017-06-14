package it.unige.cseclab.stim;

import java.util.Random;

public class StringRandomizer {
	
	public static final String ALPHABET = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ@.";
	private static final int NTYPES = 5;
	static Random random = new Random();

	public static String next(int nChars) {
		int what = random.nextInt(NTYPES);
		
		switch (what) {
		case 0 : return nextText(nChars);
		case 1 : return nextEmail(nChars);
		case 2 : return nextUser(nChars);
		case 3 : return nextPassword(nChars);
		case 4 : return nextUrl(nChars);
		default : return nextText(nChars);
		}
	}

	public static String nextText(int nChars) {
		String w = "";
		
		for(int i = 0; i < random.nextInt(nChars) + 1; i++) {
			w += ALPHABET.charAt(random.nextInt(ALPHABET.length()));
		}
		
		return w;
	}
	
	public static String nextEmail(int nChars) {
		String w = "";
		
		for(int i = 0; i < random.nextInt(nChars) + 1; i++) {
			w += ALPHABET.charAt(random.nextInt(ALPHABET.length()));
		}
		
		return w;
	}
	
	public static String nextUser(int nChars) {
		
		return "dinesh";
	}
	
	public static String nextPassword(int nChars) {
		
		return "Dinesh@123$";
	}
	
	public static String nextUrl(int nChars) {
		return "http://" + nextText(nChars);
	}
}
