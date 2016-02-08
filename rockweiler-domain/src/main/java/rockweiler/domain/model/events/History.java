/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.model.events;

import com.google.common.collect.Lists;
import com.vocumsineratio.cqrs.Event;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class History implements Iterable<Event> {
    private volatile Iterable<Event> events = Collections.EMPTY_LIST;

    public synchronized void publish(Event event) {
        List<Event> next = Lists.newArrayList(events);

        for(Event crnt : next) {
            if (crnt.eventId.isSameValue(event.eventId)) {
                if (crnt.isSameValue(event)) {
                    // Ignore previously seen events, so that
                    // we have idempotent behavior
                    return;
                } else {
                    throw new ConflictException(crnt, event);
                }
            }
        }
        next.add(event);
        events = next;
    }

    @Override
    public Iterator<Event> iterator() {
        Iterable<Event> local = events;
        return local.iterator();
    }

    public static class ConflictException extends RuntimeException {
        public final Event original;
        public final Event conflicting;

        public ConflictException(Event original, Event conflicting) {
            super("Event conflict: " + conflicting.eventId.toString());
            this.original = original;
            this.conflicting = conflicting;
        }
    }
}
