/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import java.util.Collections;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class EmptyPool<T> implements PlayerPool<T> {
    public List<T> query(String hint) {
        return Collections.EMPTY_LIST;
    }
}
