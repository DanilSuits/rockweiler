/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory.ges;

import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.eventstore.persistence.memory.AbstractUpdate;
import com.vocumsineratio.eventstore.persistence.CachedStream;
import com.vocumsineratio.eventstore.persistence.memory.MemoryStore;

/**
 * http://docs.geteventstore.com/dotnet-api/3.3.1/optimistic-concurrency-and-idempotence/
 *
 * expectedVersion > currentVersion - a WrongExpectedVersionException will be thrown.
 * expectedVersion == currentVersion - events will be written and acknowledged.
 * expectedVersion < currentVersion
 * ... events match, then signal a successful write
 * ... events don't match, WrongExpectedVersionException
 * @author Danil Suits (danil@vast.com)
 */
public class GESUpdate extends AbstractUpdate
        implements MemoryStore.Store.Update {

    public GESUpdate(StreamId streamId, ExpectedVersion version, Iterable<? extends Event> events) {
        super(streamId, version, events);

        if (this.expected.isSameValue(ExpectedVersion.Any)) {
            throw new IllegalArgumentException("ExpectedVersion.Any not supported");
        }
    }

    @Override
    protected CachedStream update(CachedStream old) {

        if (this.expected.version > old.stream.expectedVersion.version) {
            throw new WrongExpectedVersionException(old.stream.expectedVersion, this.expected);
        }

        if (this.expected.isSameValue(old.stream.expectedVersion)) {
            return old.append(events);
        }

        int offset = old.stream.expectedVersion.version;
        for(Event e : this.events) {
            Event prev = old.history.get(++offset);
            if (! e.isSameValue(prev)) {
                throw new WriteConflict(old.stream.streamId,offset,prev,e);
            }
        }

        return old;
    }

    public static class WriteConflict extends RuntimeException {
        public final StreamId stream;
        public final int version;
        public final Event actual;
        public final Event expected;

        public WriteConflict(StreamId stream, int version, Event actual, Event expected) {
            this.stream = stream;
            this.version = version;
            this.actual = actual;
            this.expected = expected;
        }

        @Override
        public String getMessage() {
            return (new StringBuilder())
                    .append("streamId: ")
                    .append(stream.streamId)
                    .append("position: ")
                    .append(version)
                    .toString();
        }
    }

    public static class WrongExpectedVersionException extends RuntimeException {
        public final ExpectedVersion actual;
        public final ExpectedVersion expected;


        public WrongExpectedVersionException(ExpectedVersion actual, ExpectedVersion expected) {
            this.actual = actual;
            this.expected = expected;
        }

        @Override
        public String getMessage() {
            return (new StringBuilder("actual: "))
                    .append(actual.version)
                    .append(" expected: ")
                    .append(expected.version)
                    .toString();

        }
    }
}
