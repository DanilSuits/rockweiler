/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory;

import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.EventStore;
import com.vocumsineratio.eventstore.EventStoreConnection;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.eventstore.persistence.async.AsyncEventStore;
import com.vocumsineratio.eventstore.persistence.async.EventGateway;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ConnectionBuilder<Ref> {

    private Map<StreamId, CachedStream> initialState = Collections.EMPTY_MAP;

    private ExecutorService executorService;

    public ConnectionBuilder connectTo(Map<StreamId, CachedStream> initialState) {
        this.initialState = initialState;
        return this;
    }

    public ConnectionBuilder with(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public EventStoreConnection connect () {

        Handle<Map<StreamId, CachedStream>> connection = new Handle<Map<StreamId, CachedStream>>(initialState);
        connection.commit(initialState);

        TaskFactory tasks = new TaskFactory(connection, new MemoryStore.Store.Update.Factory() {
            @Override
            public MemoryStore.Store.Update create(StreamId streamId, ExpectedVersion version, Iterable<? extends Event> events) {
                return new SimpleUpdate(streamId,version,events);
            }
        });

        EventGateway<MemoryStore.WriteStatus, MemoryStore.ReadResult> gateway = new EventGateway<>(executorService, tasks);
        Client client = new Client(gateway);

        final AsyncEventStore<MemoryStore.WriteStatus,MemoryStore.ReadResult> eventStore = new AsyncEventStore<>(client);

        return new EventStoreConnection() {
            @Override
            public EventStore get() {
                return eventStore;
            }
        } ;
    }

    public static ConnectionBuilder create () {
        return new ConnectionBuilder();
    }
}
