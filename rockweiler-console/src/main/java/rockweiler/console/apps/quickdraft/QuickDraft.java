package rockweiler.console.apps.quickdraft;

import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import jline.ConsoleReader;
import rockweiler.console.apps.quickdraft.plugins.IdStore;
import rockweiler.console.apps.quickdraft.plugins.ListViewport;
import rockweiler.console.apps.quickdraft.plugins.LocalListRepository;
import rockweiler.console.apps.quickdraft.plugins.LocalWatchRepository;
import rockweiler.console.apps.rank.Replay;
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
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/29/14
 * Time: 9:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuickDraft {

    public static void main(String[] args) throws Exception {

        Startup startup = Startup.create("quickdraft");

        Config config = startup.readConfiguration(args);


        File replayLog = new File(config.getString("quickdraft.replay.log"));

        Replay replay = Replay.create(replayLog);

        FileInputStream masterRepo = new FileInputStream(config.getString("quickdraft.player.database"));
        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create(masterRepo);
        masterRepo.close();

        IdStore idStore = IdStore.create(repository.getPlayers());

        // A simple command line terminal - read text from the user, which an
        // interpreter can turn into commands.  Similarly, display events to
        // the user in the view (ie: stdout)
        ConsoleReader reader = new ConsoleReader();
        DumbTerminal display = new DumbTerminal(System.out, System.err);


        Application.Module appModule = QuickDraftApp.Module.create(repository);

        List<Schema.Player> watchList = Lists.newArrayList();
        LocalWatchRepository watchRepository = new LocalWatchRepository(watchList);

        final LocalListRepository.Builder builder = LocalListRepository.builder(config.getConfig("quickdraft.listRepository"), idStore);
        builder.withWatchList(watchList);

        ListRepository listRepository = LocalListRepository.create(builder);


        ListViewport listViewport = new ListViewport(display, listRepository, watchRepository);

        Interpreter.Module interpreterModule = QuickDraftInterpreter.Module.create(replay, listRepository, listViewport);

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
