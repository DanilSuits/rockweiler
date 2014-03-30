package rockweiler.player.jackson;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/30/14
 * Time: 9:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class IdStore {
    static final Schema.Player PLAYER_NOT_FOUND = null;

    public static IdStore create(Iterable<Schema.Player> players) {
        Map<String,Map<String,Schema.Player>> store = Maps.newHashMap();
        for (Schema.Player p : players) {
            for (Map.Entry<String,String> id : p.id.entrySet()) {
                Map<String,Schema.Player> shard = store.get(id.getKey());
                if (null == shard) {
                    shard = Maps.newHashMap();
                    store.put(id.getKey(), shard);
                }

                shard.put(id.getValue(),p);
            }
        }

        return new IdStore(store);
    }

    private final Map<String, Map<String,Schema.Player>> store;

    public IdStore(Map<String, Map<String, Schema.Player>> store) {
        this.store = store;
    }

    public Schema.Player find(String key, String id) {

        Map<String,Schema.Player> shard = store.get(key);
        return shard.get(id);
    }

    public Schema.Player match(Schema.Player source) {
        for ( Map.Entry<String, String> id : source.id.entrySet()) {
            Schema.Player p = find(id.getKey(), id.getValue());
            if (null != p) {
                return p;
            }
        }

        return PLAYER_NOT_FOUND;
    }
}
