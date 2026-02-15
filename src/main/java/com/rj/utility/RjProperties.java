package com.rj.utility;
import java.io.IOException;
import java.util.Properties;

public class RjProperties {
    
    public String getProp(String key) {
        Properties props = new Properties();

        try {
            props.load(RjProperties.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return props.getProperty(key);
        
    }
}
