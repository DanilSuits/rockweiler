/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory.ges;

import com.google.common.collect.Lists;
import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.eventstore.persistence.memory.AbstractUpdate;
import com.vocumsineratio.eventstore.persistence.CachedStream;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class GESAppend extends AbstractUpdate {
    protected GESAppend(StreamId streamId, ExpectedVersion expected, Iterable<? extends Event> events) {
        super(streamId, expected, events);
        if (! ExpectedVersion.Any.isSameValue(expected)) {
            throw new IllegalArgumentException("expectedVersion: " + expected.version);
        }
    }

    @Override
    protected CachedStream update(CachedStream old) {
        List<Event> events = Lists.newArrayList(this.events);
        Event start = events.get(0);

        int found = -1;

        for(int index = 0; index < old.history.size(); ++index) {
            if (! start.isSameValue(old.history.get(index))) {
                found = index;
                break;
            }
        }

        if (found != -1) {
            int index = found;
            for(Event e : this.events) {
                final Event actual = old.history.get(index);
                if (! e.isSameValue(actual)) {
                    throw new GESUpdate.WriteConflict(old.stream.streamId, index, actual, e);
                }
            }
            return old;
        }

        return old.append(events);
    }
}
