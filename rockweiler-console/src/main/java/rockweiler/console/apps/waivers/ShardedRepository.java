/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.waivers;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import rockweiler.player.jackson.Schema;

import java.util.Map;
import java.util.Set;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ShardedRepository {
    Map<String, Map<String,Schema.Player>> shards = Maps.newHashMap();

    public void addPlayer(Schema.Player player) {
        for(Map.Entry<String, String> entry : player.id.entrySet()) {
            Map<String,Schema.Player> shard = shards.get(entry.getKey());
            if (null == shard) {
                shard = Maps.newTreeMap();
                shards.put(entry.getKey(), shard);
            }

            shard.put(entry.getValue(), player);
        }
    }

    public Schema.Player get(String key, String id) {
        Schema.Player player = null;
        Map<String,Schema.Player> shard = shards.get(key);
        if (null != shard) {
            player = shard.get(id);
        }
        return player;
    }
}
