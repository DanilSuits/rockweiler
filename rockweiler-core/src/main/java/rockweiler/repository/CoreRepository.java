/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.repository;

import com.google.common.collect.Iterables;

import java.util.Collection;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class CoreRepository<T> implements PlayerRepository<T> {
    private final Iterable<T> knownPlayers;
    private final Collection<T> provisionPlayers;

    public CoreRepository(Iterable<T> knownPlayers, Collection<T> provisionalPlayers) {
        this.knownPlayers = knownPlayers;
        this.provisionPlayers = provisionalPlayers;
    }

    public Iterable<T> getPlayers() {
        return Iterables.concat(knownPlayers, provisionPlayers);
    }

    public void add(T player) {
        provisionPlayers.add(player);
    }
}
