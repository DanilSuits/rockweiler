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
import rockweiler.idtools.player.BioReader;
import rockweiler.idtools.player.Biography;
import rockweiler.idtools.player.IdConflictException;
import rockweiler.idtools.player.IdReader;
import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.PlayerCollector;

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

    public void merge(Iterable<? extends Player> updateDatabase) {
        for (Player rhs : updateDatabase) {
            String key = idReader.getId(rhs);

            if (!master.containsKey(key)) {
                master.put(key, rhs);
            } else {
                Player lhs = master.get(key);
                merge(rhs, lhs);
            }
        }
    }

    private void merge(Player rhs, Player lhs) {
        try {
            lhs.getIds().merge(rhs.getIds());
        } catch (IdConflictException e) {
            conflictPlayers.add(rhs);
        }
    }


    private static final Predicate<Player> REJECTED = new Predicate<Player>() {
        public boolean apply(Player player) {
            return player.getIds().count() < 2;
        }
    };

    public void collectMasterDatabase(PlayerCollector collector) {
        Iterable<Player> accepted = Iterables.filter(master.values(), Predicates.not(REJECTED));
        collector.collectAll(accepted);
    }

    public void collectMissingDatabase(PlayerCollector collector) {
        Iterable<Player> rejected = Iterables.filter(master.values(), REJECTED);
        collector.collectAll(rejected);
    }

    public void collectConflictDatabase(PlayerCollector collector) {
        collector.collectAll(conflictPlayers);
    }

    public static void main(String[] args) throws IOException {

        Iterable<? extends Player> players = DatabaseFactory.createEmptyDatabase();
        players = DatabaseFactory.createDatabase("master.players.json");

        players = Iterables.filter(players, Biography.HAS_BIO_FILTER);

        IdReader idReader = new BioReader();
        Map<String, Player> idMap = DatabaseFactory.createIdMap(players, idReader);

        BootstrapMerge theMerge = new BootstrapMerge(idMap, idReader);

        String updates[] =
                {
                        "mlb.players.json"
                        , "espn.players.json"
                        , "lahman.players.json"
                        // TODO -- oliver has no DOB information in it.
                        // , "oliver.players.json"
                        , "rotoworld.players.json"
                        , "yahoo.players.json"
                        , "baseballReference.players.json"
                        , "biography.merged.json"
                };

        for (String updateDatabase : updates) {
            Iterable<? extends Player> update = DatabaseFactory.createDatabase(updateDatabase);
            update = Iterables.filter(update, Biography.HAS_BIO_FILTER);
            theMerge.merge(update);
        }

        DatabaseWriter mergedOut = DatabaseFactory.createWriter("bootstrap.merged.json");
        DatabaseWriter missingOut = DatabaseFactory.createWriter("bootstrap.missing.json");
        DatabaseWriter conflictOut = DatabaseFactory.createWriter("bootstrap.conflict.json");

        MergeReader reader = new MergeReader(mergedOut, missingOut, conflictOut);
        reader.collect(theMerge);
        reader.onEnd();

    }
}
