package tests;

import customobjects.User;
import framework.DriverWrapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pageobjects.EmailSent;
import pageobjects.ForgotPassword;

import static org.testng.Assert.assertTrue;

public class ExampleTests {

    private DriverWrapper driver;

    @BeforeClass
    public void setup() {
        driver = new DriverWrapper(DriverWrapper.browsers.CHROME, DriverWrapper.operatingSystems.LOCAL);
    }

    @Test
    public void passwordReset(){

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
