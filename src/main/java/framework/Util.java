package framework;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.Random;

/**
 * Class contains any useful utility methods which we want to make use of throughout our framework
 */
public class Util {

    private static final int DEFAULT_LENGTH = 10;

    public static String getValidEmail(){
        PropertyManager props = new PropertyManager();

        return props.getEmailUsername() + "+" + getRandomStringOfLength() + props.getEmailDomain();
    }

    public static String getRandomStringOfLength(){
        return getRandomStringOfLength(DEFAULT_LENGTH);
    }

    public static String getRandomStringOfLength(int length){
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static long getRandomNumberOfLength(){
        return getRandomNumberOfLength(DEFAULT_LENGTH);
    }

    /**
     * Generate a random number of the specified length with no leading zeros
     * @param length Number of digits our random number should contain
     * @return Random number with specified number of digits
     */
    public static long getRandomNumberOfLength(int length) {

        // We'll limit the length to 18 digits to avoid hitting Long.MAX_VALUE
        if (length > 18) {
            throw new IllegalStateException("Max random number length exceeded");
        }

        Random rng = new Random();

        // If this is the first digit then don't allow a zero value
        int firstDigit = rng.nextInt(9) + 1;;

        // We can then generate any random number for the remaining digits
        long remainingNDigits = RandomUtils.nextLong(0, (long)(Math.pow(10, (length - 1))));

        // And concatenate them to make a random number with no leading zeros
        return (firstDigit * (long)(Math.pow(10, length - 1))) + remainingNDigits;
    }
}