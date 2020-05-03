package pageobjects;

import framework.DriverWrapper;
import org.openqa.selenium.By;

public class EmailSent extends BasePageObject{

    public final String PAGE_URL = BASE_URL + "email_sent";
    public final String[] PAGE_TITLE = {"The Internet"};

    protected final By messageDiv = By.id("content");

    /**
     * Constructor used by tests to perform an initial page load
     * @param wrapper DriverWrapper instance we should get the WebDriver instance from and use to load this page
     */
    public EmailSent(DriverWrapper wrapper) {
        setup(wrapper, true);
    }

    /**
     * Constructor used by other page objects as we navigate around a site
     * @param wrapper WebDriver instance which should already be on this page
     */
    protected EmailSent(DriverWrapper wrapper, boolean loadPage) {
        setup(wrapper, loadPage);
    }

    private void setup(DriverWrapper wrapper, boolean loadPage) {
        this.driver = wrapper;

        if(loadPage) {
            driver.get(PAGE_URL);
        }

        selfCheckPageTitleContains(PAGE_TITLE);
    }


    public String getMessageText() {

        driver.waitFor.elementToBePresent(messageDiv);
        return driver.findElement(messageDiv).getText();
    }
}
