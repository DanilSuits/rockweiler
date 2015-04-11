/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rank;

import com.typesafe.config.Config;
import jline.ConsoleReader;
import rockweiler.console.core.DumbTerminal;
import rockweiler.console.core.Main;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.lifecycle.RunningState;
import rockweiler.console.core.modules.Application;
import rockweiler.console.core.modules.FrontEnd;
import rockweiler.console.core.modules.Interpreter;
import rockweiler.console.core.modules.Startup;
import rockweiler.console.jline.UserInput;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Rank {
    public static void main(String[] args) throws Exception {
        Startup startup = Startup.create("quickdraft");

        Config config = startup.readConfiguration(args);

        File replayLog = new File(config.getString("quickdraft.replay.log"));
        Replay replay = Replay.create(replayLog);

        final File rankOutput = new File(args[0]);
        ReportFactory reportFactory = new ReportFactory() {
            public OutputStream openReport() {
                try {
                    return new FileOutputStream(rankOutput);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        FileInputStream masterRepo = new FileInputStream(config.getString("quickdraft.player.database"));
        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create(masterRepo);
        masterRepo.close();

        Application.Module appModule = RankApp.Module.create(repository);
        Interpreter.Module interpreterModule = RankInterpreter.Module.create(reportFactory, replay);

        // Didn't bother with TrivialFrontEnd here, as this module, rather than an
        // injector, was going to be doing the actual work.
        FrontEnd.Module frontEnd = FrontEnd.Module.create();


        // Coordinate the necessary bindings between the modules.
        final Application.Binding appBinding = appModule.createBinding();
        final Interpreter.Binding interpreterBinding = interpreterModule.getBinding(appBinding);

        // Create the application

        // represents the state of the application - starting, running
        // shutting down, stopped.
        final RunningState runningState = RunningState.start();

        // A simple command line terminal - read text from the user, which an
        // interpreter can turn into commands.  Similarly, display events to
        // the user in the view (ie: stdout)
        ConsoleReader reader = new ConsoleReader();
        DumbTerminal display = new DumbTerminal(System.out, System.err);

        MessageListener<String> userInterpreter = interpreterBinding.bind(runningState, display);

        // In each cycle, we read one command from the user, and pass it to the
        // application via the interpreter.
        UserInput userInput = new UserInput(reader, userInterpreter, display);

        // The loop that checks for shutdown is here, if we haven't shut down, the
        // UserInput module is allowed to run for one cycle.

        Main theApp = new Main(runningState, userInput);

        replay.replay(userInterpreter);

        // And run it.
        theApp.run();

    }
}
