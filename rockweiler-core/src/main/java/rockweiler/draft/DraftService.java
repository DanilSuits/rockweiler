/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.draft;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface DraftService<T> {
    public void subscribe(DraftListener<? super T> client);

    public void requestChange(Slot<T> slot);
}
