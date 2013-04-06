/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.collect.Lists;
import rockweiler.idtools.player.IdConflictException;
import rockweiler.idtools.player.IdReader;
import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.PlayerCollector;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class IdMerge {
    private final Iterable<Player> masterDatabase;
    private final List<Player> missingPlayers = Lists.newArrayList();
    private final List<Player> conflictPlyaers = Lists.newArrayList();

    public IdMerge(Iterable<Player> masterDatabase) {
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

    public void collectMaster(PlayerCollector collector) {
        collector.collectAll(masterDatabase);
    }

    public void collectMissing(PlayerCollector colllector) {
        colllector.collectAll(missingPlayers);
    }

    public void collectConflict(PlayerCollector collector) {
        collector.collectAll(conflictPlyaers);
    }

    static class BioReader implements IdReader {
        public String getId(Player p) {
            Player.Bio bio = p.getBio();
            String key = bio.getDob() + bio.getName();

            return key;
        }
    }

    Map<String, Player> getMergeMap(Iterable<Player> database) {
        return DatabaseFactory.createIdMap(database, new BioReader());
    }

    public static void main(String[] args) throws FileNotFoundException {
        String rootDatabase = "mlb.players.json";
        String updateDatabase = "lahman.players.json";

        Iterable<Player> core = DatabaseFactory.createDatabase(rootDatabase);

        IdMerge theMerge = new IdMerge(core);

        // Iterable<Player> update = DatabaseFactory.createDatabase(updateDatabase);
        // theMerge.merge(update);

        String mergedDatabase = "merged.players.json";
        DatabaseWriter mergedOut = DatabaseFactory.createWriter(mergedDatabase);
        theMerge.collectMaster(mergedOut.collector());
        mergedOut.onEnd();

        String missingDatabase = "missing.players.json";
        String conflictDatabase = "conflict.players.json";
    }
}
