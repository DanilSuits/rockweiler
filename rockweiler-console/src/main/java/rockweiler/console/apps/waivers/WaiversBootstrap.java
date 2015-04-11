/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.waivers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.modules.Application;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class WaiversBootstrap {

    public static void main(String[] args) throws IOException {
        File history = new File("season.history.json");
        FileOutputStream out = new FileOutputStream(history);
        final PrintStream eventLog = new PrintStream(out);

        System.err.println("Logging events to: " + history.getCanonicalPath());

        final MessageListener<Application.Request> toMaster = new MessageListener<Application.Request>() {
            final ObjectMapper om = new ObjectMapper();

            public void onMessage(Application.Request message) {
                try {
                    String readableMessage = om.writeValueAsString(message);
                    eventLog.println(readableMessage);
                    eventLog.flush();
                } catch (IOException e) {
                    throw new RuntimeException("unable to parse message", e);
                }
            }
        };

        final List<Schema.Player> players = Lists.newArrayList();
        final List<Schema.Player> provisional = Lists.newArrayList();

        PlayerRepository<Schema.Player> repository = new JacksonPlayerRepository(players, provisional);

        MessageListener<Application.Event> unknownMessageHandler = new AbortOnError();
        WaiversInterpreter.UserInterpreter userInterpreter = WaiversInterpreter.UserInterpreter.create(unknownMessageHandler, repository, toMaster);

        MessageListener<Requests.AddPlayer> onAddPlayer = new MessageListener<Requests.AddPlayer>() {
            public void onMessage(Requests.AddPlayer message) {
                toMaster.onMessage(message);
                players.add(message.addPlayer);
            }
        };

        BootstrapReplay replay = new BootstrapReplay(onAddPlayer);

        replay.replay();

        File updates = new File("/Users/danil/Dropbox/OOOL/data/2014/database/repositoryPipeline.join.add.json");
        FileInputStream in = new FileInputStream(updates);
        replay.replay(JacksonPlayerRepository.create(in));


        File draftScript = new File("roster.replay.log");
        BufferedReader br = new BufferedReader(new FileReader(draftScript));

        String line;
        while ((line = br.readLine()) != null) {
            userInterpreter.onMessage(line);
        }
        br.close();

        eventLog.close();
    }

    static class AbortOnError implements MessageListener<Application.Event> {
        public void onMessage(Application.Event message) {
            ObjectMapper om = new ObjectMapper();
            try {
                throw new RuntimeException( om.writeValueAsString(message));
            } catch (IOException e) {
                throw new RuntimeException("unable to parse error", e);
            }
        }
    }
}
