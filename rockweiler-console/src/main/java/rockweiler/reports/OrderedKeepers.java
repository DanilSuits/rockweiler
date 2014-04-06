/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.reports;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.typesafe.config.Config;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
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
public class OrderedKeepers {
    public static void main(String[] args) throws Exception {
        Startup startup = Startup.create("orderingreport");

        Config config = startup.readConfiguration(args);

        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");
        final IdStore idStore = IdStore.create(repository.getPlayers());

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

        File keeperSource = new File("/Users/Danil/Dropbox/OOOL/data/2014/draft/2013.keepers.oool.json");
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        List<Schema.Player> players = om.readValue(keeperSource, JacksonPlayerRepository.SCHEMA_PLAYER_REPO);
        List<Schema.Player> ordered = Lists.transform(players,transform);
        ordered = Lists.newArrayList(ordered);
        Collections.sort(ordered,ordering);

        SimpleArchive<Schema.Player> archive = new SimpleArchive<Schema.Player>();
        RepositoryUpdate.ReportGenerator reportGenerator = new RepositoryUpdate.ReportGenerator(archive);
        reportGenerator.write("/Users/Danil/Dropbox/OOOL/data/2014/draft/2013.keepers.ordered.json",ordered);



    }
}
