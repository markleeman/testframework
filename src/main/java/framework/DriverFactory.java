package framework;

import framework.enums.SupportedBrowsers;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.io.IOException;
import java.net.URL;

/**
 * Class contains all the methods to initialize a driver instance
 */
public class DriverFactory {

    private DriverFactory() {

    }

    /**
     * Create a new driver by explicitly stating the browser we want
     * @param driverBrowser Type of browser we want to startup
     */
    public static DriverWrapper createLocalDriver(SupportedBrowsers driverBrowser){
        return setup(driverBrowser, false);
    }

    /**
     * Create a new driver by explicitly stating the browser we want and running locally
     * @param driverBrowser  Type of browser we want to startup
     */
    public static DriverWrapper createGridDriver(SupportedBrowsers driverBrowser){
        return setup(driverBrowser, true);
    }

    /**
     * Create our new driver based on system properties
     */
    public static DriverWrapper createDriverFromSystemProperties(){

        SupportedBrowsers driverBrowser = SupportedBrowsers.fromString(System.getProperty("framework.browser"));
        boolean useGrid = Boolean.parseBoolean(System.getProperty("framework.useSeleniumGrid"));

        return setup(driverBrowser, useGrid);
    }

    /**
     * Create our driver based on config properties
     */
    public static DriverWrapper createDriverFromConfig(){

        PropertyManager props = new PropertyManager();
        SupportedBrowsers driverBrowser = SupportedBrowsers.fromString(props.getDriverBrowser());
        boolean useGrid = Boolean.parseBoolean(props.getUseSeleniumGrid());

        return setup(driverBrowser, useGrid);
    }

    /**
     * Setup our new driver
     */
    private static DriverWrapper setup(SupportedBrowsers driverBrowser, boolean useGrid) {

        RemoteWebDriver driver;
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

                case IE11:
                    driver = new RemoteWebDriver(seleniumHubUrl, new InternetExplorerOptions());
                    break;

                case EDGE:
                    driver = new RemoteWebDriver(seleniumHubUrl, new EdgeOptions());
                    break;

                case SAFARI:
                    driver = new RemoteWebDriver(seleniumHubUrl, new SafariOptions());
                    break;

                case CHROME:
                default:
                    driver = new RemoteWebDriver(seleniumHubUrl, new ChromeOptions());
                    break;
            }

            driver.setFileDetector(new LocalFileDetector());
        }

        else {

            final String geckoDriver = "/geckodriver";
            final String chromeDriver = "/chromedriver";
            final String ieDriver = "/IEDriverServer";

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

                case CHROME:
                default:
                    System.setProperty("webdriver.chrome.driver", props.getDriverFolder() + chromeDriver + fileExtension);
                    driver = new ChromeDriver();
                    break;
            }
        }

        // Maximise the window
        driver.manage().window().maximize();

        System.out.println("Starting new " + driver.getCapabilities().getBrowserName() + " driver");

        return new DriverWrapper(driver, driverBrowser);
    }
}
