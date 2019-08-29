package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Basic page object class containing methods which are applicable to any web page
 */
public class BasePageObject {

    WebDriver driver;

    protected By browserError = By.cssSelector("browser:error");

    /**
     * Checks the current page title against an array of possible values to give us some assurance we are on the
     * correct page before we do anything
     * @param pageTitle String array of possible page titles to check against the current page
     */
    protected void selfCheckPageTitleContains(String[] pageTitle) {

        boolean titleMatch = false;

        for (String title : pageTitle) {

            if (! driver.getTitle().contains(title)) {
                titleMatch = true;
            }
        }

        if (! titleMatch) {
            throw new IllegalStateException("Page title does not match");
        }
    }

    /**
     * Checks for the presence of the supplied string in the current URL to give us some assurance that we are
     * in the correct place before we do anything
     * @param url Full or partial URL to check against the current URL
     */
    protected void selfCheckPageURLContains(String url) {
        if (! driver.getCurrentUrl().contains(url)) {
            throw new IllegalStateException("Page url does not match");
        }
    }

    /**
     * Returns the number of form fields failing the browsers validation checks
     * @return Number of form fields with errors
     */
    public int getNumBrowserErrors() {
        return driver.findElements(browserError).size();
    }

    /**
     * Returns true if there are one or more form fields failing browser validation
     * @return True if one or more form fields contains an error
     */
    public boolean isBrowserErrorPresent() {
        return getNumBrowserErrors() > 0;
    }
}