/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.draft;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TrivialDraft<T> implements Draft<T> {
    private final List<Slot<? extends T>> slots;

    public TrivialDraft(List<Slot<? extends T>> slots) {
        this.slots = slots;
    }

    public void update(Slot change) {
        slots.set(change.pickId,change);
    }

    public int onClock() {
        for(Slot s : slots) {
            if (Slot.BasicStates.PENDING.equals(s.state)) {
                return s.pickId;
            }
        }

        throw new RuntimeException("No pending draft picks");
    }

    public List<Slot<T>> copy() {
        List<Slot<T>> clone = Lists.newArrayList();
        for(Slot s : slots) {
            clone.add(s);
        }
        return clone;
    }
}
