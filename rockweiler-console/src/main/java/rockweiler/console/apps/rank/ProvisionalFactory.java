/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rank;

import com.google.common.collect.Maps;
import rockweiler.player.jackson.Schema;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ProvisionalFactory {
    private final String key;

    public ProvisionalFactory(String key) {
        this.key = key;
    }

    public Schema.Player create(String name) {
        Schema.Bio bio = new Schema.Bio();
        bio.name = name;

        Schema.Player player = new Schema.Player();
        player.bio = bio;
        player.id = Maps.newTreeMap();
        player.id.put(key, String.valueOf(bio.hashCode()));

        return player;
    }
}
