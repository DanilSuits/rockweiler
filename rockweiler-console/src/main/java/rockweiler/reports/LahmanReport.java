package rockweiler.reports;

import com.google.common.collect.Sets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import rockweiler.console.core.modules.Startup;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/30/14
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class LahmanReport {
    public static void main(String[] args) throws Exception {
        Config config = ConfigFactory.load("lahmanreport");
        Startup startup = new Startup(config);

        config = startup.readConfiguration(args);

        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");

        Set<String> knownPlayers = Sets.newHashSet();
        for( Schema.Player p : repository.getPlayers()) {
            String lahmanId = p.id.get("lahman");
            if (null != lahmanId) {
                knownPlayers.add(lahmanId);
            }
        }

    }
}
