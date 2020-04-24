package framework.enums;

import org.openqa.selenium.remote.BrowserType;

/**
 * Web browsers supported by the framework
 */
public enum SupportedBrowsers {
    FIREFOX (BrowserType.FIREFOX),
    CHROME (BrowserType.CHROME),
    CHROME_HEADLESS ("chrome_headless"),
    SAFARI (BrowserType.SAFARI),
    IE11 (BrowserType.IEXPLORE),
    EDGE (BrowserType.EDGE);

    public final String browserName;

    SupportedBrowsers(String browserName){
        this.browserName = browserName;
    }

    public static SupportedBrowsers fromString(String stringValue) {

        for (SupportedBrowsers browser : SupportedBrowsers.values()) {
            if (browser.browserName.equalsIgnoreCase(stringValue)) {
                return browser;
            }
        }

        return null;
    }
}