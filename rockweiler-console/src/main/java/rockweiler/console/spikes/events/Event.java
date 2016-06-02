/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import java.util.UUID;

/**
 * @author Danil Suits (danil@vast.com)
 */
public abstract class Event<D> {
    final String eventType;
    final UUID eventId;
    final D data;

    protected Event(String eventType, UUID eventId, D data) {
        this.eventType = eventType;
        this.eventId = eventId;
        this.data = data;
    }

    public D data() {
        return data;
    }
}
