/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.csv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Predicate;
import rockweiler.player.Player;
import rockweiler.player.jackson.JsonPlayer;
import rockweiler.player.jackson.JsonPlayerFactory;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class CsvPlayerFactory {
    private final JsonPlayerFactory core;

    public CsvPlayerFactory(JsonPlayerFactory core) {
        this.core = core;
    }

    public JsonPlayer toJsonPlayer(String src) {
        String args[] = split(src);
        return toJsonPlayer(args);
    }

    public JsonPlayer toJsonPlayer(String args[]) {

        ObjectMapper jackson = new ObjectMapper();

        ObjectNode root = jackson.createObjectNode();
        root.with("id").put("mlb", args[1]);
        root.with("id").put("lahman", args[0]);
        root.with("bio").put("name", args[2]);

        return core.toPlayer(root);
    }

    public Predicate<Player> toSearch(String src) {
        String args[] = split(src);
        return toSearch(args);
    }

    public Predicate<Player> toSearch(String args[]) {
        String mlbId = args[1];
        String lahmanId = args[0];
        String name = args[2];

        return new CsvPlayerMatch(mlbId,lahmanId,name);
    }

    private String[] split(String src) {
        return src.split(",");
    }
}
