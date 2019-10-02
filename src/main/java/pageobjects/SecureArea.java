package pageobjects;

import framework.DriverWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SecureArea extends BasePageObject {

    public final String PAGE_URL = BASE_URL + "secure";
    public final String[] PAGE_TITLE = {"The Internet"};

    protected final By logoutbutton = By.className("icon-signout");
    protected final By messageBox = By.id("flash");

    /**
     * Constructor used by other page objects as we navigate around a site
     * @param driver
     */
    protected SecureArea(RemoteWebDriver driver) {
        this.driver = driver;

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.presenceOfElementLocated(logoutbutton));

        selfCheckPageTitleContains(PAGE_TITLE);
    }

    public String getMessageText() {
        return ((driver.findElements(messageBox).size() > 0) ? driver.findElement(messageBox).getText() : "");
    }
}
