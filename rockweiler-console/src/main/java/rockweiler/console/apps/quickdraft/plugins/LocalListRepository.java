package rockweiler.console.apps.quickdraft.plugins;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import rockweiler.console.apps.quickdraft.ListRepository;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/30/14
 * Time: 12:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class LocalListRepository implements ListRepository {
    public static class Builder {
        private final IdStore store;
        private Shard watch = EMPTY_SHARD;
        private Config config = null;

        public Builder(IdStore store) {
            this.store = store;
        }

        public Builder withWatchList(List<Schema.Player> watchList) {
            this.watch = new LeafShard(watchList);
            return this;
        }

        public Builder withConfig(Config config) {
            this.config = config;
            return this;
        }

        public List<Schema.Player> createList(String path) throws IOException {
            return createList(new File(path));
        }

        List<Schema.Player> createList(File source) throws IOException {
            InputStream is = new FileInputStream(source);

            List<Schema.Player> normalizedList = Lists.newArrayList();

            JacksonPlayerRepository repo = JacksonPlayerRepository.create(is);
            for (Schema.Player p : repo.getPlayers()) {
                if (null == p) {
                    System.err.println(p);
                }
                Schema.Player known = store.match(p);
                if (IdStore.PLAYER_NOT_FOUND != known) {
                    normalizedList.add(known);
                }
            }
            return normalizedList;
        }

        Shard build(Config config) throws IOException {
            withConfig(config);
            return build();
        }

        NestedShard createShard(Config config) throws IOException {
            NestedShard root = new NestedShard();

            for (Map.Entry<String, ConfigValue> entry : config.root().entrySet()) {
                String key = entry.getKey();

                Shard shard = EMPTY_SHARD;

                if (ConfigValueType.STRING.equals(entry.getValue().valueType())) {
                    List<Schema.Player> players = this.createList(config.getString(key));
                    shard = new LeafShard(players);
                }

                if (ConfigValueType.OBJECT.equals(entry.getValue().valueType())) {
                    shard = this.createShard(config.getConfig(key));
                }

                root.add(key,shard);
            }

            return root;

        }

        Shard build() throws IOException {
            NestedShard root = createShard(this.config);
            root.add("watch", watch);

            return root;
        }

    }

    public static LocalListRepository create(Builder builder) throws IOException {
        Shard root = builder.build();
        return new LocalListRepository(root);
    }

    public static LocalListRepository create(Config config, IdStore store) throws IOException {
        Builder builder = builder(config, store);

        return create(builder);
    }

    public static Builder builder(Config config, IdStore store) {
        Builder builder = new Builder(store);
        builder.withConfig(config);
        return builder;
    }

    private final Shard rootShard;

    public LocalListRepository(Shard rootShard) {
        this.rootShard = rootShard;
    }

    public List<Schema.Player> get(String key) {
        Shard crntShard = findShard(key);
        return crntShard.getPlayers();
    }

    Shard findShard(String key) {

        Shard crnt = rootShard;
        for( String part : key.split("\\.")) {
            crnt = crnt.shard(part);
        }

        return crnt;
    }

    static interface Shard {
        Shard shard(String key);

        List<Schema.Player> getPlayers();
    }

    private static final Shard EMPTY_SHARD = new Shard() {
        public Shard shard(String key) {
            return this;
        }

        public List<Schema.Player> getPlayers() {
            return Collections.EMPTY_LIST;
        }
    };

    static class NestedShard implements Shard {
        Map<String, Shard> children = Maps.newHashMap();

        void add(String key, Shard child) {
            children.put(key,child);
        }

        public Shard shard(String key) {
            Shard child = children.get(key);
            if (null == child) {
                child = EMPTY_SHARD;
            }

            return child;
        }

        public List<Schema.Player> getPlayers() {
            return EMPTY_SHARD.getPlayers();
        }
    }

    static class LeafShard implements Shard {
        private final List<Schema.Player> players;

        LeafShard(List<Schema.Player> players) {
            this.players = players;
        }

        public Shard shard(String key) {
            return EMPTY_SHARD;
        }

        public List<Schema.Player> getPlayers() {
            return players;
        }
    }
}
