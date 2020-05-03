package framework;

import framework.enums.SupportedBrowsers;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Wrapper for the WebDriver instance providing
 */
public class DriverWrapper {

    private RemoteWebDriver driver;
    private SupportedBrowsers driverBrowser;
    public Waits waitFor;

    protected DriverWrapper(RemoteWebDriver driver, SupportedBrowsers driverBrowser) {
        this.driver = driver;
        this.driverBrowser = driverBrowser;
        waitFor = new Waits(driver);
    }

    // Make sure the driver is closed properly
    protected void finalize() throws Throwable {

        if (driver != null) {
            shutDown();
        }

        super.finalize();
    }

    public void shutDown(){
        driver.quit();
        driver = null;
    }

    public Boolean browserIs(SupportedBrowsers browser) {
        return driverBrowser == browser;
    }

    public void get(String pageURL) {
        driver.get(pageURL);
    }

    public void switchToTab(String tab){
        driver.switchTo().window(tab);
    }

    public void closeTab(String tab){
        switchToTab(tab);
        // TODO actually close the tab...
    }

    public Set<String> getTabs(){
        return driver.getWindowHandles();
    }

    public void deleteAllCookies(){
        driver.manage().deleteAllCookies();
    }

    public void setCookie(String cookieName, String cookieValue){
        Cookie cookie = new Cookie(cookieName, cookieValue);
        driver.manage().addCookie(cookie);
    }

    public void deleteCookie(String cookieName){
        driver.manage().deleteCookieNamed(cookieName);
    }

    public String getCookie(String cookieName){
        return driver.manage().getCookieNamed(cookieName).getValue();
    }

    public void clearLocalStorage() {
        JavascriptExecutor jsExe = driver;
        jsExe.executeScript("window.localStorage.clear();");
    }

    public void clearSessionStorage() {
        JavascriptExecutor jsExe = driver;
        jsExe.executeScript("window.sessionStorage.clear();");
    }

    public String getLocalStorage(String storageKey){
        JavascriptExecutor jsExe = driver;
        return (String) jsExe.executeScript("return localStorage." + storageKey + ";");
    }

    public String getSessionStorage(String storageKey){
        JavascriptExecutor jsExe = driver;
        return (String) jsExe.executeScript("return sessionStorage." + storageKey + ";");
    }

    /**
     * Runs a supplied JavaScript statement and returns the result as a string
     */
    public String executeJavascript(String js){
        JavascriptExecutor jsExe = driver;
        return (String) jsExe.executeScript(js);
    }

    public SupportedBrowsers getDriverBrowser(){
        return driverBrowser;
    }

    /**
     * Saves a screenshot of the browser window as a png with the provided filename.  The path to the screeshots folder
     * should be specified in the config file
     * @param imageName Filename for the screenshot without file extension
     */
    public void takeScreenShot(String imageName){

        File scrFile = driver.getScreenshotAs(OutputType.FILE);

        String folderPath = new PropertyManager().getScreenshotPath();

        int fileNum = 0;
        File screenshot;

        // Keep incrementing the file number until we get a unique filename
        do {
            // Write the image to a file
            screenshot = new File(folderPath + File.separator + imageName + "-" + fileNum + ".png");
            fileNum++;
        }
        while(screenshot.isFile());

        try {
            FileUtils.copyFile(scrFile, screenshot);
        } catch (IOException e) {
            System.out.println("Error saving screenshot");
            e.printStackTrace();
        }
    }

    public boolean urlContains(String search) {
        return driver.getCurrentUrl().contains(search);
    }

    public boolean pageTitleContains(String search) {
        return driver.getTitle().contains(search);
    }

    // *****************************************************************************************************************
    // Wrapper methods for common WebDriver functionality
    // *****************************************************************************************************************

    /**
     * Clicks on the element located by the provided By locator
     * @param locator Locator for the element to click on
     */
    public void clickOn(By locator){
        clickOn(driver.findElement(locator));
    }

    public void clickOn(WebElement element){
        element.click();
    }

    public void clickOnAndWaitFor(By clickOnLocator, By waitForLocator){
        clickOn(clickOnLocator);
        waitFor.elementToBePresent(waitForLocator);
    }

    public void okAlert() {
        try {
            driver.switchTo().alert().accept();
        }
        catch (NoAlertPresentException e) {
            // Just carry on
        }
    }

    public WebElement findElement (By locator) {
        return driver.findElement(locator);
    }

    public List<WebElement> findElements (By locator) {
        return driver.findElements(locator);
    }

    public String getTitle() {
        return driver.getTitle();
    }
}