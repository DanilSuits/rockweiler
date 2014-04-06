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
public class Requests {
    public static final Application.Request QUIT = new Application.Request() {
    };

    public static class Comment implements Application.Request {
        public final String message;

        public Comment(String message) {
            this.message = message;
        }
    }

    public static class Pick implements Application.Request {
        public final String query;

        public Pick(String query) {
            this.query = query;
        }
    }

    public static class AddPlayer implements Application.Request {
        public final String name;

        public AddPlayer(String name) {
            this.name = name;
        }
    }

    public static class HidePlayer implements Application.Request {
        public final String query;

        public HidePlayer(String query) {
            this.query = query;
        }
    }

    public static class Filter implements Application.Request {
        public final List<Schema.Player> players;

        public Filter(List<Schema.Player> players) {
            this.players = players;
        }
    }
}
