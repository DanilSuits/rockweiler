/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory;

import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.persistence.async.Tasks;

import java.util.concurrent.Callable;

/**
 * @author Danil Suits (danil@vast.com)
 */
class TaskFactory implements Tasks.Factory<MemoryStore.WriteStatus, MemoryStore.ReadResult>
{
    private final Handle connection;
    private final MemoryStore.Store.Update.Factory updateFactory;

    TaskFactory(Handle connection, MemoryStore.Store.Update.Factory updateFactory) {
        this.connection = connection;
        this.updateFactory = updateFactory;
    }

    public Callable<MemoryStore.WriteStatus> store(final StreamId streamId, final ExpectedVersion version, Iterable<? extends Event> events) {
        MemoryStore.Store.Update update = updateFactory.create(streamId, version, events);
        return new MemoryStore.Store(connection, update);
    }

    public Callable<MemoryStore.ReadResult> read(final StreamId streamId) {
        return new MemoryStore.ReadTask(connection, streamId);
    }

}
