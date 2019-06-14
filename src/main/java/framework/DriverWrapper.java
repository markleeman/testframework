package framework;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * Creates a new RemoteWebDriver using the supplied browser and OS and provides a number of helper methods
 * for interacting with it
 */
public class DriverWrapper {

    private RemoteWebDriver driver;
    private static final String HUB_URL = "http://localhost:4444/wd/hub";
    // TODO: Load the Hub URL from a properties file
    private browsers driverBrowser;
    private operatingSystems driverOS;

    /**
     * Create a new driver by explicitly stating the browser and OS we want
     * @param browser
     * @param os
     */
    DriverWrapper(browsers browser, operatingSystems os){
        setup(browser, os);
    }

    /**
     * Create a new driver by explicitly stating the browser we want
     * We'll take whatever OS the grid gives us
     * @param browser
     */
    DriverWrapper(browsers browser){
        setup(browser, null);
    }

    /**
     * Create our new driver based on system properties
     */
    public static DriverWrapper createFromSystemProperties(){

        browsers browser = browsers.fromString(System.getProperty("framework.browser"));
        operatingSystems os = operatingSystems.fromString(System.getProperty("framework.os"));

        return new DriverWrapper(browser, os);
    }

    /**
     * Setup our new driver based on the supplied browser and os
     * @param browser
     * @param os
     */
    private void setup(browsers browser, operatingSystems os){

        // OS isn't critical, but we do need the user to specify a browser so we'll throw an exception if they haven't
        if (browser == null) {
            throw new IllegalStateException("No browser has been specified");
        }

        driverBrowser = browser;
        driverOS = os;

        URL seleniumHubUrl;

        try {
            seleniumHubUrl = new URL(HUB_URL);
        }
        catch (MalformedURLException e) {
            throw new IllegalStateException("Selenium Hub URL invalid or not set");
        }

        DesiredCapabilities capabilities = setCapabilities();

        driver = new RemoteWebDriver(seleniumHubUrl, capabilities);

        // Maximise the window and enable the driver to upload files
        try {
            if (driverOS != operatingSystems.IOS && driverOS != operatingSystems.ANDROID) {
                driver.manage().window().maximize();
            }
        }
        catch (Exception e) {
            // Not all browsers support maximizing the screen so just catch any
            // exceptions and carry on
            System.out.println("Unable to maximize screen");
        }
        driver.setFileDetector(new LocalFileDetector());

        System.out.println("Starting new " + driver.getCapabilities().getBrowserName() + " driver on " + driver.getCapabilities().getPlatform() + " node");
    }

