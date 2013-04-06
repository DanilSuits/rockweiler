/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player.json;

import rockweiler.idtools.player.AbstractPlayerCollector;
import rockweiler.idtools.player.Player;

import java.io.PrintStream;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JsonWriter extends AbstractPlayerCollector {
    private final PrintStream out;

    public JsonWriter(PrintStream out) {
        this.out = out;
    }

    public void collect(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
