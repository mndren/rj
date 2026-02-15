package com.rj.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Health {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String status;
    public String name;
    public String version;

    public static String toJson(String status, String name, String version)  {
        ObjectMapper objectMapper = new ObjectMapper();
        Health h = new Health();
        h.setStatus(status);
        h.setName(name);
        h.setVersion(version);
        try {
            return objectMapper.writeValueAsString(h);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}


