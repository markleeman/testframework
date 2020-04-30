package pageobjects;

import framework.DriverWrapper;
import org.openqa.selenium.By;

public class SecureArea extends BasePageObject {

    public final String PAGE_URL = BASE_URL + "secure";
    public final String[] PAGE_TITLE = {"The Internet"};

    protected final By logoutbutton = By.className("icon-signout");
    protected final By messageBox = By.id("flash");

    /**
     * Constructor used by other page objects as we navigate around a site
     * @param wrapper WebDriver instance which should already be on this page
     */
    protected SecureArea(DriverWrapper wrapper) {
        this.driver = wrapper;
        driver.waitFor.elementToBePresent(logoutbutton);
        selfCheckPageTitleContains(PAGE_TITLE);
    }

    public String getMessageText() {
        return ((driver.findElements(messageBox).size() > 0) ? driver.findElement(messageBox).getText() : "");
    }
}
