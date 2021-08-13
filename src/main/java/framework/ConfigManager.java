package framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class is responsible for providing config values to the rest of the framework.  If we have multiple test environments
 * each one should have it's own config file, and we use the framework.config system property to define which one we're
 * using at runtime.
 */
public class ConfigManager {

    private Properties props;

    public ConfigManager() {
        props = new Properties();

        environment env = environment.fromString(System.getProperty("framework.config"));

        if (env == null) {
            env = environment.TEST;
            System.out.println("framework.config property not specified, defaulting to '" + env.envName + "'");
        }

        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream(env.configFileName);
            props.load(input);
        } catch (IOException e) {
            System.out.println("Config file not found");
        }
    }

    public String getHubURL() {
        return getPropValue("hub_url");
    }

    public String getDriverFolder() {
        return getPropValue("driver_folder");
    }

    public String getBaseURL() {
        return getPropValue("base_url");
    }

    public String getEmailUsername() {
        return getPropValue("email_username");
    }

    public String getEmailDomain() { return getPropValue("email_domain"); }

    public String getScreenshotPath() { return getPropValue("screenshot_path"); }

    public String getImapHost() { return getPropValue("imap_host"); }

    public int getImapPort() { return Integer.parseInt(getPropValue("imap_port")); }

    public String getImapUsername() { return getPropValue("imap_username"); }

    public String getImapPassword() { return getPropValue("imap_password"); }

    private String getPropValue(String key) {

        // First, try and get the property from system properties
        String val = System.getProperty(key);

        // If it's not there, check the config
        if (val == null || val.equals("")) {
            val = props.getProperty(key);
        }

        if (val == null || val.equals("")) {
            throw new IllegalStateException("No value set for property: " + key);
        }

        return val;
    }

    public String getDriverBrowser() {
        return props.getProperty("driver_browser");
    }

    public String getUseSeleniumGrid() {
        return props.getProperty("use_selenium_grid");
    }

    public enum environment {
        TEST ("config.properties", "test"),
        STAGING ("config.properties", "staging"),
        PRODUCTION ("config.properties", "production");

        public final String configFileName;
        public final String envName;

        environment(String configFile, String envName){
            this.configFileName = configFile;
            this.envName = envName;
        }

        public static environment fromString(String stringValue) {

            for (environment env : environment.values()) {
                if (env.envName.equalsIgnoreCase(stringValue)) {
                    return env;
                }
            }

            return null;
        }
    }
}
