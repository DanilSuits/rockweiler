/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.core.modules;

import rockweiler.console.core.MessageListener;

/**
* @author Danil Suits (danil@vast.com)
*/
public class Application {
    public interface Request {
    }

    public interface Event {
    }

    public static interface Module {
        Binding createBinding();
    }

    public static interface Binding {
        MessageListener<Request> bind(MessageListener<Event> eventListener);
    }
}
