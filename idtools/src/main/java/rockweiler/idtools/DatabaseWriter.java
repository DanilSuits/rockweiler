/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.PlayerCollector;

import java.io.PrintStream;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DatabaseWriter {
    private final PrintStream out ;
    private final PlayerCollector target;

    public DatabaseWriter(PrintStream out, PlayerCollector target) {
        this.out = out;
        this.target = target;
    }

    public PlayerCollector collector() {
        return target;
    }

    public void onEnd() {
        out.close();
    }
}
