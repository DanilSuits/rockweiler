/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.io;

import rockweiler.player.Player;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface PlayerStore {
    interface Reader {
        Iterable<? extends Player> readPlayers(String key) throws KeyNotFoundException;
    }

    interface Writer {
        void writePlayers(String key, Iterable<? extends Player> players) throws KeyNotFoundException, KeyNotUpdatedException;
    }

    Reader createReader();
    Writer createWriter();


}
