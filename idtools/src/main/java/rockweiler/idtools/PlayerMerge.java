/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.PlayerCollector;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface PlayerMerge {
    public void merge(Iterable<Player> updateDatabase);

    public void collectMasterDatabase(PlayerCollector collector);
    public void collectMissingDatabase(PlayerCollector collector);
    public void collectConflictDatabase(PlayerCollector collector);
}
