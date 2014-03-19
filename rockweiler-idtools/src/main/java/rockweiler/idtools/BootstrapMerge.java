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
import rockweiler.player.BioReader;
import rockweiler.player.Biography;
import rockweiler.player.IdConflictException;
import rockweiler.player.IdReader;
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

    public Iterable<? extends Player> collectMasterDatabase() {
        return Iterables.filter(master.values(), Predicates.not(REJECTED));
    }

    public Iterable<? extends Player> collectMissingDatabase() {
        return Iterables.filter(master.values(), REJECTED);
    }

    public Iterable<? extends Player> collectConflictDatabase() {
        return conflictPlayers;
    }

    public static void main(String[] args) throws KeyStoreException {
        String rootDatabase = "master.players.json";

        final PlayerStore playerStore = FileBackedStore.create("");
        PlayerStore.Reader in = playerStore.createReader();

        Iterable<? extends Player> players = in.readPlayers(rootDatabase);
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
                        // , "baseballReference.players.json"
                        // , "biography.merged.json"
                };

        for (String updateDatabase : updates) {
            Iterable<? extends Player> update = in.readPlayers(updateDatabase);
            update = Iterables.filter(update, Biography.HAS_BIO_FILTER);
            theMerge.merge(update);
        }

        PlayerStore.Writer out = playerStore.createWriter();
        out.writePlayers("bootstrap.merged.json", theMerge.collectMasterDatabase());
        out.writePlayers("bootstrap.missing.json", theMerge.collectMissingDatabase());
        out.writePlayers("bootstrap.conflict.json", theMerge.collectConflictDatabase());
    }
}
