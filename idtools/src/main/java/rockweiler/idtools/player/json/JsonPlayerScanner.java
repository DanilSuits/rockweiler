/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player.json;

import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.PlayerScanner;

import java.util.Scanner;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JsonPlayerScanner implements PlayerScanner {
    private final Scanner source;
    private final JsonPlayerFactory factory;

    public JsonPlayerScanner(Scanner source, JsonPlayerFactory factory) {
        this.source = source;
        this.factory = factory;
    }

    public boolean hasNext() {
        return source.hasNext();
    }

    public Player next() {
        String rawJson = source.nextLine();
        return factory.toPlayer(rawJson);
    }
}
