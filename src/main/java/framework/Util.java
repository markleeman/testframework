package framework;

import org.apache.commons.lang3.RandomStringUtils;

public class Util {

    private static final int DEFAULT_LENGTH = 10;

    public static String getValidEmail(){

        return getRandomString() + new PropertyManager().getEmailDomain();
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
     * Generate a random string with just numeric characters, with any leading zeros stripped
     * @param numDigits
     * @return
     */
    public static String getRandomNumber(int numDigits) {

        return  RandomStringUtils.random(5);
    }
}