/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory;

import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.cqrs.Event;

import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
class SimpleUpdate implements MemoryStore.Store.Update {
    private final StreamId streamId;
    private final ExpectedVersion version;
    private final Iterable<? extends Event> events;

    SimpleUpdate(StreamId streamId, ExpectedVersion version, Iterable<? extends Event> events) {
        this.streamId = streamId;
        this.version = version;
        this.events = events;
    }

    public void copyTo(Map<StreamId, CachedStream> working) {
        CachedStream old = read(working);
        CachedStream current = update(old);

        writeTo(working, current);
    }

    private CachedStream read(Map<StreamId, CachedStream> working) {
        CachedStream old = working.get(streamId);
        if (null == old) {
            old = CachedStream.emptyStream(streamId);
        }
        return old;
    }

    private CachedStream update(CachedStream old) {
        return old.append(events);
    }

    private void writeTo(Map<StreamId, CachedStream> working, CachedStream current) {
        working.put(streamId, current);
    }
}
