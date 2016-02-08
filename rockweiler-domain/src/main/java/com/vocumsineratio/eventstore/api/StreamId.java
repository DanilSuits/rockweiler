/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.api;

import com.vocumsineratio.domain.Id;
import com.vocumsineratio.domain.Value;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class StreamId implements Value<StreamId> {
    public final String streamId;

    private StreamId(String streamId) {
        this.streamId = streamId;
    }

    @Override
    public boolean isSameValue(StreamId other) {
        return streamId.equals(other.streamId);
    }

    public static final StreamId of(Id id) {
        return new StreamId(id.uuid.toString());
    }

    public static final StreamId All = new StreamId("$all");

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }

        if (!(rhs instanceof StreamId)) {
            return false;
        }

        return isSameValue((StreamId) rhs);
    }

    @Override
    public int hashCode() {
        return streamId.hashCode();
    }
}
