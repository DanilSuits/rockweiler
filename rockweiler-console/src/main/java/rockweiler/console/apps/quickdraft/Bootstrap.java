/**
 * Copyright Vast 2015. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.apps.quickdraft;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import rockweiler.console.core.modules.Startup;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.JacksonPlayerRepository;

import java.io.FileInputStream;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Bootstrap {
    public static void main(String[] args) throws Exception {
        Startup startup = Startup.create("bootstrap");

        Config config = startup.readConfiguration(args);

        FileInputStream masterRepo = new FileInputStream(config.getString("bootstrap.player.database"));
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        List<Schema.Player> players = om.readValue(masterRepo, JacksonPlayerRepository.SCHEMA_PLAYER_REPO);

        for (Schema.Player player : players) {
            Command command = new AddPlayer(player);

            System.out.println(om.writeValueAsString(command));
        }



    }

    public static class Command {
        public final String command;

        public Command(String command) {
            this.command = command;
        }
    }

    public static class AddPlayer extends Command {
        public final Schema.Player player;

        public AddPlayer(Schema.Player player) {
            super("repo.addPlayer");
            this.player = player;
        }
    }
}
