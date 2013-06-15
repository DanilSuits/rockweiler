/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import rockweiler.idtools.player.BioReader;
import rockweiler.idtools.player.IdConflictException;
import rockweiler.idtools.player.IdReader;
import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.PlayerCollector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class BootstrapMerge implements PlayerMerge {
    private final Map<String, Player> master;
    private final List<Player> conflictPlayers = Lists.newArrayList();

    private final IdReader idReader;

    public BootstrapMerge(Map<String, Player> master, IdReader idReader) {
        this.master = master;
        this.idReader = idReader;
    }

    public void merge(Iterable<Player> updateDatabase) {
        Map<String, Player> update = DatabaseFactory.createIdMap(updateDatabase, idReader);

        for (Map.Entry<String, Player> crnt : update.entrySet()) {
            String key = crnt.getKey();
            Player rhs = crnt.getValue();

            if (!master.containsKey(key)) {
                master.put(key, rhs);
            } else {
                Player lhs = master.get(key);
                try {
                    lhs.getIds().merge(rhs.getIds());
                } catch (IdConflictException e) {
                    conflictPlayers.add(rhs);
                }
            }
        }
    }

    private static final Predicate<Player> REJECTED = new Predicate<Player>() {
        public boolean apply(Player player) {
            return player.getIds().count() < 2;
        }
    };

    public void collectMasterDatabase(PlayerCollector collector) {
        for (Map.Entry<String, Player> crnt : master.entrySet()) {
            if (Predicates.not(REJECTED).apply(crnt.getValue())) {
                collector.collect(crnt.getValue());
            }
        }
    }

    public void collectMissingDatabase(PlayerCollector collector) {
        List<Player> rejected = Lists.newArrayList();

        for (Map.Entry<String, Player> crnt : master.entrySet()) {
            if (REJECTED.apply(crnt.getValue())) {
                rejected.add(crnt.getValue());
            }
        }

        collector.collectAll(rejected);
    }

    public void collectConflictDatabase(PlayerCollector collector) {
        collector.collectAll(conflictPlayers);
    }

    static final Predicate<Player> HAS_BIO = new Predicate<Player>() {

        public boolean apply(Player input) {
            return null != input.getBio();
        }
    };

    public static void main(String[] args) throws IOException {

        IdReader idReader = new BioReader();
        Map<String,Player> mergeMap = getBootstrapDatabase("master.player.json", idReader);
        BootstrapMerge theMerge = new BootstrapMerge(mergeMap,idReader);

        String updates[] =
                {
                        "mlb.players.json"
                        , "espn.players.json"
                        , "lahman.players.json"
                        // TODO -- oliver has no DOB information in it.
                        // , "oliver.players.json"
                        , "rotoworld.players.json"
                        , "yahoo.players.json"
                };

        for (String updateDatabase : updates) {
            Iterable<Player> update = DatabaseFactory.createDatabase(updateDatabase);
            update = Iterables.filter(update, HAS_BIO);
            theMerge.merge(update);
        }

        DatabaseWriter mergedOut = DatabaseFactory.createWriter("bootstrap.merged.json");
        DatabaseWriter missingOut = DatabaseFactory.createWriter("bootstrap.missing.json");
        DatabaseWriter conflictOut = DatabaseFactory.createWriter("bootstrap.conflict.json");

        MergeReader reader = new MergeReader(mergedOut, missingOut, conflictOut);
        reader.collect(theMerge);
        reader.onEnd();

    }

    private static Map<String, Player> getCleanDatabase() {
        return Maps.newHashMap();
    }

    private static Map<String, Player> getBootstrapDatabase(String masterDatabase, IdReader idReader) throws FileNotFoundException {

        Iterable<? extends Player> players = DatabaseFactory.createDatabase(masterDatabase);
        Map<String, Player> idMap = DatabaseFactory.createIdMap(players, idReader);

        return idMap;
    }

}
