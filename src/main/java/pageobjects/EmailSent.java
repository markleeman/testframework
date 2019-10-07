package pageobjects;

import framework.DriverWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EmailSent extends BasePageObject{

    public final String PAGE_URL = BASE_URL + "email_sent";
    public final String[] PAGE_TITLE = {"The Internet"};

    protected final By messageDiv = By.id("content");

    /**
     * Constructor used by tests to perform an initial page load
     * @param wrapper DriverWrapper instance we should get the WebDriver instance from and use to load this page
     */
    public EmailSent(DriverWrapper wrapper) {
        this.driver = wrapper.getDriver();
        driver.get(PAGE_URL);
        selfCheckPageTitleContains(PAGE_TITLE);
    }

    /**
     * Constructor used by other page objects as we navigate around a site
     * @param driver WebDriver instance which should already be on this page
     */
    protected EmailSent(RemoteWebDriver driver) {
        this.driver = driver;
        selfCheckPageTitleContains(PAGE_TITLE);
    }

    public String getMessageText() {

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.presenceOfElementLocated(messageDiv));

        return driver.findElement(messageDiv).getText();
    }
}
