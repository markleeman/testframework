package pageobjects;

import framework.DriverWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ForgotPassword extends BasePageObject {

    public final String PAGE_URL = BASE_URL + "forgot_password";
    public final String[] PAGE_TITLE = {"The Internet"};

    protected final By emailField = By.id("email");
    protected final By submitButton = By.id("form_submit");

    /**
     * Constructor used by tests to perform an initial page load
     * @param wrapper
     */
    public ForgotPassword(DriverWrapper wrapper) {
        this.driver = wrapper.getDriver();
        driver.get(PAGE_URL);
        selfCheckPageTitleContains(PAGE_TITLE);
    }

    /**
     * Constructor used by other page objects as we navigate around a site
     * @param driver
     */
    protected ForgotPassword(RemoteWebDriver driver) {
        this.driver = driver;
        selfCheckPageTitleContains(PAGE_TITLE);
    }

    public ForgotPassword enterEmail(String emailAddress) {
        setText(emailField, emailAddress);
        return this;
    }

    public EmailSent submitFormWithButton() {
        clickElement(submitButton);
        return new EmailSent(driver);
    }

    public EmailSent submitFormWithReturnKey() {
        driver.findElement(emailField).submit();
        return new EmailSent(driver);
    }
}