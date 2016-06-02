package rockweiler.console.spikes.events;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface PlayerPool<T> {
    List<T> query (String hint);
}
