/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory;

import com.vocumsineratio.eventstore.EventStore;
import com.vocumsineratio.eventstore.EventStoreConnection;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.eventstore.persistence.CachedStream;
import com.vocumsineratio.eventstore.persistence.async.AsyncEventStore;
import com.vocumsineratio.eventstore.persistence.async.EventGateway;
import com.vocumsineratio.eventstore.persistence.memory.simple.SimpleUpdate;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ConnectionBuilder<Ref> {

    private Map<StreamId, CachedStream> initialState = Collections.EMPTY_MAP;

    private ExecutorService executorService;

    private MemoryStore.Store.Update.Factory updateFactory = new SimpleUpdate.Factory();

    private Consumer<StreamPosition> subscriber;

    public ConnectionBuilder<Ref> connectTo(Map<StreamId, CachedStream> initialState) {
        this.initialState = initialState;
        return this;
    }

    public ConnectionBuilder<Ref> with(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public ConnectionBuilder<Ref> using(MemoryStore.Store.Update.Factory factory) {
        this.updateFactory = factory;
        return this;
    }

    public ConnectionBuilder<Ref> onUpdateNotify(Consumer<StreamPosition> subscriber){
        this.subscriber = subscriber;
        return this;
    }

    public EventStoreConnection connect () {

        Handle<Map<StreamId, CachedStream>> connection = new Handle<Map<StreamId, CachedStream>>(initialState);
        connection.commit(initialState);

        Consumer<StreamPosition> observer = new Consumer<StreamPosition>() {
            @Override
            public void accept(final StreamPosition item) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        ConnectionBuilder.this.subscriber.accept(item);
                    }
                });
            }
        } ;

        TaskFactory tasks = new TaskFactory(connection, updateFactory, observer);

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
