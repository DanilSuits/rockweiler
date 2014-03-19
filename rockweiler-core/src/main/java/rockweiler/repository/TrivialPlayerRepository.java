/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.repository;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TrivialPlayerRepository implements PlayerRepository<String> {
    public static TrivialPlayerRepository create(Iterable<String> knownPlayers) {
        List<String> provisionalPlayers = Lists.newArrayList();
        return create(knownPlayers,provisionalPlayers);
    }

    public static TrivialPlayerRepository create(Iterable<String> knownPlayers,List<String> provisionalPlayers) {
        Iterable<String> allPlayers = Iterables.concat(knownPlayers,provisionalPlayers);
        return new TrivialPlayerRepository(allPlayers,provisionalPlayers);
    }

    private final Iterable<String> knownPlayers;
    private final List<String> provisionalPlayers;

    public TrivialPlayerRepository(Iterable<String> knownPlayers, List<String> provisionalPlayers) {
        this.knownPlayers = knownPlayers;
        this.provisionalPlayers = provisionalPlayers;
    }

    public Iterable<String> getPlayers() {
        return Iterables.concat(knownPlayers,provisionalPlayers);
    }

    public void add(String player) {
        provisionalPlayers.add(player);
    }
}
