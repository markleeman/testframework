import customobjects.User;
import framework.*;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pageobjects.EmailSent;
import pageobjects.ForgotPassword;
import pageobjects.LoginPage;
import pageobjects.SecureArea;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ExampleTest {

    private DriverWrapper driver;

    // Example test using an explicitly defined browser and Selenium Grid option
    @Test(retryAnalyzer = RetryOnFail.class)
    public void passwordReset() {

        driver = new DriverWrapper(DriverWrapper.browsers.CHROME_HEADLESS, false);

        String confirmMessage = "Your e-mail's been sent!";

        User testUser = User.createNewRandomUser();

        EmailSent reset = new ForgotPassword(driver)
                .enterEmail(testUser.getEmailAddress())
                .submitFormWithButton();

        assertTrue(reset.getMessageText().contains(confirmMessage));
    }

    // Example test using an explicitly defined browser and default Selenium Grid option
    @Test(retryAnalyzer = RetryOnFail.class)
    public void invalidUsername() {

        driver = new DriverWrapper(DriverWrapper.browsers.CHROME_HEADLESS);

        String errorMessage = "Your username is invalid!";

        User testUser = User.createNewRandomUser();

        LoginPage login = new LoginPage(driver)
                .enterUserDetails(testUser)
                .loginExpectingError();

        assertTrue(login.getMessageText().contains(errorMessage));
    }

    // Example test using system properties to specify the browser and os
    @Test(retryAnalyzer = RetryOnFail.class)
    public void validLogin() {

        driver = DriverWrapper.createFromSystemProperties();

        String loggedInMessage = "You logged into a secure area!";

        User testUser = User.createNewRandomUser();
        testUser.setUsername("tomsmith");
        testUser.setPassword("SuperSecretPassword!");

        SecureArea secure = new LoginPage(driver)
                .enterUserDetails(testUser)
                .loginExpectingSuccess();

        assertTrue(secure.getMessageText().contains(loggedInMessage));
    }

    // Example tests using the Rest API helper instead of a browser
    @Test(retryAnalyzer = RetryOnFail.class)
    public void getRequest() {

        PropertyManager props = new PropertyManager();
        String endpoint = props.getBaseURL() + "status_codes/404";

        RestAPIHelper api = new RestAPIHelper(endpoint);
        api.addRequestHeader("Accept-Encoding", "gzip, deflate");
        api.submitGetRequest();
        assertEquals(api.getResponseCode(), 404);
    }

    @Test(retryAnalyzer = RetryOnFail.class)
    public void postRequest() {

        PropertyManager props = new PropertyManager();
        String endpoint = props.getBaseURL() + "authenticate";

        RestAPIHelper api = new RestAPIHelper(endpoint);
        api.setRequestBody("username=tomsmith&password=SuperSecretPassword!");
        api.addRequestHeader("Accept-Encoding", "gzip, deflate");
        api.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

        api.submitPostRequest();
        assertEquals(api.getResponseCode(), 200);
        assertTrue(api.getResponseBody().contains("You logged into a secure area!"));
    }

    @AfterMethod
    public void screenshotOnFail(ITestResult result) {
        if (driver != null) {

            if (!result.isSuccess()) {
                driver.takeScreenShot(result.getMethod().getMethodName());
            }

            driver.shutDown();
        }
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.shutDown();
        }
    }
}