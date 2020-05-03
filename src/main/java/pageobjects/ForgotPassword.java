package pageobjects;

import framework.DriverWrapper;
import org.openqa.selenium.By;

public class ForgotPassword extends BasePageObject {

    public final String PAGE_URL = BASE_URL + "forgot_password";
    public final String[] PAGE_TITLE = {"The Internet"};

    protected final By emailField = By.id("email");
    protected final By submitButton = By.id("form_submit");

    /**
     * Constructor used by tests to perform an initial page load
     * @param wrapper DriverWrapper instance we should get the WebDriver instance from and use to load this page
     */
    public ForgotPassword(DriverWrapper wrapper) {
        setup(wrapper, true);
    }

    /**
     * Constructor used by other page objects as we navigate around a site
     * @param wrapper DriverWrapper instance which should already be on this page
     */
    protected ForgotPassword(DriverWrapper wrapper, boolean loadPage) {
        setup(wrapper, loadPage);
    }

    private void setup(DriverWrapper wrapper, boolean loadPage) {
        this.driver = wrapper;

        if (loadPage) {
            driver.get(PAGE_URL);
        }

        selfCheckPageTitleContains(PAGE_TITLE);
    }

    public ForgotPassword enterEmail(String emailAddress) {
        setText(emailField, emailAddress);
        return this;
    }

    public EmailSent submitFormWithButton() {
        driver.clickOn(submitButton);
        return new EmailSent(driver);
    }

    public EmailSent submitFormWithReturnKey() {
        driver.findElement(emailField).submit();
        return new EmailSent(driver);
    }
}