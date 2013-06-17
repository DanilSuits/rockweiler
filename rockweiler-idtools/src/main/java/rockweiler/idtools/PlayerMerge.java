/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import rockweiler.player.Player;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface PlayerMerge {
    public void merge(Iterable<? extends Player> updateDatabase);

    public Iterable<? extends Player> collectMasterDatabase();
    public Iterable<? extends Player> collectMissingDatabase();
    public Iterable<? extends Player> collectConflictDatabase();
}
