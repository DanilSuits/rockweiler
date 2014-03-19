/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rosters;

import rockweiler.console.core.modules.Application;

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

    public static class SetTeam implements Application.Request {
        public final String team;

        public SetTeam(String team) {
            this.team = team;
        }
    }

    public static class Pick implements Application.Request {
        public final String team;
        public final String player;

        public Pick(String team, String player) {
            this.team = team;
            this.player = player;
        }
    }
}
