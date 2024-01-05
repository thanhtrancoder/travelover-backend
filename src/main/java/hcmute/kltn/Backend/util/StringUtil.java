package hcmute.kltn.Backend.util;

import java.text.Normalizer;
import java.util.Random;
import java.util.regex.Pattern;

public class StringUtil {
	public static String genRandom(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(rnd.nextInt(characters.length())));
        }

        return sb.toString();
	}
	
	public static String genRandomInteger(int length) {
        String characters = "0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(rnd.nextInt(characters.length())));
        }

        return sb.toString();
	}
	
	public static String getIntegerString(String input) {
		double varDouble = Double.valueOf(input);
		int varInteger = (int) varDouble;
		String varString = String.valueOf(varInteger);

        return varString;
	}
	
	public static String getNormalAlphabet(String input) {
		input = input.trim();
		input = input.toLowerCase();
		input  = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        input = pattern.matcher(input ).replaceAll("");
        input = input.replaceAll("đ", "d");
        String splitPlace = input.replaceAll("  ", " ");
        while (!splitPlace.equals(input)) {
        	input = splitPlace;
        	splitPlace = input.replaceAll("  ", " ");
        }

        return input;
    }
}
