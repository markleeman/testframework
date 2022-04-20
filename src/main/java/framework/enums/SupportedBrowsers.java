package framework.enums;

import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.BrowserType;

/**
 * Web browsers supported by the framework
 */

// TODO: Most/all browsers should now support headless mode, so we should make this an option rather than specified browser type

public enum SupportedBrowsers {
    FIREFOX (Browser.FIREFOX.browserName()),
    CHROME (Browser.CHROME.browserName()),
    CHROME_HEADLESS ("chrome_headless"),
    SAFARI (Browser.SAFARI.browserName()),
    EDGE (Browser.EDGE.browserName());

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