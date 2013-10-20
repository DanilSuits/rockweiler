/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.draft;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface Draft<T> {
    public void update(Slot<? extends T> change);

    public int onClock();

    public List<Slot<T>> copy();
}
