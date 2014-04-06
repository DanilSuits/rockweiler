package rockweiler.reports;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import rockweiler.console.core.modules.Startup;
import rockweiler.idtools.RepositoryUpdate;
import rockweiler.player.jackson.Schema;
import rockweiler.player.jackson.SimpleArchive;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/30/14
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class LahmanReport {
    public static class KeeperRecord {
        public String lahman;
        public String year;
    }

    public static void main(String[] args) throws Exception {
        Config config = ConfigFactory.load("lahmanreport");
        Startup startup = new Startup(config);

        config = startup.readConfiguration(args);

        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");

        Set<String> knownPlayers = Sets.newTreeSet();
        for( Schema.Player p : repository.getPlayers()) {
            String lahmanId = p.id.get("lahman");
            if (null != lahmanId) {
                knownPlayers.add(lahmanId);
            }
        }

        Map<String,Integer> columns = Maps.newHashMap();

        CSVReader csv = new CSVReader(new FileReader(config.getString("lahmanreport.batting.file") ));
        String[] headers = csv.readNext();
        for(int x = 0; x < headers.length; ++x) {
            columns.put(headers[x], x);
        }

        Map<String,Map<String,Integer>> history = Maps.newHashMap();

        int idColumn = columns.get("playerID");
        int yearColumn = columns.get("yearID");
        int abColumn = columns.get("AB");

        String [] nextLine = null;
        while((nextLine = csv.readNext()) != null) {
            if (nextLine[abColumn].isEmpty()) {
                continue;
            }
            String playerId = nextLine[idColumn];
            if ( knownPlayers.contains(playerId)) {
                if (! history.containsKey(playerId)) {
                    history.put(playerId, Maps.<String, Integer>newTreeMap());
                }
                Map<String,Integer> data = history.get(playerId);
                try {
                    data.put(nextLine[yearColumn], Integer.parseInt(nextLine[abColumn]));
                } catch (NumberFormatException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

        Map<String,Integer> clock = Maps.newTreeMap();

        for(Map.Entry<String,Map<String,Integer>> entry : history.entrySet()) {
            int sum = 0;
            String playerId = entry.getKey();
            Map<String, Integer> data = entry.getValue();
            for(Map.Entry<String,Integer> year : data.entrySet()) {
                sum += year.getValue();
                if (sum > 150) {
                    int yearId = Integer.parseInt(year.getKey());
                    clock.put(playerId, yearId);
                    break;
                }
            }
        }

        history.clear();

        csv = new CSVReader(new FileReader(config.getString("lahmanreport.pitching.file") ));
        headers = csv.readNext();
        for(int x = 0; x < headers.length; ++x) {
            columns.put(headers[x], x);
        }

        idColumn = columns.get("playerID");
        yearColumn = columns.get("yearID");
        int outsColumn = columns.get("IPouts");

        while((nextLine = csv.readNext()) != null) {
            if (nextLine[outsColumn].isEmpty()) {
                continue;
            }

            String playerId = nextLine[idColumn];
            if ( knownPlayers.contains(playerId)) {
                if (! history.containsKey(playerId)) {
                    history.put(playerId, Maps.<String,Integer>newTreeMap());
                }
                Map<String,Integer> data = history.get(playerId);
                data.put(nextLine[yearColumn], Integer.parseInt(nextLine[outsColumn]));
            }
        }

        for(Map.Entry<String,Map<String,Integer>> entry : history.entrySet()) {
            int sum = 0;
            String playerId = entry.getKey();
            Map<String, Integer> data = entry.getValue();
            for(Map.Entry<String,Integer> year : data.entrySet()) {
                sum += year.getValue();
                if (sum > 150) {
                    int yearId = Integer.parseInt(year.getKey());

                    if (clock.containsKey(playerId)) {
                        yearId = Math.min(yearId,clock.get(playerId));
                    }
                    clock.put(playerId, yearId);

                    break;
                }
            }
        }

        List<KeeperRecord> report = Lists.newArrayList();

        for(String playerId : knownPlayers) {
            // TODO
            int yearId = 2014;
            if (clock.containsKey(playerId)) {
                yearId = clock.get(playerId);
            }
            KeeperRecord keeper = new KeeperRecord();
            keeper.lahman = playerId;
            keeper.year = String.valueOf(yearId);

            report.add(keeper);

        }

        SimpleArchive<KeeperRecord> archive = new SimpleArchive<KeeperRecord>();

        FileOutputStream out = new FileOutputStream(config.getString("lahmanreport.output.file"));
        archive.archive(report,out);


    }
}
