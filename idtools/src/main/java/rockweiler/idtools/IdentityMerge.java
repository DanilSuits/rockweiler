/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import rockweiler.idtools.player.BioReader;
import rockweiler.idtools.player.Biography;
import rockweiler.idtools.player.IdConflictException;
import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.PlayerCollector;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class IdentityMerge implements PlayerMerge {
    private final Iterable<? extends Player> masterDatabase;
    private final List<Player> missingPlayers = Lists.newArrayList();
    private final List<Player> conflictPlyaers = Lists.newArrayList();

    public IdentityMerge(Iterable<? extends Player> masterDatabase) {
        this.masterDatabase = masterDatabase;
    }

    public void merge(Iterable<? extends Player> updateDatabase) {
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

    Map<String, Player> getMergeMap(Iterable<? extends Player> database) {
        return DatabaseFactory.createIdMap(database, new BioReader());
    }

    public static void main(String[] args) throws IOException {
        String rootDatabase = "mlb.players.json";
        Iterable<? extends Player> core = DatabaseFactory.createDatabase(rootDatabase);
        core = Iterables.filter(core, Biography.HAS_BIO_FILTER);

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
            Iterable< ? extends Player> update = DatabaseFactory.createDatabase(updateDatabase);
            update = Iterables.filter(update, Biography.HAS_BIO_FILTER);
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
