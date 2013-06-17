/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.PlayerCollector;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DatabaseWriter {
    private final OutputStream out ;
    private final PlayerCollector target;

    public DatabaseWriter(OutputStream out, PlayerCollector target) {
        this.out = out;
        this.target = target;
    }

    public PlayerCollector collector() {
        return target;
    }

    public void onEnd() {
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
