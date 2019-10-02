package framework;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

/**
 * Creates a new RemoteWebDriver using the supplied browser and OS and provides a number of helper methods
 * for interacting with it
 */
public class DriverWrapper {

    private RemoteWebDriver driver;
    private browsers driverBrowser;
    private boolean useGrid;

    private final String geckoDriver = "geckodriver";
    private final String chromeDriver = "chromedriver";
    private final String ieDriver = "IEDriverServer";

    /**
     * Create a new driver by explicitly stating the browser we want
     * @param browser
     * @param useSeleniumGrid
     */
    public DriverWrapper(browsers browser, boolean useSeleniumGrid){
        driverBrowser = browser;
        useGrid = useSeleniumGrid;
        setup();
    }

    /**
     * Create a new driver by explicitly stating the browser we want and running locally
     * @param browser
     */
    public DriverWrapper(browsers browser){
        driverBrowser = browser;
        useGrid = false;
        setup();
    }

    /**
     * Create our new driver based on system properties
     */
    public static DriverWrapper createFromSystemProperties(){

        browsers browser = browsers.fromString(System.getProperty("framework.browser"));
        boolean useGrid = Boolean.parseBoolean(System.getProperty("framework.useSeleniumGrid"));

        return new DriverWrapper(browser, useGrid);
    }

    /**
     * Setup our new driver
     */
    private void setup() {

        PropertyManager props = new PropertyManager();

        // If no browser has been specified throw an exception
        if (driverBrowser == null) {
            throw new IllegalStateException("No browser has been specified");
        }

        if (useGrid) {
            URL seleniumHubUrl;

            try {
                seleniumHubUrl = new URL(props.getHubURL());
            } catch (IOException e) {
                throw new IllegalStateException("Selenium Hub URL invalid or not set");
            }

            switch(driverBrowser) {

                case FIREFOX:
                    driver = new RemoteWebDriver(seleniumHubUrl, new FirefoxOptions());
                    break;

                case CHROME_HEADLESS:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.setHeadless(true);
                    chromeOptions.addArguments("window-size=1920,1200");
                    driver = new RemoteWebDriver(seleniumHubUrl, chromeOptions);
                    break;

                case CHROME:
                    driver = new RemoteWebDriver(seleniumHubUrl, new ChromeOptions());
                    break;

                case IE11:
                    driver = new RemoteWebDriver(seleniumHubUrl, new InternetExplorerOptions());
                    break;

                case EDGE:
                    driver = new RemoteWebDriver(seleniumHubUrl, new EdgeOptions());
                    break;

                case SAFARI:
                    driver = new RemoteWebDriver(seleniumHubUrl, new SafariOptions());
                    break;
            }

            driver.setFileDetector(new LocalFileDetector());
        }

        else {

            String fileExtension = "";

            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                fileExtension = ".exe";
            }

            switch(driverBrowser) {

                case FIREFOX:
                    System.setProperty("webdriver.gecko.driver", props.getDriverFolder() + geckoDriver + fileExtension);
                    driver = new FirefoxDriver();
                    break;

                case CHROME_HEADLESS:
                    System.setProperty("webdriver.chrome.driver", props.getDriverFolder() + chromeDriver + fileExtension);
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.setHeadless(true);
                    chromeOptions.addArguments("window-size=1920,1200");
                    driver = new ChromeDriver(chromeOptions);
                    break;

                case CHROME:
                    System.setProperty("webdriver.chrome.driver", props.getDriverFolder() + chromeDriver + fileExtension);
                    driver = new ChromeDriver();
                    break;

                case IE11:
                    // Don't forget to set the below registry key or we'll keep loosing the connection to the browser
                    // For 32bit machines
                    // HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Internet Explorer\Main\FeatureControl\FEATURE_BFCACHE
                    // For 64bit machines
                    // HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Microsoft\Internet Explorer\Main\FeatureControl\FEATURE_BFCACHE
                    // FEATURE_BFCACHE sub-key should contain a DWORD value named iexplore.exe with the value of 0
                    // Additionally, the Protected Mode value must the same for all zones under Internet options -> Security
                    // TODO include this in a readme
                    System.setProperty("webdriver.ie.driver", props.getDriverFolder() + ieDriver + fileExtension);
                    driver = new InternetExplorerDriver();
                    break;

                case EDGE:
                    // Edge driver for versions 18+ is now an optional feature in windows.
                    // Search for Manage Optional Features and add Microsoft Webdriver
                    // Domain joined computers may have to bypass their WSUS server before this can be installed by
                    // editing the following registry key
                    // HKEY_LOCAL_MACHINE\SOFTWARE\Policies\Microsoft\Windows\WindowsUpdate\AU
                    // If UseWUServer exists set it's value to 0, then restart and install Microsoft Webdriver normally
                    // TODO include this in a readme
                    EdgeOptions edgeOptions = new EdgeOptions();
                    driver = new EdgeDriver(edgeOptions);
                    break;

                case SAFARI:
                    // Need to Allow Remote Automation from the Develop menu
                    // Develop menu needs to be enabled from Preferences -> Advanced
                    // TODO include this in a readme
                    driver = new SafariDriver();
                    break;
            }
        }

        // Maximise the window
        driver.manage().window().maximize();

        System.out.println("Starting new " + driver.getCapabilities().getBrowserName() + " driver");
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
    public RemoteWebDriver getDriver(){
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

    /**
     * Saves a screenshot of the browser window with the provided filename.  The path to the screeshots folder
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

    /**
     * Web browsers supported by the framework
     */
    public enum browsers {
        FIREFOX (BrowserType.FIREFOX),
        CHROME (BrowserType.CHROME),
        CHROME_HEADLESS ("chrome_headless"),
        SAFARI (BrowserType.SAFARI),
        IE11 (BrowserType.IEXPLORE),
        EDGE (BrowserType.EDGE);

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
}