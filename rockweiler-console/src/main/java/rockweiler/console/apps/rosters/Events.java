/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rosters;

import rockweiler.console.core.modules.Application;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Events {
    static final Application.Event SHUTDOWN = new Application.Event() {
    };

    static final class CurrentTeam implements Application.Event {
        public final String team;

        CurrentTeam(String team) {
            this.team = team;
        }
    }

    static final class NoMatch implements Application.Event {
        public final String query;

        NoMatch(String query) {
            this.query = query;
        }
    }

    static final class AlreadyTaken implements Application.Event {
        public final String query;

        AlreadyTaken(String query) {
            this.query = query;
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

    static final class RosterUpdate<T> implements Application.Event {
        public final Iterable<RostersApp.RosterSlot<T>> slots;

        RosterUpdate(Iterable<RostersApp.RosterSlot<T>> slots) {
            this.slots = slots;
        }
    }

    static final class NoTeam implements Application.Event {
        public final String team;

        NoTeam(String team) {
            this.team = team;
        }
    }
}
