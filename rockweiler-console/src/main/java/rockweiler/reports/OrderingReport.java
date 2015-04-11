/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.reports;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import rockweiler.console.apps.quickdraft.plugins.IdStore;
import rockweiler.console.apps.quickdraft.plugins.LocalListRepository;
import rockweiler.console.core.modules.Startup;
import rockweiler.idtools.RepositoryUpdate;
import rockweiler.player.jackson.Schema;
import rockweiler.player.jackson.SimpleArchive;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class OrderingReport {
    public static class RotoData {
        public String team;
        public Map<String, String> depth;
    }

    public static class RotoPlayer extends Schema.Player {
        public Map<String, RotoData> years;
    }

    public static class ByPosition {
        List<Schema.Player> all = Lists.newArrayList();
        List<Schema.Player> start = Lists.newArrayList();
        int rank = 0;
    }

    public static void main(String[] args) throws Exception {
        Startup startup = Startup.create("orderingreport");

        Config config = startup.readConfiguration(args);

        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");
        final IdStore idStore = IdStore.create(repository.getPlayers());
        LocalListRepository.Builder builder = new LocalListRepository.Builder(idStore);

        final Map<Schema.Player, Integer> ranking = Maps.newHashMap();

        final Ordering<Integer> rankOrdering = Ordering.<Integer>natural().nullsLast();

        final Ordering<Schema.Player> ordering = new Ordering<Schema.Player>() {
            @Override
            public int compare(Schema.Player left, Schema.Player right) {
                return rankOrdering.compare(ranking.get(left), ranking.get(right));
            }
        };

        for (String rank : config.getStringList("orderingreport.ranked")) {
            List<Schema.Player> rankedPlayers = builder.createList(rank);

            int currentRank = 0;
            for (Schema.Player p : rankedPlayers) {
                currentRank++;
                ranking.put(p, currentRank);
            }
        }


        File keepers = new File(config.getString("orderingreport.keeper"));
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final TypeReference<List<LahmanReport.KeeperRecord>> KEEPER_RECORD_REPO = new TypeReference<List<LahmanReport.KeeperRecord>>() {
        };

        final Map<Schema.Player, String> players2keeper = Maps.newHashMap();

        List<LahmanReport.KeeperRecord> keeperRecords = om.readValue(keepers, KEEPER_RECORD_REPO);
        for (LahmanReport.KeeperRecord k : keeperRecords) {
            Schema.Player p = idStore.find("lahman", k.lahman);
            if (IdStore.PLAYER_NOT_FOUND != p) {
                players2keeper.put(p, k.year);
            }
        }

        final String yearFilter = config.getString("orderingreport.year");

        final Ordering<String> yearOrdering = Ordering.natural().reverse().nullsFirst();

        Predicate<Schema.Player> filterCheapPlayers = new Predicate<Schema.Player>() {
            public boolean apply(Schema.Player input) {
                String year = players2keeper.get(input);
                boolean accept = yearOrdering.compare(yearFilter, year) > 0;
                return accept;
            }
        };


        final TypeReference<List<RotoPlayer>> ROTO_PLAYER_REPO = new TypeReference<List<RotoPlayer>>() {
        };

        File roto = new File(config.getString("orderingreport.depth"));
        List<RotoPlayer> rotoRecords = om.readValue(roto, ROTO_PLAYER_REPO);

        Config pos = config.getConfig("orderingreport.pos");

        Function<Schema.Player, Schema.Player> transform = new Function<Schema.Player, Schema.Player>() {
            public Schema.Player apply(Schema.Player rhs) {
                for (Map.Entry<String, String> id : rhs.id.entrySet()) {
                    Schema.Player lhs = idStore.find(id.getKey(), id.getValue());
                    if (null != lhs) {
                        return lhs;
                    }
                }

                return rhs;
            }
        };

        Map<String, ByPosition> byPosition = Maps.newHashMap();

        for (Map.Entry<String, ConfigValue> k : pos.root().entrySet()) {
            String position = k.getKey();
            Integer depthLimit = (Integer) k.getValue().unwrapped();
            ByPosition positionData = new ByPosition();
            positionData.rank = depthLimit;
            byPosition.put(position, positionData);
        }

        Map<String,String> rotoPositions = Maps.newHashMap();
        for (Map.Entry<String, ConfigValue> k : config.getConfig("orderingreport.rotoPositions").root().entrySet()) {
            rotoPositions.put(k.getKey(), (String)k.getValue().unwrapped());
        }

        for (RotoPlayer r : rotoRecords) {
            Schema.Player s = idStore.find("rotoworld", r.id.get("rotoworld"));
            if (null == s) {
                System.err.println("Missing player: rotoworld:" + r.id.get("rotoworld") + " " + r.bio.name);
                continue;
            }

            if (null != r.years) {
                RotoData d = r.years.get("2014");
                if (null != d) {
                    for (Map.Entry<String, String> entry : d.depth.entrySet()) {
                        String positionKey = rotoPositions.get(entry.getKey());
                        ByPosition positionData = byPosition.get(positionKey);
                        positionData.all.add(s);

                        if (Integer.parseInt(entry.getValue()) <= positionData.rank) {
                            positionData.start.add(s);
                        }
                    }
                }
            }
        }

        SimpleArchive<Schema.Player> archive = new SimpleArchive<Schema.Player>();
        RepositoryUpdate.ReportGenerator reportGenerator = new RepositoryUpdate.ReportGenerator(archive);

        for (Map.Entry<String,ByPosition> entry : byPosition.entrySet()) {
            String position = entry.getKey();
            ByPosition positionData = entry.getValue();

            Collections.sort(positionData.all, ordering);
            reportGenerator.write("/Users/Danil/Dropbox/OOOL/data/2014/draft/" + position + ".all.json", positionData.all);
            Collections.sort(positionData.start, ordering);
            reportGenerator.write("/Users/Danil/Dropbox/OOOL/data/2014/draft/" + position + ".start.json", positionData.start);



            Iterable<Schema.Player> players = Iterables.transform(positionData.start, transform);
            players = Iterables.filter(players, filterCheapPlayers);
            List result = Lists.newArrayList(players);
            Collections.sort(result, ordering);

            String destination = "/Users/Danil/Dropbox/OOOL/data/2014/draft/" + position + ".cheap.json";
            reportGenerator.write(destination, result);


        }

    }

}
