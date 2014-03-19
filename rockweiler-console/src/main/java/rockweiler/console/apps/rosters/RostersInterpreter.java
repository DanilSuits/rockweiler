/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rosters;

import rockweiler.console.core.MessageListener;
import rockweiler.console.core.lifecycle.Shutdown;
import rockweiler.console.core.modules.Application;
import rockweiler.console.core.modules.Interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class RostersInterpreter {
    public static class Module implements Interpreter.Module {
        public static Module create() {
            return new Module(new State());
        }

        private final State state;

        Module(State state) {
            this.state = state;
        }

        public Interpreter.Binding getBinding(final Application.Binding appBinding) {
            return new Interpreter.Binding() {
                public MessageListener<String> bind(Shutdown s, MessageListener<String> responseListener) {
                    EventInterpreter eventInterpreter = new EventInterpreter(s, responseListener, state);
                    return new UserInterpreter(appBinding, eventInterpreter, state);
                }
            };
        }
    }

    public static class State {
        public String currentTeam = null;
    }

    static class EventInterpreter implements MessageListener<Application.Event> {
        private final Shutdown shutdown;
        private final MessageListener<String> display;
        private final State state;

        EventInterpreter(Shutdown shutdown, MessageListener<String> display, State state) {
            this.shutdown = shutdown;
            this.display = display;
            this.state = state;
        }

        public void onMessage(Application.Event message) {
            if (Events.SHUTDOWN.equals(message)) {
                shutdown.shutdown();
            }

            if (message instanceof Events.CurrentTeam) {
                Events.CurrentTeam currentTeam = (Events.CurrentTeam) message;
                this.state.currentTeam = currentTeam.team;

                display.onMessage(currentTeam.team);
            }

            if (message instanceof Events.NoMatch) {
                Events.NoMatch noMatch = (Events.NoMatch) message;
                display.onMessage("Error: " + noMatch.query);
            }
        }
    }

    static final Pattern PARSE_TEAM_REQUEST = Pattern.compile("^team (\\w+)");
    static final Pattern PARSE_PICK_REQUEST = Pattern.compile("^pick (.*)");

    static class UserInterpreter implements MessageListener<String> {
        private final MessageListener<Application.Request> requestMessageListener;
        private final State state;

        UserInterpreter(Application.Binding binding, MessageListener<Application.Event> eventListener, State state) {
            this.requestMessageListener = binding.bind(eventListener);
            this.state = state;
        }

        public void onMessage(String message) {
            Application.Request crnt = new Requests.Comment(message);

            if ("quit".equals(message)) {
                crnt = Requests.QUIT;
            }

            Matcher team = PARSE_TEAM_REQUEST.matcher(message);
            if (team.find()) {
                crnt = new Requests.SetTeam(team.group(1));
            }

            Matcher pick = PARSE_PICK_REQUEST.matcher(message);
            if (pick.find()) {
                crnt = new Requests.Pick(state.currentTeam, pick.group(1));
            }
            requestMessageListener.onMessage(crnt);
        }
    }

}
