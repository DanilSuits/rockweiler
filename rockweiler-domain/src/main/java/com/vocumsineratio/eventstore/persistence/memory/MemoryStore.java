/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.eventstore.persistence.CachedStream;
import com.vocumsineratio.eventstore.persistence.StreamStore;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class MemoryStore {

    enum WriteStatus {
        SUCCESS,
        FAILED
    }

    /**
     * @author Danil Suits (danil@vast.com)
     */
    public static class Store implements Callable<WriteStatus> {

        private final Handle<Map<StreamId, CachedStream>> connection;
        private final Update update;
        private final Consumer<StreamPosition> observer;

        public Store(Handle connection, Update update, Consumer<StreamPosition> observer) {
            this.connection = connection;
            this.update = update;
            this.observer = observer;
        }

        @Override
        public WriteStatus call() throws Exception {
            LocalStreamStore working = new LocalStreamStore();

            synchronized (connection) {
                working.load(connection.get());
                update.copyTo(working);
                commit(working);
            }

            working.writeTo(this.observer);

            return WriteStatus.SUCCESS;
        }

        public void commit(LocalStreamStore working) {
            connection.commit(working.localStore);
        }

        public interface Update {
            void copyTo(StreamStore working);

            interface Factory {
                Update create(StreamId streamId, ExpectedVersion version, Iterable<? extends Event> events);
            }
        }
    }

    public static class LocalStreamStore implements StreamStore {
        final Map<StreamId, CachedStream> localStore = Maps.newHashMap();

        private final List<CachedStream> writes = Lists.newArrayList();

        void load(Map<StreamId, CachedStream> source) {
            localStore.putAll(source);
        }

        @Override
        public CachedStream read(StreamId key) {
            CachedStream stream = localStore.get(key);
            if (null == stream) {
                stream = CachedStream.emptyStream(key);
            }

            return stream;
        }

        @Override
        public void store(StreamId key, CachedStream value) {
            writes.add(value);
            localStore.put(key,value);
        }

        public void writeTo(Consumer<StreamPosition> observer) {
            for (CachedStream write : writes) {
                observer.accept(write.stream);
            }
        }
    }

    static class ReadResult {
        final Iterable<Event> events;

        ReadResult(Iterable<Event> events) {
            this.events = events;
        }
    }

    static class ReadTask implements Callable<ReadResult> {

        private final Handle<Map<StreamId, CachedStream>> connection;
        private final StreamId streamId;

        public ReadTask(Handle<Map<StreamId, CachedStream>> connection, StreamId streamId) {
            this.streamId = streamId;
            this.connection = connection;
        }

        @Override
        public ReadResult call() throws Exception {
            Map<StreamId, CachedStream> store = connection.get();
            CachedStream stream = store.get(streamId);

            // TODO:
            if (null == stream) {
                return new ReadResult(Collections.EMPTY_LIST);
            }
            return new ReadResult(stream.history);
        }
    }
}
