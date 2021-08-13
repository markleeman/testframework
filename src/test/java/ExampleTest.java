import framework.*;
import framework.enums.SupportedBrowsers;
import framework.enums.TestAccounts;
import models.User;
import org.openqa.selenium.logging.LogEntry;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pageobjects.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ExampleTest {

    private DriverWrapper driver;

    // Example tests using an explicitly defined, local browser
    @Test(retryAnalyzer = RetryOnFail.class)
    public void passwordReset() {

        driver = DriverFactory.createLocalDriver(SupportedBrowsers.CHROME_HEADLESS);

        String confirmMessage = "Your e-mail's been sent!";

        User testUser = User.createNewRandomUser();

        EmailSent reset = new ForgotPassword(driver)
                .enterEmail(testUser.getEmailAddress())
                .submitFormWithButton();

        assertTrue(reset.getMessageText().contains(confirmMessage));
    }

    @Test(retryAnalyzer = RetryOnFail.class)
    public void invalidUsername() {

        driver = DriverFactory.createLocalDriver(SupportedBrowsers.CHROME_HEADLESS);

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

        driver = DriverFactory.createDriverFromSystemProperties();

        String loggedInMessage = "You logged into a secure area!";

        User testUser = TestAccounts.CUSTOMER.getUser();

        SecureArea secure = new LoginPage(driver)
                .enterUserDetails(testUser)
                .loginExpectingSuccess();

        assertTrue(secure.getMessageText().contains(loggedInMessage));
    }

    // Example tests using the Rest API helper instead of a browser
    @Test(retryAnalyzer = RetryOnFail.class)
    public void getRequest() {

        ConfigManager props = new ConfigManager();
        String endpoint = props.getBaseURL() + "status_codes/404";

        RestAPIHelper api = new RestAPIHelper(endpoint);
        api.addRequestHeader("Accept-Encoding", "gzip, deflate");
        api.submitGetRequest();
        assertEquals(api.getResponseCode(), 404);
    }

    @Test(retryAnalyzer = RetryOnFail.class)
    public void postRequest() {

        ConfigManager props = new ConfigManager();
        String endpoint = props.getBaseURL() + "authenticate";

        User testUser = TestAccounts.CUSTOMER.getUser();

        RestAPIHelper api = new RestAPIHelper(endpoint);
        api.setRequestBody("username=" + testUser.getUsername() + "&password=" + testUser.getPassword());
        api.addRequestHeader("Accept-Encoding", "gzip, deflate");
        api.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

        api.submitPostRequest();
        assertEquals(api.getResponseCode(), 200);
        assertTrue(api.getResponseBody().contains("You logged into a secure area!"));
    }

    @Test(retryAnalyzer = RetryOnFail.class, groups="excludeOnGithub")
    public void emailTest() {

        User testUser = User.createNewRandomUser();

        driver = DriverFactory.createDriverFromSystemProperties();

        EmailSent reset = new ForgotPassword(driver)
                .enterEmail(testUser.getEmailAddress())
                .submitFormWithButton();

        EmailHelper gmail = new EmailHelper();
        String email = gmail.waitForPasswordResetEmail(testUser);

        // At this point we'd probably want to extract a URL, token, or some other information from the email, and then
        // use this to continue our test.  However, for the purpose of this example we'll just confirm it contains
        // a piece of text

        assertTrue(email.contains("username: tomsmith"));
    }

    @Test
    public void getBrowserLogs() {

        driver = DriverFactory.createLocalDriver(SupportedBrowsers.CHROME_HEADLESS);

        JSError err = new JSError(driver);

        List<LogEntry> logs = driver.getBrowserConsoleErrors();

        assertEquals(logs.size(), 1);
        assertEquals(logs.get(0).getLevel(), Level.SEVERE);

        for (LogEntry entry : logs) {
            System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage() + " " + entry.toString());
        }
    }

    @AfterMethod
    public void screenshotOnFail(ITestResult result) {
        if (driver != null) {

            if (!result.isSuccess()) {
                driver.takeScreenShot(result.getMethod().getMethodName());
            }

            driver.shutDown();
            driver = null;
        }
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.shutDown();
            driver = null;
        }
    }
}