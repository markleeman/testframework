package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Basic page object class containing methods which are applicable to any web page
 */
public class BasePageObject {

    protected WebDriver driver;
    protected String BASE_URL;

    protected By browserError = By.cssSelector("browser:error");

    protected BasePageObject() {
        Properties props = new Properties();

        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
            props.load(input);
        } catch (IOException e) {
            // Pretend nothing happened and carry on
            // TODO something helpful
        }

        BASE_URL = props.getProperty("base_url");
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

    protected void setText(By element, String text) {
        driver.findElement(element).clear();
        driver.findElement(element).sendKeys(text);
    }

    public void waitForPageTitle(String[] expectedTitles, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until((ExpectedCondition<Boolean>) driver -> {

            boolean titleMatches = false;

            for(String expectedTitle : expectedTitles){
                if (driver.getTitle().contains(expectedTitle)){
                    titleMatches = true;
                }
            }

            return titleMatches;
        });
    }
}