/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence;

import com.google.common.collect.Lists;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.persistence.memory.StreamPosition;

import java.util.Collections;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class CachedStream {
    public final StreamPosition stream;
    public final List<Event> history;

    public CachedStream(StreamPosition stream, List<Event> history) {
        this.stream = stream;
        this.history = history;
    }

    public CachedStream append(Iterable<? extends Event> source) {
        if (StreamId.All.isSameValue(stream.streamId)) {
            throw new IllegalStateException("wrong stream");
        }

        List<Event> events = Lists.newArrayList(source);
        if (events.isEmpty()) {
            return this;
        }
        ExpectedVersion version = this.stream.expectedVersion.next(events.size());

        return append(events, version);
    }

    CachedStream append(List<Event> events, ExpectedVersion next) {
        List<Event> history = Lists.newArrayList(this.history);
        history.addAll(events);
        StreamPosition nextPosition = stream.next(next);
        return new CachedStream(nextPosition, history);
    }

    public static CachedStream emptyStream(StreamId streamId) {
        return new CachedStream(StreamPosition.empty(streamId), Collections.EMPTY_LIST);
    }
}
