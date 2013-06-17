/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.io;

import com.google.common.collect.Lists;
import rockweiler.player.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class SortingWriter implements PlayerStore.Writer {
    private final PlayerStore.Writer target;

    public SortingWriter(PlayerStore.Writer target) {
        this.target = target;
    }

    private static final Comparator<Player> NAME_ORDER = new Comparator<Player>() {
        public int compare(Player lhs, Player rhs) {
            return lhs.getBio().getName().compareTo(rhs.getBio().getName());
        }
    };

    public void writePlayers(String key, Iterable<? extends Player> players) throws KeyNotUpdatedException, KeyNotFoundException {
        List<? extends Player> sortedPlayers = Lists.newArrayList(players);
        Collections.sort(sortedPlayers, NAME_ORDER);
        target.writePlayers(key,sortedPlayers);
    }
}
