/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.draft;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface DraftListener<T> {
    public void onLoad(Draft<T> crntDraft);
    public void onChange(Slot<? extends T> slot);
}
