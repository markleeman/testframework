package framework;

import org.apache.commons.lang3.RandomStringUtils;

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
     * Generate a random string with just numeric characters
     * @param numDigits Number of digits our random number should contain
     * @return String containing random numeric characters
     */
    public static String getRandomNumber(int numDigits) {
        // TODO strip leading zeros
        return  RandomStringUtils.random(5);
    }
}