/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.beust.jcommander.internal.Maps;
import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.PlayerCollector;

import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class BootstrapMerge implements PlayerMerge {
    Map<String,Player> allPlayers = Maps.newHashMap();

    public void merge(Iterable<Player> updateDatabase) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void collectMasterDatabase(PlayerCollector collector) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void collectMissingDatabase(PlayerCollector collector) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void collectConflictDatabase(PlayerCollector collector) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
