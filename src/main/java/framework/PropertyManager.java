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
        String val = props.getProperty(key);

        if (val == null || val.equals("")) {
            throw new IllegalStateException("No value set for property: " + key);
        }

        return val;
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
