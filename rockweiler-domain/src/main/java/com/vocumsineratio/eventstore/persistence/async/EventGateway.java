/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.async;

import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.persistence.async.Tasks.Client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class EventGateway<Write,Read> implements Client<Write,Read> {
    private final ExecutorService executor;
    private final Tasks.Factory<Write,Read> factory;

    public EventGateway(ExecutorService executor, Tasks.Factory<Write, Read> factory) {
        this.executor = executor;
        this.factory = factory;
    }

    public Future<Read> get(final StreamId streamId) {
        return executor.submit(factory.read(streamId));
    }

    public Future<Write> store(final StreamId streamId, final ExpectedVersion version, final Iterable<? extends Event> events) {
        return executor.submit(factory.store(streamId, version, events));
    }
}
