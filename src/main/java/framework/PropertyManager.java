package framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class is responsible for providing config values to the rest of the framework.  If we have multiple test environments
 * each one should have it's own config file, and we use the framework.config system property to define which one we're
 * using at runtime.
 */
public class PropertyManager {

    private Properties props;

    public PropertyManager() {
        props = new Properties();

        environment env = environment.fromString(System.getProperty("framework.config"));

        if (env == null) {
            env = environment.TEST;
            System.out.println("framework.config property not specified, defaulting to '" + env.envName + "'");
        }

        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream(env.configFile);
            props.load(input);
        } catch (IOException e) {
            // Pretend nothing happened and carry on
            // TODO something helpful
        }
    }

    // TODO verify property values have been set
    public String getHubURL() {
        return props.getProperty("hub_url");
    }

    public String getDriverFolder() {
        return props.getProperty("driver_folder");
    }

    public String getBaseURL() {
        return props.getProperty("base_url");
    }

    public String getEmailPrefix() {
        return props.getProperty("email_prefix");
    }

    public String getEmailDomain() {
        return props.getProperty("email_domain");
    }

    public String getScreenshotPath() { return props.getProperty("screenshot_path"); }

    public String getImapHost() { return props.getProperty("imap_host"); }

    public int getImapPort() { return Integer.parseInt(props.getProperty("imap_port")); }

    public String getImapUsername() { return props.getProperty("imap_username"); }

    public String getImapPassword() { return props.getProperty("imap_password"); }

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

        public final String configFile;
        public final String envName;

        environment(String configFile, String envName){
            this.configFile = configFile;
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
