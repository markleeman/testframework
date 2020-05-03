package pageobjects;

import framework.DriverWrapper;
import models.User;
import org.openqa.selenium.By;

public class LoginPage extends BasePageObject {

    public final String PAGE_URL = BASE_URL + "login";
    public final String[] PAGE_TITLE = {"The Internet"};

    protected final By usernameField = By.id("username");
    protected final By passwordField = By.id("password");
    protected final By loginbutton = By.className("fa-sign-in");
    protected final By messageBox = By.id("flash");

    /**
     * Constructor used by tests to perform an initial page load
     * @param wrapper DriverWrapper instance we should get the WebDriver instance from and use to load this page
     */
    public LoginPage(DriverWrapper wrapper) {
        setup(wrapper, true);
    }

    /**
     * Constructor used by other page objects as we navigate around a site
     * @param wrapper WebDriver instance which should already be on this page
     */
    protected LoginPage(DriverWrapper wrapper, boolean loadPage) {
        setup(wrapper, loadPage);
    }

    private void setup(DriverWrapper wrapper, boolean loadPage) {
        this.driver = wrapper;

        if (loadPage) {
            driver.get(PAGE_URL);
        }

        selfCheckPageTitleContains(PAGE_TITLE);
    }

    public LoginPage enterUsername(String username) {
        setText(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        setText(passwordField, password);
        return this;
    }

    public LoginPage enterUserDetails(User user) {
        enterUsername(user.getUsername());
        enterPassword(user.getPassword());
        return this;
    }

    public SecureArea loginExpectingSuccess() {
        driver.clickOn(loginbutton);
        return new SecureArea(driver);
    }

    public LoginPage loginExpectingError() {
        driver.clickOnAndWaitFor(loginbutton, messageBox);
        return this;
    }

    public String getMessageText() {
        return ((driver.findElements(messageBox).size() > 0) ? driver.findElement(messageBox).getText() : "");
    }
}
