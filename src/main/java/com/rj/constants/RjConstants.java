package com.rj.constants;

public class RjConstants {

    public static class API {
        public static final String API_V1_PREFIX = "/api/v1/";

        public static final String API_V1_HEALTH = API_V1_PREFIX + "health";
    }
    public static class SSR {
        public static final String SSR_HEALTH =  "/health";
    }

    public static class RjResponse {
        public static class Status {
            public static final int OK = 200;
            public static final int NOT_FOUND = 404;
            public static final int INTERNAL_SERVER_ERROR=500;
        }
    }

}
