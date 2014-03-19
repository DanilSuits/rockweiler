/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.demo;

import rockweiler.console.core.Main;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.lifecycle.*;
import rockweiler.console.core.modules.Application;
import rockweiler.console.core.modules.FrontEnd;
import rockweiler.console.core.modules.Interpreter;
import rockweiler.modules.binding.BindingFactory;

import java.io.IOException;

/**
 * This is an app that I put together expecting to send it to codereview.stackexchange.com
 *
 * The basic idea here is to explore how the various components (text front end, application
 * back end, command interpreter, and so on) should be arranged in the assembly stage.  It
 * ought to be possible to snap in one for another.
 *
 * Eventually, I found the idea of passing a BindingFactory to a constructor - the constructor
 * can pass the this pointer to the factory, and get the other half of the bind returned to it.
 * Kind of screwy, it seems to be effective.
 *
 * http://code.google.com/p/google-guice/wiki/AssistedInject can detect the situation
 * with the right annotations, but I wanted to do the initial exploration by hand to force
 * myself to understand it.
 *
 * This is a complicated implementation of a toy application: all it does is echo input back
 * to standard out until the user submits "quit".
 *
 * @author Danil Suits (danil@vast.com)
 */
public class ConsoleDemo {

    public static void main(String[] args) throws IOException {

        Application.Module appModule = TrivialApplication.Module.create();
        Interpreter.Module interpreterModule = TrivialInterpreter.Module.create();

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

    // Request that the application shut down
    static final Application.Request REQUEST_QUIT = new Application.Request() {
    };

    // Request that the application broadcast a message
    static class BroadcastRequest implements Application.Request {
        final String message;

        BroadcastRequest(String message) {
            this.message = message;
        }
    }


    static final Application.Event EVENT_SHUTDOWN = new Application.Event() {
    };

    static class BroadcastEvent implements Application.Event {
        final String message;

        BroadcastEvent(String message) {
            this.message = message;
        }
    }

    static class UserInterpreter implements MessageListener<String> {
        private final MessageListener<Application.Request> requestMessageListener;

        UserInterpreter(Application.Binding binding, MessageListener<Application.Event> eventListener) {
            this.requestMessageListener = binding.bind(eventListener);
        }

        public void onMessage(String message) {
            if ("quit".equals(message)) {
                requestMessageListener.onMessage(REQUEST_QUIT);
            } else {
                requestMessageListener.onMessage(new BroadcastRequest(message));
            }
        }
    }

    static class EventInterpreter implements MessageListener<Application.Event> {
        private final Shutdown shutdown;
        private final MessageListener<String> display;

        EventInterpreter(Shutdown shutdown, MessageListener<String> display) {
            this.shutdown = shutdown;
            this.display = display;
        }

        public void onMessage(Application.Event message) {
            if (EVENT_SHUTDOWN.equals(message)) {
                shutdown.shutdown();
            }

            if (message instanceof BroadcastEvent) {
                BroadcastEvent broadcastEvent = (BroadcastEvent) message;
                display.onMessage(broadcastEvent.message);
            }
        }
    }

    static class TrivialInterpreter {
        static class Module implements Interpreter.Module {
            static Module create() {
                return new Module();
            }

            public Interpreter.Binding getBinding(final Application.Binding appBinding) {
                return new Interpreter.Binding() {
                    public MessageListener<String> bind(Shutdown s, MessageListener<String> responseListener) {
                        EventInterpreter eventInterpreter = new EventInterpreter(s, responseListener);
                        return new UserInterpreter(appBinding, eventInterpreter);
                    }
                };
            }
        }
    }

    static class TrivialApplication {
        static class Module implements Application.Module {
            static Application.Module create() {
                return new Module();
            }

            private final Connection connection = Connection.create();

            public Application.Binding createBinding() {
                return new Application.Binding() {

                    public MessageListener<Application.Request> bind(MessageListener<Application.Event> eventListener) {
                        connection.getClient().setListener(eventListener);
                        return connection;
                    }
                };
            }

        }

        static class Connection implements MessageListener<Application.Request> {
            static Connection create() {
                ClientBinding bind = new ClientBinding();
                return new Connection(bind);
            }

            static class ClientBinding implements BindingFactory<Connection, Client> {
                public Client create(Connection lhs) {
                    return new Client(lhs);
                }
            }

            private final Client client;

            Connection(ClientBinding binding) {
                this.client = binding.create(this);
            }

            public Client getClient() {
                return client;
            }

            public void onMessage(Application.Request message) {
                if (REQUEST_QUIT.equals(message)) {
                    client.eventListener.onMessage(EVENT_SHUTDOWN);
                }

                if (message instanceof BroadcastRequest) {
                    BroadcastRequest broadcastRequest = (BroadcastRequest) message;
                    client.eventListener.onMessage(new BroadcastEvent(broadcastRequest.message));
                }
            }
        }

        static class Client implements MessageListener<Application.Request> {
            private final Connection connection;
            private MessageListener<Application.Event> eventListener = null;

            Client(Connection connection) {
                this.connection = connection;
            }

            public void onMessage(Application.Request message) {
                connection.onMessage(message);
            }

            public void setListener(MessageListener<Application.Event> listener) {
                this.eventListener = listener;
            }
        }
    }

}
