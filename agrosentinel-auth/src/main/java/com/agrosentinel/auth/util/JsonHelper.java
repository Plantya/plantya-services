package com.agrosentinel.auth.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonHelper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object o, int passwordLength) {
        try {
            ObjectNode node = mapper.valueToTree(o);

            if (node.has("password")) {
                node.put("password", "*".repeat(passwordLength));
            }

            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            return "{}";
        }
    }

}
