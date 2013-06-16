/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import rockweiler.idtools.player.BioReader;
import rockweiler.idtools.player.IdConflictException;
import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.PlayerCollector;
import rockweiler.idtools.player.Predicates;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class IdentityMerge implements PlayerMerge {
    private final Iterable<Player> masterDatabase;
    private final List<Player> missingPlayers = Lists.newArrayList();
    private final List<Player> conflictPlyaers = Lists.newArrayList();

    public IdentityMerge(Iterable<Player> masterDatabase) {
        this.masterDatabase = masterDatabase;
    }

    public void merge(Iterable<Player> updateDatabase) {
        Map<String, Player> master = getMergeMap(masterDatabase);

        Map<String, Player> update = getMergeMap(updateDatabase);
        for (Map.Entry<String, Player> crnt : update.entrySet()) {
            String key = crnt.getKey();
            Player rhs = crnt.getValue();
            if (!master.containsKey(key)) {
                missingPlayers.add(rhs);
            } else {
                Player lhs = master.get(key);
                try {
                    lhs.getIds().merge(rhs.getIds());
                } catch (IdConflictException e) {
                    conflictPlyaers.add(rhs);
                }
            }
        }
    }

    public void collectMasterDatabase(PlayerCollector collector) {
        collector.collectAll(masterDatabase);
    }

    public void collectMissingDatabase(PlayerCollector colllector) {
        colllector.collectAll(missingPlayers);
    }

    public void collectConflictDatabase(PlayerCollector collector) {
        collector.collectAll(conflictPlyaers);
    }

    Map<String, Player> getMergeMap(Iterable<Player> database) {
        return DatabaseFactory.createIdMap(database, new BioReader());
    }

    public static void main(String[] args) throws IOException {
        String rootDatabase = "mlb.players.json";
        Iterable<Player> core = DatabaseFactory.createDatabase(rootDatabase);
        core = Iterables.filter(core, Predicates.HAS_BIO);

        IdentityMerge theMerge = new IdentityMerge(core);

        String updates[] =
                {"espn.players.json"
                        , "lahman.players.json"
                        , "oliver.players.json"
                        , "rotoworld.players.json"
                        , "yahoo.players.json"
                        , "merged.players.json"
                };

        for(String updateDatabase : updates) {
            Iterable<Player> update = DatabaseFactory.createDatabase(updateDatabase);
            update = Iterables.filter(update, Predicates.HAS_BIO);
            theMerge.merge(update);
        }

        DatabaseWriter mergedOut = DatabaseFactory.createWriter("identitymerge.merged.json");
        DatabaseWriter missingOut = DatabaseFactory.createWriter("identitymerge.missing.json");
        DatabaseWriter conflictOut = DatabaseFactory.createWriter("identitymerge.conflict.json");

        MergeReader reader = new MergeReader(mergedOut, missingOut, conflictOut);
        reader.collect(theMerge);
        reader.onEnd();

    }
}
