/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory;

import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class StreamPosition {
    public final StreamId streamId;
    public final ExpectedVersion expectedVersion;

    public StreamPosition(StreamId streamId, ExpectedVersion expectedVersion) {
        this.streamId = streamId;
        this.expectedVersion = expectedVersion;
    }

    public StreamPosition update(StreamPosition rhs) {
        if (! this.streamId.isSameValue(rhs.streamId)) {
            throw new IllegalArgumentException("actual: " + rhs.streamId.streamId + " expected: " + this.streamId.streamId);
        }
        if (isUpdate(rhs)) {
            return rhs;
        }

        return this;
    }

    public StreamPosition next(ExpectedVersion version) {
        return update(new StreamPosition(this.streamId, version));
    }

    public boolean isUpdate(StreamPosition rhs) {
        return rhs.expectedVersion.version > this.expectedVersion.version;
    }

    public static StreamPosition empty(StreamId streamId) {
        return new StreamPosition(streamId, ExpectedVersion.NoStream);
    }

    public interface Listener {
        void on(StreamPosition position);
    }
}
