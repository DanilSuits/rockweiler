package rockweiler.console.apps.quickdraft;

import jline.ConsoleReader;
import rockweiler.console.apps.rank.RankApp;
import rockweiler.console.apps.rank.RankInterpreter;
import rockweiler.console.apps.rank.Replay;
import rockweiler.console.core.DumbTerminal;
import rockweiler.console.core.Main;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.lifecycle.RunningState;
import rockweiler.console.core.modules.Application;
import rockweiler.console.core.modules.FrontEnd;
import rockweiler.console.core.modules.Interpreter;
import rockweiler.console.jline.UserInput;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/29/14
 * Time: 9:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuickDraft {
    public static void main(String[] args) throws Exception {
        // TODO: this needs to read from a configuration
        File replayLog = new File(System.getProperty("java.io.tmpdir"), "quickdraft.replay.log");
        Replay replay = Replay.create(replayLog);

        Application.Module appModule = QuickDraftApp.Module.create();
        Interpreter.Module interpreterModule = QuickDraftInterpreter.Module.create(replay);

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
