/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.io;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import rockweiler.player.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class SortingWriter implements PlayerStore.Writer {

    private static Function<Player, String> GET_NAME = new Function<Player, String>() {
        public String apply(rockweiler.player.Player input) {
            String name = null;
            if (null != input.getBio()) {
                name = input.getBio().getName();
            }
            return name;
        }
    };

    private static final Ordering<Player> NAME_ORDER = Ordering.natural().nullsFirst().onResultOf(GET_NAME);

    private final Comparator<Player> ordering;
    private final PlayerStore.Writer target;

    public SortingWriter(PlayerStore.Writer target) {
        this(NAME_ORDER,target);
    }

    public SortingWriter(Comparator<Player> ordering, PlayerStore.Writer target) {
        this.ordering = ordering;
        this.target = target;
    }


    public void writePlayers(String key, Iterable<? extends Player> players) throws KeyNotUpdatedException, KeyNotFoundException {
        List<? extends Player> sortedPlayers = Lists.newArrayList(players);
        Collections.sort(sortedPlayers, this.ordering);
        target.writePlayers(key,sortedPlayers);
    }
}
