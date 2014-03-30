/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.quickdraft;

import rockweiler.console.core.modules.Application;

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
}
