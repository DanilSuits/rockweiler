/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory.simple;

import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.persistence.memory.AbstractUpdate;
import com.vocumsineratio.eventstore.persistence.CachedStream;
import com.vocumsineratio.eventstore.persistence.memory.MemoryStore;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class SimpleUpdate extends AbstractUpdate
        implements MemoryStore.Store.Update {

    SimpleUpdate(StreamId streamId, ExpectedVersion version, Iterable<? extends Event> events) {
        super(streamId,version,events);
    }

    protected CachedStream update(CachedStream old) {
        return old.append(events);
    }

    public static class Factory implements MemoryStore.Store.Update.Factory{

        @Override
        public MemoryStore.Store.Update create(StreamId streamId, ExpectedVersion version, Iterable<? extends Event> events) {
            return new SimpleUpdate(streamId, version, events);
        }
    }
}
