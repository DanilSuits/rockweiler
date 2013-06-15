/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class SortingCollector implements PlayerCollector {
    private final PlayerCollector target;
    private final Comparator<Player> orderBy;

    public SortingCollector(PlayerCollector target, Comparator<Player> orderBy) {
        this.target = target;
        this.orderBy = orderBy;
    }

    public void collect(Player player) {
        target.collect(player);
    }

    public void collectAll(Iterable<? extends Player> allPlayers) {
        List<Player> toSort = Lists.newArrayList(allPlayers);
        Collections.sort(toSort, orderBy);
        target.collectAll(toSort);
    }
}
