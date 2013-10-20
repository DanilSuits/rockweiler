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
public class TrivialDraftService<T> implements DraftService<T>{
    private final List<DraftListener<? super T>> allListeners = Lists.newArrayList();
    private final Draft draftState;

    public TrivialDraftService(Draft draftState) {
        this.draftState = draftState;
    }

    public void subscribe(DraftListener<? super T> client) {
        allListeners.add(client);
        client.onLoad(draftState);
    }

    public void requestChange(Slot<T> slot) {
        if (isValidChange(slot)) {
            applyChange(slot);
            broadcastChange(slot);
        }
    }

    private void broadcastChange(Slot<T> slot) {
        for(DraftListener<? super T> crnt : allListeners) {
            crnt.onChange(slot);
        }
    }

    private boolean isValidChange(Slot<T> slot) {

        if (slot.state.equals(Slot.BasicStates.PENDING)) {
            return true;
        }

        return slot.pickId == draftState.onClock();
    }

    private void applyChange(Slot<T> slot) {
        draftState.update(slot);
    }
}
