package hcmute.kltn.Backend.util;

import java.util.Random;

public class IntegerUtil {
	public static int randomRange(int min, int max) {
        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;
        
        return randomNum;
	}
}
