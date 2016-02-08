/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.async;

import com.vocumsineratio.eventstore.EventStore;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.cqrs.Event;

import java.util.concurrent.Future;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class AsyncEventStore<Write,Read> implements EventStore {
    private final Tasks.AsyncClient<Write,Read> client;

    public AsyncEventStore(Tasks.AsyncClient<Write, Read> client) {
        this.client = client;
    }

    @Override
    public Iterable<Event> get(StreamId streamId) {
        final Future<Read> task = client.get(streamId);
        return client.read(task);
    }

    @Override
    public void store(StreamId streamId, ExpectedVersion version, Iterable<? extends Event> events) {
        final Future<Write> task = client.store(streamId, version, events);
        client.commit(task);
    }
}
