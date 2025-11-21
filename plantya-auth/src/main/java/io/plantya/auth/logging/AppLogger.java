package io.plantya.auth.logging;

import org.jboss.logging.Logger;
import org.jboss.logmanager.MDC;

import java.util.UUID;

import static io.plantya.auth.util.JsonHelper.toJson;

public class AppLogger {

    private static final Logger log = Logger.getLogger(AppLogger.class);

    public static void start(String message, Object requestData, int passwordLength) {
        MDC.put("Correlation-ID", UUID.randomUUID().toString());
        MDC.put("request_data", toJson(requestData, passwordLength));

        log.infof("[START] %s", message);
    }

    public static void end(String message, Object responseData, int passwordLength) {
        MDC.put("response_data", toJson(responseData, passwordLength));

        log.infof("[END] %s", message);
        MDC.clear();
    }

    public static void error(String message, Throwable e) {
        log.errorf(e, "[ERROR] %s", message);
    }

    public static void info(String message) {
        log.infof("[INFO] %s", message);
    }

}