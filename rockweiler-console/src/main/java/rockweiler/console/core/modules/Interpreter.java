/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.core.modules;

import rockweiler.console.apps.demo.ConsoleDemo;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.lifecycle.*;

/**
* @author Danil Suits (danil@vast.com)
*/
public class Interpreter {
    static interface Module {
        Binding getBinding(ConsoleDemo.Application.Binding appBinding);
    }

    static interface Binding {
        MessageListener<String> bind(Shutdown s, MessageListener<String> responseListener);
    }
}
