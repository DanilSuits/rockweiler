/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import rockweiler.player.BioReader;
import rockweiler.player.Biography;
import rockweiler.player.Player;
import rockweiler.player.io.FileBackedStore;
import rockweiler.player.io.KeyNotFoundException;
import rockweiler.player.io.KeyNotUpdatedException;
import rockweiler.player.io.PlayerStore;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class RepositoryUpdate {
    private static final BioReader bioReader = new BioReader();

    private static final Ordering<Player> SORT = new Ordering<Player>() {
        @Override
        public int compare(Player left, Player right) {
            String lhs = bioReader.getId(left);
            String rhs = bioReader.getId(right);
            return String.CASE_INSENSITIVE_ORDER.compare(lhs, rhs);
        }
    };

    Map<String, Map<String,Player>> index = Maps.newHashMap();

    List<Player> conflicts = Lists.newArrayList();
    List<Player> missing = Lists.newArrayList();

    public void merge(Iterable<? extends Player> updateDatabase) {
        for (Player rhs : updateDatabase) {
            boolean matched = false;
            boolean conflict = false;

            for(String key : rhs.getIds().all()) {
                String id = rhs.getIds().get(key);

                Map<String,Player> repo = index.get(key);
                if (null != repo) {
                    Player original = repo.get(id);
                    if (null != original) {
                        matched = true;

                        if (! match(rhs,original)) {
                            conflict = true;
                        }
                    }
                }
            }

            if (conflict) {
                conflicts.add(rhs);
            } else if (matched) {
               // No-Op
            } else {
                missing.add(rhs);
            }
        }
    }

    boolean match(Player rhs, Player original) {
        return bioReader.getId(rhs).equals(bioReader.getId(original));
    }

    public static void main(String[] args) throws KeyNotUpdatedException, KeyNotFoundException {

        RepositoryUpdate theMerge = new RepositoryUpdate();

        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");

        for(Schema.Player p : repository.getPlayers()) {
            for (Map.Entry<String,String> id : p.id.entrySet()) {
                Map<String,Player> repo = theMerge.index.get(id.getKey());
                if (null == repo) {
                    repo = Maps.newHashMap();
                    theMerge.index.put(id.getKey(),repo);
                }

                repo.put(id.getValue(),Schema.TRANSFORM.apply(p));
            }
        }

        final FileBackedStore playerStore = new FileBackedStore(new File(""));
        PlayerStore.Reader in = playerStore.createReader();

        String updates[] =
                {
                        "mlb.players.json"
                        , "espn.players.json"
                        , "lahman.players.json"
                        , "rotoworld.players.json"
                        , "yahoo.players.json"
                };


        for (String updateDatabase : updates) {
            Iterable<? extends Player> update = in.readPlayers(updateDatabase);
            update = Iterables.filter(update, Biography.HAS_BIO_FILTER);
            theMerge.merge(update);
        }


        PlayerStore.Writer out = playerStore.createWriter(SORT);
        out.writePlayers("update.missing.json", theMerge.missing);
        out.writePlayers("update.conflict.json", theMerge.conflicts);

    }
}
