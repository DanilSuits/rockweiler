/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory;

import com.google.common.collect.Lists;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.cqrs.Event;

import java.util.Collections;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
class CachedStream {
    public final StreamId streamId;
    public final List<Event> history;
    public final ExpectedVersion version;

    private CachedStream(StreamId streamId, List<Event> history, ExpectedVersion version) {
        this.streamId = streamId;
        this.history = history;
        this.version = version;
    }

    CachedStream append(Iterable<? extends Event> source) {
        if (StreamId.All.isSameValue(streamId)) {
            throw new IllegalStateException("wrong stream");
        }

        List<Event> events = Lists.newArrayList(source);
        if (events.isEmpty()) {
            return this;
        }
        ExpectedVersion version = this.version.next(events.size());

        return append(events, version);
    }

    CachedStream append(List<Event> events, ExpectedVersion next) {
        List<Event> history = Lists.newArrayList(this.history);
        history.addAll(events);
        return new CachedStream(this.streamId, history, next);
    }

    static CachedStream emptyStream(StreamId streamId) {
        return new CachedStream(streamId, Collections.EMPTY_LIST, ExpectedVersion.NoStream);
    }
}
