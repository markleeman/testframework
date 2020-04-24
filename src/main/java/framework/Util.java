package framework;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Class contains any useful utility methods which we want to make use of throughout our framework
 */
public class Util {

    private static final int DEFAULT_LENGTH = 10;

    public static String getValidEmail(){
        PropertyManager props = new PropertyManager();

        return props.getEmailPrefix() + getRandomString() + props.getEmailDomain();
    }

    public static String getRandomString(){
        return getRandomString(DEFAULT_LENGTH);
    }

    public static String getRandomString(int length){
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String getRandomNumber(){
        return getRandomNumber(DEFAULT_LENGTH);
    }

    /**
     * Generate a random string of the specified length with just numeric characters and no leading zeros
     * We're returning a string to make comparisons simpler in our tests as the values we get back from the driver
     * will also be strings.
     * @param numDigits Number of digits our random number should contain
     * @return String containing random numeric characters
     */
    public static String getRandomNumber(int numDigits) {
        return  RandomStringUtils.randomNumeric(1, 9) + RandomStringUtils.randomNumeric(numDigits - 1);
    }
}