/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.waivers;

import rockweiler.console.core.modules.Application;
import rockweiler.player.jackson.Schema;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Requests {
    public static class AddPlayer implements Application.Request {
        public Schema.Player addPlayer;
    }

    public static class PickPlayer implements Application.Request {
        public Schema.Player pickPlayer;
    }

    public static class WaivePlayer implements Application.Request {
        public Schema.Player waivePlayer;
    }
}
