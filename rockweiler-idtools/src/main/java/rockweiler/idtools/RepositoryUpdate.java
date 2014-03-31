/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.base.Predicate;
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
import rockweiler.player.jackson.SimpleArchive;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.io.*;
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

    Map<String, Map<String, Schema.Player>> index = Maps.newHashMap();

    List<Schema.Player> conflicts = Lists.newArrayList();
    List<Schema.Player> missing = Lists.newArrayList();

    public void merge(Iterable<Schema.Player> updateDatabase) {
        for (Schema.Player rhs : updateDatabase) {
            boolean matched = false;
            boolean conflict = false;

            for (Map.Entry<String, String> entry : rhs.id.entrySet()) {
                String key = entry.getKey();
                String id = entry.getValue();

                Map<String, Schema.Player> repo = index.get(key);
                if (null != repo) {
                    Schema.Player original = repo.get(id);
                    if (null != original) {
                        matched = true;

                        if (!match(rhs, original)) {
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

    boolean match(Schema.Player rhs, Schema.Player original) {
        return bioReader.getId(rhs).equals(bioReader.getId(original));
    }

    static final Predicate<Schema.Player> HAS_BIO = new Predicate<Schema.Player>() {
        public boolean apply(rockweiler.player.jackson.Schema.Player player) {
            return null != player.bio;
        }
    };

    public static void main(String[] args) throws Exception {

        RepositoryUpdate theMerge = new RepositoryUpdate();

        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");

        for (Schema.Player p : repository.getPlayers()) {
            for (Map.Entry<String, String> id : p.id.entrySet()) {
                Map<String, Schema.Player> repo = theMerge.index.get(id.getKey());
                if (null == repo) {
                    repo = Maps.newHashMap();
                    theMerge.index.put(id.getKey(), repo);
                }

                repo.put(id.getValue(), p);
            }
        }

        String updates[] =
                {
                        "mlb.players.json"
                        , "espn.players.json"
                        , "lahman.players.json"
                        , "rotoworld.players.json"
                        , "yahoo.players.json"
                };


        for (String updateDatabase : updates) {
            File source = new File(updateDatabase);
            FileInputStream in = new FileInputStream(source);

            Iterable<Schema.Player> update = JacksonPlayerRepository.create(in).getPlayers();
            update = Iterables.filter(update, HAS_BIO);
            theMerge.merge(update);
        }


        SimpleArchive<Schema.Player> archive = new SimpleArchive<Schema.Player>();
        ReportGenerator reportGenerator = new ReportGenerator(archive);

        reportGenerator.write("update.missing.json", theMerge.missing);
        reportGenerator.write("update.conflict.json", theMerge.conflicts);

    }

    static class ReportGenerator {
        private final SimpleArchive<Schema.Player> archive;

        ReportGenerator(SimpleArchive<Schema.Player> archive) {
            this.archive = archive;
        }

        public void write(String reportName, List<? extends Schema.Player> players) throws IOException {
            FileOutputStream report = create(reportName);
            try {
                archive.archive(players, report);
            } finally {
                report.flush();
                report.close();
            }
        }

        FileOutputStream create(String reportName) throws FileNotFoundException {
            File target = new File(reportName);
            return new FileOutputStream(target);
        }
    }
}
