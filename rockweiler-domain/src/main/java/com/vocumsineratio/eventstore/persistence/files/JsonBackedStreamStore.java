/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.files;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.eventstore.persistence.CachedStream;
import com.vocumsineratio.eventstore.persistence.StreamStore;
import com.vocumsineratio.eventstore.persistence.memory.StreamPosition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JsonBackedStreamStore implements StreamStore {
    private final File storeHome;
    private final ObjectMapper om;
    private final Adapter adapter;

    public JsonBackedStreamStore(File storeHome, ObjectMapper om, Adapter adapter) {
        this.storeHome = storeHome;
        this.om = om;
        this.adapter = adapter;
    }

    @Override
    public CachedStream read(StreamId key) {
        File source = toFile(key);
        try {
            final JsonNode jsonNode = om.readTree(source);
            List<Event> events = parse(jsonNode);
            final StreamPosition position = new StreamPosition(key, ExpectedVersion.of(events.size()));
            return new CachedStream(position, events);

        } catch (FileNotFoundException e) {
            return CachedStream.emptyStream(key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(key.streamId, e);
        } catch (IOException e) {
            throw new RuntimeException(key.streamId, e);
        }
    }

    private File toFile(StreamId key) {
        return new File(storeHome, key.streamId);
    }

    private List<Event> parse(JsonNode jsonNode) {
        return adapter.parse(jsonNode);
    }

    @Override
    public void store(StreamId key, CachedStream value) {
        File source = toFile(key);
        try {
            final JsonNode json = adapter.parse(value.history);
            om.writeValue(source, json);
        } catch (Exception e) {
            throw new RuntimeException(key.streamId, e);
        }

    }

    public interface Adapter {
        List<Event> parse(JsonNode json);
        JsonNode parse(List<Event> events);
    }
}
