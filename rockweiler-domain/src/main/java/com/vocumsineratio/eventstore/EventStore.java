/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore;

import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface EventStore {
    Iterable<Event> get(StreamId streamId);
    void store(StreamId streamId, ExpectedVersion version, Iterable<? extends  Event> events);
}
