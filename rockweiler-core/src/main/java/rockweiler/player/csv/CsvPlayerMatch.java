/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.csv;

import com.google.common.base.Predicate;
import rockweiler.player.Player;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class CsvPlayerMatch implements Predicate<Player> {
    private final String mlbId;
    private final String lahmanId;
    private final String name;

    public CsvPlayerMatch(String mlbId, String lahmanId, String name) {
        this.mlbId = mlbId;
        this.lahmanId = lahmanId;
        this.name = name;
    }

    public boolean apply(Player player) {
        Player.Ids rhs = player.getIds();
        if (mlbId.equals(rhs.get("mlb"))) {
            return true;
        }

        if (lahmanId.equals(rhs.get("lahman"))) {
            return true;
        }

        if (name.equals(player.getBio().getName())) {
            return true;
        }

        return false;
    }
}
