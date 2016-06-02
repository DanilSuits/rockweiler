/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.UUID;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JsonSpike {
    @Test
    public void writeJsonNode () throws Exception {
        ObjectMapper om = new ObjectMapper();
        StringBuilder source = new StringBuilder();
        source.append("[");
        event(source);
        source.append(",");
        event(source);
        source.append("]");

        final JsonNode json = om.readTree(source.toString());
        om.writerWithDefaultPrettyPrinter().writeValue(System.out, json);
    }

    void event(StringBuilder b) {
        Random r = new Random();

        b
                .append("{")
                .append("\"id\":\"").append(UUID.randomUUID().toString()).append("\"")
                .append(",")
                .append("\"eventType\":\"testEvent\"")
                .append(",")
                .append("\"data\":{")
                .append("\"a\":").append(r.nextInt())
                .append(",")
                .append("\"b\":\"").append(r.nextInt()).append("\"")
                .append("}")
                .append(",")
                .append("\"metadata\":{}")
                .append("}");

    }
}
