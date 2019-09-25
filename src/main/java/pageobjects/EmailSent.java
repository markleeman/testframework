package pageobjects;

import framework.DriverWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EmailSent extends BasePageObject{

    public final String PAGE_URL = BASE_URL + "email_sent";
    public final String[] PAGE_TITLE = {"The Internet"};

    protected final By messageDiv = By.id("content");

    /**
     * Constructor used by tests to perform an initial page load
     * @param wrapper
     */
    public EmailSent(DriverWrapper wrapper) {
        this.driver = wrapper.getDriver();
        driver.get(PAGE_URL);
        selfCheckPageTitleContains(PAGE_TITLE);
    }

    /**
     * Constructor used by other page objects as we navigate around a site
     * @param driver
     */
    protected EmailSent(WebDriver driver) {
        this.driver = driver;
        selfCheckPageTitleContains(PAGE_TITLE);
    }

    public String getMessageText() {

        // TODO wait for element
        return driver.findElement(messageDiv).getText();
    }
}
