package framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {

    private Properties props;

    public PropertyManager() {
        props = new Properties();

        environment env = environment.fromString(System.getProperty("framework.config"));
        assert env != null;

        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream(env.configFile);
            props.load(input);
        } catch (IOException e) {
            // Pretend nothing happened and carry on
            // TODO something helpful
        }
    }

    public String getHubURL() {
        return props.getProperty("hub_url");
    }

    public String getDriverFolder() {
        return props.getProperty("driver_folder");
    }

    public String getBaseURL() {
        return props.getProperty("base_url");
    }

    public String getEmailDomain() {
        return props.getProperty("email_domain");
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
