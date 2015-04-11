/**
 * Copyright Vast 2015. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.apps.quickdraft.plugins;

import rockweiler.player.jackson.Schema;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class LocalWatchRepository {
    private final List<Schema.Player> players;

    public LocalWatchRepository(List<Schema.Player> players) {
        this.players = players;
    }

    public void add(Schema.Player player) {
        players.add(player);
    }
}
