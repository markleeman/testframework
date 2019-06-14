package framework;

import org.jboss.aerogear.security.otp.Totp;

/**
 * Helper for tests involving 2F Auth.  Pass the shared secret when initilizing the class and it will allow
 * a TOTP (Temporary One Time Password) to be generated at any time.
 */
public class TOTPHelper {

    final Totp totpGen;

    public TOTPHelper(String secret){
        totpGen = new Totp(secret);
    }

    public String getOTP(){
        return totpGen.now();
    }

    public static String generateOTP(String secret) {
        Totp totp = new Totp(secret);
        return totp.now();
    }
}