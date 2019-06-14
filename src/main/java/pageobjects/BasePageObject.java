package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

public class BasePageObject {

    RemoteWebDriver driver;

    protected By browserError = By.cssSelector("browser:error");

    // Simple checks to make sure we are in the right place before we start doing anything
    protected void selfCheckPageTitleContains(String pageTitle) {
        if (! driver.getTitle().contains(pageTitle)) {
            throw new IllegalStateException("Page title does not match");
        }
    }

    protected void selfCheckPageURLContains(String url) {
        if (! driver.getCurrentUrl().contains(url)) {
            throw new IllegalStateException("Page url does not match");
        }
    }

    public int getNumBrowserErrors() {
        return driver.findElements(browserError).size();
    }

    public boolean isBrowserErrorPresent() {
        return getNumBrowserErrors() > 0;
    }
}