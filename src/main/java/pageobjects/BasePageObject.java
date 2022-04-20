package pageobjects;

import framework.DriverWrapper;
import framework.ConfigManager;
import framework.enums.SupportedBrowsers;
import org.openqa.selenium.*;

/**
 * Basic page object class containing methods which are applicable to any web page
 */
public class BasePageObject {

    protected DriverWrapper driver;
    protected String BASE_URL;
    protected ConfigManager props;

    protected By browserError = By.cssSelector("browser:error");

    protected BasePageObject() {

        props = new ConfigManager();
        BASE_URL = props.getBaseURL();
    }

    /**
     * Checks the current page title against an array of possible values to give us some assurance we are on the
     * correct page before we do anything.  Some browsers can be a little slow or the title could be changed by JS
     * so we'll keep checking for a few seconds before we fail
     * @param pageTitles String array of possible page titles to check against the current page
     */
    protected void selfCheckPageTitleContains(String[] pageTitles) {

        try {
            driver.waitFor.pageTitleToContain(pageTitles, 5L);
        }
        catch (TimeoutException e) {
            StringBuilder expectedTitles = new StringBuilder();

            for (String title : pageTitles) {
                expectedTitles.append("Expected:\t").append(title).append("\n");
            }

            throw new IllegalStateException("Page title does not match\nFound:\t" + driver.getTitle() + "\n" + expectedTitles);
        }
    }

    /**
     * Checks for the presence of the supplied string in the current URL to give us some assurance that we are
     * in the correct place before we do anything
     * @param url Full or partial URL to check against the current URL
     */
    protected void selfCheckPageURLContains(String url) {
        if (! driver.urlContains(url)) {
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

    /**
     * Sends the supplied keystrokes to the supplied element
     * @param element Element to send keystrokes to
     * @param text Keystrokes to send to the element
     */
    protected void setText(By element, String text) {
        driver.findElement(element).clear();
        driver.findElement(element).sendKeys(text);
    }

    /**
     * Test environments frequently have invalid security certs which most browsers will ignore with the
     * ACCEPT_INSECURE_CERTS capability.  This doesn't work with IE and Edge however, so this method will
     * detect the security warning these browsers throw up and accept them.  It should only be needed for the
     * initial page load as once the warnings have been dismissed the browser will accept the certs for the
     * remainder of the session.
     */
    protected void loadPageAndDealWithCertWarnings(String pageURL){

        try {
            driver.get(pageURL);
        }
        catch(UnhandledAlertException e){
            driver.okAlert();
        }

        // Handling for security alerts in Edge
        if (driver.browserIs(SupportedBrowsers.EDGE)) {

            if (driver.pageTitleContains("Certificate Error:")){
                try {
                    driver.clickOnAndWaitFor(By.id("moreInformationDropdownSpan"), By.id("invalidcert_continue"));
                    driver.clickOn(By.id("invalidcert_continue"));
                }
                catch (NoSuchElementException e){ /* No alert present */ }
            }
        }
    }
}