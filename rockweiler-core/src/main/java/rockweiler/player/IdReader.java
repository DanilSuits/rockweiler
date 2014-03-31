/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player;

import rockweiler.player.jackson.Schema;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface IdReader {
    String getId(Player p);
    String getId(Schema.Player p);
}
