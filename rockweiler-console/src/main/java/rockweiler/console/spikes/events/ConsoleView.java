/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface ConsoleView {
    void onStatus(String status);

    // TODO
    void onPlayers(List players);
}
