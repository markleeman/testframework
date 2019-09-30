package pageobjects;

import framework.PropertyManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Basic page object class containing methods which are applicable to any web page
 */
public class BasePageObject {

    protected WebDriver driver;
    protected String BASE_URL;
    protected PropertyManager props;

    protected By browserError = By.cssSelector("browser:error");

    protected BasePageObject() {

        props = new PropertyManager();
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
            waitForPageTitle(pageTitles, 5);
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

    /**
     * Clicks on the element located by the provided By locator
     * @param locator
     */
    protected void clickElement(By locator){
        driver.findElement(locator).click();
    }

    /**
     * Clicks on the element located by the provided By locator, and then waits for a second element to be in a clickable state
     * @param clickOnElement
     * @param waitForElement
     */
    protected void clickElementAndWaitForElementToBeClickable(By clickOnElement, By waitForElement){

        clickElement(clickOnElement);

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.elementToBeClickable(waitForElement));
    }

    /**
     * Clicks on the element located by the provided By locator, and then waits for a second element to be present in the DOM
     * @param clickOnElement
     * @param waitForElement
     */
    protected void clickElementAndWaitForElementToBePresent(By clickOnElement, By waitForElement){

        clickElement(clickOnElement);

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.presenceOfElementLocated(waitForElement));
    }

    /**
     * Sends the supplied keystrokes to the supplied element
     * @param element
     * @param text
     */
    protected void setText(By element, String text) {
        driver.findElement(element).clear();
        driver.findElement(element).sendKeys(text);
    }

    /**
     * Keep check the page title for the supplied number of seconds until it matches one of the supplied strings
     * @param expectedTitles
     * @param timeOut
     */
    protected void waitForPageTitle(String[] expectedTitles, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until((ExpectedCondition<Boolean>) driver -> {

            boolean titleMatches = false;

            for(String expectedTitle : expectedTitles){
                if (this.driver.getTitle().contains(expectedTitle)){
                    titleMatches = true;
                }
            }

            return titleMatches;
        });
    }
}