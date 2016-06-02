/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory.ges;

import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.eventstore.persistence.memory.MemoryStore;
import com.vocumsineratio.eventstore.persistence.memory.ges.GESAppend;
import com.vocumsineratio.eventstore.persistence.memory.ges.GESUpdate;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class GESUpdateFactory implements MemoryStore.Store.Update.Factory {
    @Override
    public MemoryStore.Store.Update create(StreamId streamId, ExpectedVersion version, Iterable<? extends Event> events) {
        if (ExpectedVersion.Any.isSameValue(version)) {
            return new GESAppend(streamId, version, events);
        }
        return new GESUpdate(streamId, version, events);
    }
}
