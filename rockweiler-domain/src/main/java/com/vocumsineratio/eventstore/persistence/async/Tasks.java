/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.async;

import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.cqrs.Event;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Tasks {
    public interface Factory<Write,Read> {
        Callable<Write> store(final StreamId streamId, final ExpectedVersion version, final Iterable<? extends Event> events);
        Callable<Read> read(final StreamId streamId);
    }

    public interface Client<Write,Read> {
        Future<Read> get(final StreamId streamId);
        Future<Write> store(final StreamId streamId, final ExpectedVersion version, final Iterable<? extends Event> events);
    }

    public interface AsyncClient<Write,Read> extends Client<Write,Read> {
        void commit(Future<Write> task);
        Iterable<Event> read(Future<Read> task);
    }
}
