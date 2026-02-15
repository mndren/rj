package com.rj.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rj.App;
import com.rj.json.Health;

import java.io.IOException;
import java.util.Properties;

public class HealthHandler {

    public String getInfo() {
        Properties props = new Properties();
        try {
            props.load(HealthHandler.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String version = props.getProperty("version");
        String applicationName= props.getProperty("application.name");

        try {
            return Health.toJson("ok", applicationName, version);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
