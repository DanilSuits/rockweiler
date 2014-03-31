package rockweiler.reports;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import rockweiler.console.apps.quickdraft.plugins.IdStore;
import rockweiler.console.core.modules.Startup;
import rockweiler.player.Player;
import rockweiler.player.io.FileBackedStore;
import rockweiler.player.io.PlayerStore;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/30/14
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class RotoworldDepthReport {
    public static void main(String[] args) throws Exception {
        Startup startup = Startup.create("rotoworld-depth");

        Config config = startup.readConfiguration(args);

        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");
        IdStore idStore = IdStore.create(repository.getPlayers());

        // TODO: cruft
        File source = new File(config.getString("rotoworld.depth.source"));
        FileInputStream in = new FileInputStream(source);


        Iterable<Schema.Player> players = JacksonPlayerRepository.create(in).getPlayers();

        for( Schema.Player p : players) {
            String id = p.id.get("rotoworld");
            Schema.Player knownPlayer = idStore.find("rotoworld", id);
            if (null == knownPlayer) {
                System.out.println(p.bio.dob + " : " + p.bio.name);
            }
        }


    }
}
