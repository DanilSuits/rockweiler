/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory;

import com.google.common.collect.Maps;
import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

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
    static class Store implements Callable<WriteStatus> {

        private final Handle<Map<StreamId, CachedStream>> connection;
        private final Update update;

        public Store(Handle connection, Update update) {
            this.connection = connection;
            this.update = update;
        }

        @Override
        public WriteStatus call() throws Exception {
            synchronized (connection) {
                Map<StreamId, CachedStream> working = Maps.newHashMap(connection.get());
                update.copyTo(working);
                commit(working);
                return WriteStatus.SUCCESS;
            }
        }

        public void commit(Map<StreamId, CachedStream> working) {
            connection.commit(working);
        }

        interface Update {
            void copyTo(Map<StreamId, CachedStream> working);

            interface Factory {
                Update create(StreamId streamId, ExpectedVersion version, Iterable<? extends Event> events);
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
