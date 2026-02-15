package com.rj.utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RjUtility {

    public String getIndex() {
        try {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("index.html")) {
                if (is == null) return "";
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
