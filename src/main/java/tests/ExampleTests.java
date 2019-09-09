package tests;

import customobjects.User;
import framework.DriverWrapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pageobjects.EmailSent;
import pageobjects.ForgotPassword;

public class ExampleTests {

    private DriverWrapper driver;

    @BeforeClass
    public void setup() {
        driver = new DriverWrapper(DriverWrapper.browsers.CHROME, DriverWrapper.operatingSystems.LOCAL);
    }

    @Test
    public void passwordReset(){
        User testUser = User.createNewRandomUser();

        EmailSent reset = new ForgotPassword(driver)
                .enterEmail(testUser.getEmailAddress())
                .submitFormWithButton();
        // TODO assert something
    }

    @AfterClass
    public void teardown() {
        driver.shutDown();
    }
}