    /**
     * Specify the capabilities we want from our new driver
     * @return Set of desired capabilities we can send to the Selenium Grid Hub, which will hopefully have a node to match
     */
    private DesiredCapabilities setCapabilities(){
        DesiredCapabilities capabilities = new DesiredCapabilities();

        switch (driverBrowser){

            case IE11:
                capabilities = DesiredCapabilities.internetExplorer();
                capabilities.setVersion("11");
                capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
                capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                break;

            case EDGE:
                capabilities = DesiredCapabilities.edge();
                break;

            default:
                capabilities.setBrowserName(driverBrowser.browserName);

                if (driverOS != null) {
                    capabilities.setPlatform(driverOS.platform);
                }
                capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                capabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
                break;
        }

        return capabilities;
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

    /**
     * Our tests should never interact with the driver directly but we'll provide a method for the
     * page objects to get at it
     */
    protected RemoteWebDriver getDriver(){
        return driver;
    }

    public void switchToTab(String tab){
        driver.switchTo().window(tab);
    }

    public void closeTab(String tab){
        driver.switchTo().window(tab);
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

    public browsers getDriverBrowser(){
        return driverBrowser;
    }

    public operatingSystems getDriverOS(){
        return driverOS;
    }

    public void takeScreenShot(String fullFilePath){

        // TODO get screenshots folder from a properties file so we just need the file name as a parm
        // TODO auto-increment filename if the file already exists
        File scrFile = driver.getScreenshotAs(OutputType.FILE);

        try {
            FileUtils.copyFile(scrFile, new File(fullFilePath));
        } catch (IOException e) {
            System.out.println("Error saving screenshot");
            e.printStackTrace();
        }
    }

    /**
     * Test environments frequently have invalid security certs which most browsers will ignore with the
     * ACCEPT_INSECURE_CERTS capability.  This doesn't work with IE and Edge however, so this method will
     * detect the security warning these browsers throw up and accept them.  It should only be needed for the
     * initial page load as once the warnings have been dismissed the browser will accept the certs for the
     * remainder of the session.
     */
    protected void loadPageAndDealWithCertWarnings(String pageURL){

        WebDriverWait fiveSecWait = new WebDriverWait(driver, 5);

        try {
            driver.get(pageURL);
        }
        catch(UnhandledAlertException e){
            driver.switchTo().alert().accept();
        }

        // Handling for security alerts in IE11
        if (driverBrowser.equals(browsers.IE11)) {

            // IE will occasionally throw up a security alert because of self signed certs
            try {
                fiveSecWait.until(ExpectedConditions.or(ExpectedConditions.alertIsPresent(), ExpectedConditions.titleContains("Certificate Error:")));

                try {
                    driver.switchTo().alert().accept();
                    fiveSecWait.until(ExpectedConditions.not(ExpectedConditions.titleContains("This page can’t be displayed")));
                }
                catch(NoAlertPresentException e) { /* No alert present */ }

                if (driver.getTitle().contains("Certificate Error:")){
                    try {
                        driver.findElement(By.id("overridelink")).click();

                        // And then it might occasionally throw up a security alert
                        Alert securityAlert = fiveSecWait.until(ExpectedConditions.alertIsPresent());
                        securityAlert.accept();
                    }
                    catch (NoSuchElementException e){ /* No alert present */ }
                    catch(TimeoutException e){ /* No alert present */ }
                    catch(NoAlertPresentException e) { /* No alert present */ }
                }

            }
            catch(TimeoutException e){ /* No alert present */ }
        }

        // Handling for security alerts in Edge
        else if (driverBrowser.equals(browsers.EDGE)) {

            if (driver.getTitle().contains("Certificate Error:")){
                try {
                    driver.findElement(By.id("moreInformationDropdownSpan")).click();

                    fiveSecWait.until(ExpectedConditions.presenceOfElementLocated(By.id("invalidcert_continue")));

                    driver.findElement(By.id("invalidcert_continue")).click();
                }
                catch (NoSuchElementException e){ /* No alert present */ }
            }
        }
    }

    public void clickElement(WebElement element){

        // Selenium _should_ scroll the element to be clicked into view, but we'll do so explicitly

        if (driver == null){
            throw new IllegalStateException("Driver has not been initialized");
        }

        JavascriptExecutor jse = driver;

        try {
            jse.executeScript("arguments[0].scrollIntoView(false)", element);
        }
        catch (NullPointerException e) { /* Keep going */ }

        try {
            element.click();
        }
        catch (WebDriverException e){
            // Something has prevented us clicking on the element, usually because a dialog hasn't finished
            // animating yet, so we'll try again using JavaScript
            jse.executeScript("arguments[0].click();", element);
        }
    }

    public void clickElement(By locator){
        if (locator != null) {
            clickElement(driver.findElement(locator));
        }
        else{
            throw new IllegalStateException("Element locator cannot be null");
        }
    }

    public void clickElementAndWaitForElementToBeClickable(By clickOnElement, By waitForElement){

        clickElement(clickOnElement);

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.elementToBeClickable(waitForElement));
    }

    public void clickElementAndWaitForElementToBePresent(By clickOnElement, By waitForElement){

        clickElement(clickOnElement);

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.presenceOfElementLocated(waitForElement));
    }

    /**
     * Web browsers supported by the framework
     */
    public enum browsers {
        FIREFOX ("firefox"),
        CHROME ("chrome"),
        SAFARI ("safari"),
        IE11 ("iexplore"),
        EDGE ("edge");

        public final String browserName;

        browsers(String browserName){
            this.browserName = browserName;
        }

        public static browsers fromString(String stringValue) {

            for (browsers browser : browsers.values()) {
                if (browser.browserName.equalsIgnoreCase(stringValue)) {
                    return browser;
                }
            }

            return null;
        }
    }

    /**
     * Operating systems supported by the framework
     */
    public enum operatingSystems{
        WINDOWS ("windows", Platform.WINDOWS),
        MAC ("macos", Platform.MAC),
        LINUX ("linux", Platform.LINUX),
        IOS ("ios", Platform.IOS),
        ANDROID ("android", Platform.ANDROID);

        public final String osName;
        public final Platform platform;

        operatingSystems (String osName, Platform platform){
            this.osName = osName;
            this.platform = platform;
        }

        public static operatingSystems fromString(String stringValue) {

            for (operatingSystems system : operatingSystems.values()) {
                if (system.osName.equalsIgnoreCase(stringValue)) {
                    return system;
                }
            }

            return null;
        }
    }
}