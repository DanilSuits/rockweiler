/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory;

import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.eventstore.persistence.CachedStream;
import com.vocumsineratio.eventstore.persistence.StreamStore;

/**
 * @author Danil Suits (danil@vast.com)
 */
public abstract class AbstractUpdate implements MemoryStore.Store.Update{
    protected final StreamId streamId;
    protected final ExpectedVersion expected;
    protected final Iterable<? extends Event> events;

    protected AbstractUpdate(StreamId streamId, ExpectedVersion expected, Iterable<? extends Event> events) {
        this.streamId = streamId;
        this.expected = expected;
        this.events = events;
    }

    public void copyTo(StreamStore working) {
        CachedStream old = read(working);
        CachedStream current = update(old);

        writeTo(working, current);
    }

    protected abstract CachedStream update(CachedStream old);

    protected CachedStream read(StreamStore working) {
        return working.read(this.streamId);
    }

    private void writeTo(StreamStore working, CachedStream current) {
        working.store(streamId, current);
    }
}
