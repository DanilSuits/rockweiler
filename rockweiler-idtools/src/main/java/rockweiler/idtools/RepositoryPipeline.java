/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import rockweiler.player.jackson.IdStore;
import rockweiler.player.jackson.Schema;
import rockweiler.player.jackson.SimpleArchive;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class RepositoryPipeline {
    static Function<Schema.Player, String> BIO_KEY = new Function<Schema.Player, String>() {
        public String apply(Schema.Player input) {
            return input.bio.name + input.bio.dob;
        }
    };

    static class Join {
        Map<String, Schema.Player> players = Maps.newTreeMap();

        public void scan(Iterable<Schema.Player> players) {
            for(Schema.Player player : players) {
                scan(player);
            }
        }

        public void scan(Schema.Player player) {
            String key = BIO_KEY.apply(player);
            Schema.Player known = players.get(key);
            if (known != null) {
                player.id.putAll(known.id);
            }

            players.put(key, player);
        }

        public List<Schema.Player> get(final Predicate<Schema.Player> filter) {
            return Lists.newArrayList(Iterables.filter(players.values(), filter));
        }

    }

    static class BioSplit {

        public static BioSplit create(Iterable<Schema.Player> players) {
            Map<String,Schema.Player> knownPlayers = Maps.uniqueIndex(players, BIO_KEY);
            final Function<String, Schema.Player> bioLookup = Functions.forMap(knownPlayers, IdStore.PLAYER_NOT_FOUND);
            Function<Schema.Player, Schema.Player> match = new Function<Schema.Player, Schema.Player>() {
                public Schema.Player apply(rockweiler.player.jackson.Schema.Player input) {
                    String key = BIO_KEY.apply(input);
                    return bioLookup.apply(key);
                }
            };

            return new BioSplit(match);
        }

        private final Function<Schema.Player,Schema.Player> matchPlayer;

        List<Schema.Player> inserts = Lists.newArrayList();
        List<Schema.Player> rejected = Lists.newArrayList();
        List<Schema.Player> conflicts = Lists.newArrayList();
        List<Schema.Player> merges = Lists.newArrayList();

        BioSplit(Function<Schema.Player,Schema.Player> matchPlayer) {
            this.matchPlayer = matchPlayer;
        }

        public void scan(Iterable<Schema.Player> update) {
            for(Schema.Player player : update) {
                scan(player);
            }
        }

        public void scan(Schema.Player player) {
            if (HAS_BIO.apply(player)) {
                Schema.Player bestMatch = matchPlayer.apply(player);
                if (IdStore.PLAYER_NOT_FOUND == bestMatch) {
                    inserts.add(player);
                } else {
                    boolean conflict = false;
                    for(Map.Entry<String,String> knownId : bestMatch.id.entrySet()) {
                        String newId = player.id.get(knownId.getKey());
                        if ( null != newId) {
                            if (! knownId.getValue().equals(newId)) {
                                conflict = true;
                            }
                        }
                    }

                    if (conflict) {
                        conflicts.add(player);
                    } else {
                        bestMatch.id.putAll(player.id);
                        merges.add(bestMatch);
                    }
                }
            } else {
                rejected.add(player);
            }
        }
    }

    static class IdSplit {
        private final IdStore idStore;

        List<Schema.Player> rejected = Lists.newArrayList();
        List<Schema.Player> conflict = Lists.newArrayList();
        List<Schema.Player> inserts = Lists.newArrayList();


        IdSplit(IdStore idStore) {
            this.idStore = idStore;
        }

        public void scan(Iterable<Schema.Player> update) {
            for(Schema.Player player : update) {
                scan(player);
            }
        }

        public void scan(Schema.Player player) {
            if (HAS_BIO.apply(player)) {

                boolean found = false;

                for(Map.Entry<String,String> entry : player.id.entrySet()) {
                    Schema.Player known = idStore.find(entry.getKey(),entry.getValue());
                    if (known != IdStore.PLAYER_NOT_FOUND) {
                        if (match(player, known)) {
                            found = true;
                            // NoOp
                        } else {
                            conflict.add(player);
                            return;
                        }
                    }
                }

                if (! found) {
                    inserts.add(player);
                }
            } else {
                rejected.add(player);
            }
        }

        public boolean match(Schema.Player lhs, Schema.Player rhs) {
            if (lhs.bio.dob.equals(rhs.bio.dob)) {
                if (lhs.bio.name.equals(rhs.bio.name)) {
                    return true;
                }
            }

            return false;
        }
    }

    static final Predicate<Schema.Player> HAS_BIO = new Predicate<Schema.Player>() {
         public boolean apply(rockweiler.player.jackson.Schema.Player player) {
             if (null == player.bio) {
                 return false;
             }

             if (null == player.bio.name) {
                 return false;
             }

             if (null == player.bio.dob) {
                 return false;
             }

             return true;
         }
     };

    public static void main(String[] args) throws Exception {
        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");
        IdStore idStore = IdStore.create(repository.getPlayers());

        String updates[] =
                {
                        "mlb.players.json"
                        , "espn.players.json"
                        , "lahman.players.json"
                        , "rotoworld.players.json"
                        , "yahoo.players.json"
                };

        List<Schema.Player> allUpdates = Lists.newArrayList();

        for (String updateDatabase : updates) {
            File source = new File(updateDatabase);
            FileInputStream in = new FileInputStream(source);

            for(Schema.Player player : JacksonPlayerRepository.create(in).getPlayers()) {
                allUpdates.add(player);
            }
        }

        SimpleArchive<Schema.Player> archive = new SimpleArchive<Schema.Player>();
        RepositoryUpdate.ReportGenerator reportGenerator = new RepositoryUpdate.ReportGenerator(archive);

        IdSplit idSplit = new IdSplit(idStore);
        idSplit.scan(allUpdates);

        reportGenerator.write("repositoryPipeline.id.conflict.json", idSplit.conflict);
        reportGenerator.write("repositoryPipeline.id.rejected.json", idSplit.rejected);
        reportGenerator.write("repositoryPipeline.id.new.json", idSplit.inserts);

        BioSplit bioSplit = BioSplit.create(repository.getPlayers());
        bioSplit.scan(idSplit.inserts);

        reportGenerator.write("repositoryPipeline.bio.conflict.json", bioSplit.conflicts);
        reportGenerator.write("repositoryPipeline.bio.rejected.json", bioSplit.rejected);
        reportGenerator.write("repositoryPipeline.bio.merge.json", bioSplit.merges);
        reportGenerator.write("repositoryPipeline.bio.new.json", bioSplit.inserts);

        Join join = new Join();
        join.scan(bioSplit.inserts);

        Predicate<Schema.Player> multipleIds = new Predicate<Schema.Player>() {
            public boolean apply(Schema.Player input) {
                return input.id.size() > 1;
            }
        };

        reportGenerator.write("repositoryPipeline.join.add.json", join.get(multipleIds));
        reportGenerator.write("repositoryPipeline.join.not.json", join.get(Predicates.not(multipleIds)));
    }
}
