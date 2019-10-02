import customobjects.User;
import framework.DriverWrapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import pageobjects.EmailSent;
import pageobjects.ForgotPassword;

import static org.testng.Assert.assertTrue;

public class ExampleTest {

    private DriverWrapper driver;

    // Example test using an explicitly defined browser and os
    @Test
    public void passwordReset(){

        driver = new DriverWrapper(DriverWrapper.browsers.CHROME_HEADLESS, false);

        String confirmMessage = "Your e-mail's been sent!";

        User testUser = User.createNewRandomUser();

        EmailSent reset = new ForgotPassword(driver)
                .enterEmail(testUser.getEmailAddress())
                .submitFormWithButton();

        assertTrue(reset.getMessageText().contains(confirmMessage));
    }

    // Example test using system properties to specify the browser and os
    @Test
    public void test2(){

        // TODO Test something else...
        driver = DriverWrapper.createFromSystemProperties();

        String confirmMessage = "Your e-mail's been sent!";

        User testUser = User.createNewRandomUser();

        EmailSent reset = new ForgotPassword(driver)
                .enterEmail(testUser.getEmailAddress())
                .submitFormWithButton();

        assertTrue(reset.getMessageText().contains(confirmMessage));
    }

    @AfterClass
    public void teardown() {
        driver.shutDown();
    }
}
