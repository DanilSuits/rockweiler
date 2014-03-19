/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.core.modules;

import jline.ConsoleReader;
import rockweiler.console.core.DumbTerminal;
import rockweiler.console.core.Main;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.lifecycle.RunningState;
import rockweiler.console.jline.UserInput;

import java.io.IOException;

/**
* @author Danil Suits (danil@vast.com)
*/
public class FrontEnd {
    public static class Module {
        public static Module create() {
            return new Module();
        }

        public Main createApp(Interpreter.Binding interpreterBinding) throws IOException {
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
            return new Main(runningState, userInput);

        }

    }
}
