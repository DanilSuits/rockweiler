/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rosters;

import rockweiler.console.core.Main;
import rockweiler.console.core.modules.Application;
import rockweiler.console.core.modules.FrontEnd;
import rockweiler.console.core.modules.Interpreter;

import java.io.IOException;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Rosters {
    public static void main(String[] args) throws IOException {

        Application.Module appModule = RostersApp.Module.create();
        Interpreter.Module interpreterModule = RostersInterpreter.Module.create();

        // Didn't bother with TrivialFrontEnd here, as this module, rather than an
        // injector, was going to be doing the actual work.
        FrontEnd.Module frontEnd = FrontEnd.Module.create();

        // Coordinate the necessary bindings between the modules.
        final Application.Binding appBinding = appModule.createBinding();
        final Interpreter.Binding interpreterBinding = interpreterModule.getBinding(appBinding);

        // Create the application
        Main theApp = frontEnd.createApp(interpreterBinding);

        // And run it.
        theApp.run();
    }
}
