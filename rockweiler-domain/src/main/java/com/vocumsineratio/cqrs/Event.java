/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.cqrs;

import com.vocumsineratio.domain.ComparisonChain;
import com.vocumsineratio.domain.Id;
import com.vocumsineratio.domain.Value;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Event<D extends Value<D>> implements Value<Event<D>> {
    public final EventName eventName;
    public final Id eventId;
    public final D data;

    public Event(EventName eventName, Id eventId, D data) {
        this.eventName = eventName;
        this.eventId = eventId;
        this.data = data;
    }

    @Override
    public boolean isSameValue(Event<D> other) {
        return ComparisonChain
                .start(this,other)
                .compare(eventId, other.eventId)
                .compare(eventName, other.eventName)
                .compare(data, other.data)
                .end();
    }

    public interface Data<D extends Data<D>> extends Value<D> {
        Event<D> create(Id id);
    }
}
