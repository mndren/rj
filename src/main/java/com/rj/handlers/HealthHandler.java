package com.rj.handlers;
import com.rj.json.Health;
import com.rj.utility.RjProperties;

public class HealthHandler {

    public String getInfo() {
        RjProperties rp = new RjProperties();
        return Health.toJson("ok", rp.getProp("application.name"), rp.getProp("version"));
    }
}
