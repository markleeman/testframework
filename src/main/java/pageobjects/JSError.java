package pageobjects;

import framework.DriverWrapper;

public class JSError extends BasePageObject {

    public final String PAGE_URL = BASE_URL + "javascript_error";
    public final String[] PAGE_TITLE = {"Page with JavaScript errors on load"};

    public JSError(DriverWrapper wrapper) {
        setup(wrapper, true);
    }

    private void setup(DriverWrapper wrapper, boolean loadPage) {
        this.driver = wrapper;

        if (loadPage) {
            driver.get(PAGE_URL);
        }

        selfCheckPageTitleContains(PAGE_TITLE);
    }
}
