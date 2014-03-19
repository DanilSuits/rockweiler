/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.core.modules;

import rockweiler.console.apps.demo.ConsoleDemo;
import rockweiler.console.core.MessageListener;

/**
* @author Danil Suits (danil@vast.com)
*/
public class Application {
    interface Request {
    }

    interface Event {
    }

    static interface Module {
        Binding createBinding();
    }

    static interface Binding {
        MessageListener<Request> bind(MessageListener<Event> eventListener);
    }
}
