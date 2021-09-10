package framework;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Waits {

    private RemoteWebDriver driver;

    public Waits(RemoteWebDriver driver) {
        this.driver = driver;
    }

    private final Long DEFAULT_TIMEOUT = 5L;

    /**
     * Keep check the page title for the supplied number of seconds until it matches one of the supplied strings
     * @param expectedTitles Array of possible page titles
     * @param timeOutSeconds Length of time in seconds we should wait for
     */
    public void pageTitleToContain(String[] expectedTitles, int timeOutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutSeconds);
        wait.until((ExpectedCondition<Boolean>) driver -> {

            boolean titleMatches = false;

            for(String expectedTitle : expectedTitles) {
                if (driver.getTitle().contains(expectedTitle)) {
                    titleMatches = true;
                }
            }

            return titleMatches;
        });
    }

    public WebElement elementToBePresent(By locator) {
        return elementToBePresent(locator, DEFAULT_TIMEOUT);
    }

    public WebElement elementToBePresent(By locator, Long timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public Alert alertToBePresent() {
        return alertToBePresent(DEFAULT_TIMEOUT);
    }

    public Alert alertToBePresent(Long timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.alertIsPresent());
    }
}
