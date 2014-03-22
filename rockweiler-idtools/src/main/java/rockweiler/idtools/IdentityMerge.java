/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import rockweiler.player.BioReader;
import rockweiler.player.Biography;
import rockweiler.player.IdConflictException;
import rockweiler.player.Player;
import rockweiler.player.database.DatabaseFactory;
import rockweiler.player.io.FileBackedStore;
import rockweiler.player.io.KeyStoreException;
import rockweiler.player.io.PlayerStore;

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

    public Iterable<? extends Player> collectMasterDatabase() {
        return masterDatabase;
    }

    public Iterable<? extends Player> collectMissingDatabase() {
        return missingPlayers;
    }

    public Iterable<? extends Player> collectConflictDatabase() {
        return conflictPlyaers;
    }

    Map<String, Player> getMergeMap(Iterable<? extends Player> database) {
        return DatabaseFactory.createIdMap(database, new BioReader());
    }

    public static void main(String[] args) throws KeyStoreException {
        String rootDatabase = "master.players.json";

        final PlayerStore playerStore = FileBackedStore.create("");
        PlayerStore.Reader in = playerStore.createReader();

        Iterable<? extends Player> core = in.readPlayers(rootDatabase);
        core = Iterables.filter(core, Biography.HAS_BIO_FILTER);

        IdentityMerge theMerge = new IdentityMerge(core);

        String updates[] =
                {"espn.players.json"
                        , "lahman.players.json"
                        , "rotoworld.players.json"
                        , "yahoo.players.json"
                        , "mlb.players.json"
                };

        for(String updateDatabase : updates) {
            Iterable< ? extends Player> update = in.readPlayers(updateDatabase);
            update = Iterables.filter(update, Biography.HAS_BIO_FILTER);
            theMerge.merge(update);
        }

        PlayerStore.Writer out = playerStore.createWriter();
        out.writePlayers("identitymerge.merged.json", theMerge.collectMasterDatabase());
        out.writePlayers("identitymerge.missing.json", theMerge.collectMissingDatabase());
        out.writePlayers("identitymerge.conflict.json", theMerge.collectConflictDatabase());
    }
}
