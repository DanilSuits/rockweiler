/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.quickdraft;

import rockweiler.console.core.modules.Application;
import rockweiler.player.jackson.Schema;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Events {
    static final Application.Event SHUTDOWN = new Application.Event() {
    };

    static final class NoMatch implements Application.Event {
        public final String query;

        NoMatch(String query) {
            this.query = query;
        }
    }

    static final class DraftUpdate<T> implements Application.Event {
        public final Iterable<T> slots;

        DraftUpdate(Iterable<T> slots) {
            this.slots = slots;
        }
    }

    static final class AmbiguousPlayer<T> implements Application.Event {
        public final String query;
        public final List<T> players;

        AmbiguousPlayer(String query, List<T> players) {
            this.query = query;
            this.players = players;
        }
    }

    static final class AlreadyTaken implements Application.Event {
        public final String query;

        AlreadyTaken(String query) {
            this.query = query;
        }
    }

    public static final class HidePlayer implements Application.Event {
        public final Schema.Player player;

        HidePlayer(Schema.Player player) {
            this.player = player;
        }
    }

    public static final class WatchPlayer implements Application.Event {
        public final Schema.Player player;

        public WatchPlayer(Schema.Player player) {
            this.player = player;
        }
    }

    public static final class FilterResult implements Application.Event {
        public final List<Schema.Player> players;

        FilterResult(List<Schema.Player> players) {
            this.players = players;
        }
    }

    public static final class StatusUpdate implements Application.Event {
        public final List<PlayerStatus> update;

        StatusUpdate(List<PlayerStatus> update) {
            this.update = update;
        }
    }

    public static class PlayerStatus {
        public boolean available;
        public Schema.Player player;
    }
}
