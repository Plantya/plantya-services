package com.agrosentinel.auth.logging;

import io.quarkiverse.loggingjson.JsonGenerator;
import io.quarkiverse.loggingjson.JsonProvider;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logmanager.ExtLogRecord;

import java.io.IOException;
import java.util.Map;

@ApplicationScoped
public class JsonLoggingProvider implements JsonProvider {

    @Override
    public void writeTo(JsonGenerator generator, ExtLogRecord event) throws IOException {
        Map<String, String> mdc = event.getMdcCopy();

        generator.writeStringField("Correlation-ID", mdc.getOrDefault("Correlation-ID", ""));
        generator.writeStringField("request_data", mdc.getOrDefault("request_data", ""));
        generator.writeStringField("response_data", mdc.getOrDefault("response_data", ""));
    }

}