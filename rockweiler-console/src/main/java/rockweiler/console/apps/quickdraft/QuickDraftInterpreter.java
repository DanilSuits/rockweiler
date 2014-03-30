package rockweiler.console.apps.quickdraft;

import rockweiler.console.apps.rank.*;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.SharedMessageListener;
import rockweiler.console.core.lifecycle.*;
import rockweiler.console.core.modules.Application;
import rockweiler.console.core.modules.Interpreter;
import rockweiler.player.jackson.Schema;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/29/14
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuickDraftInterpreter {
    public static class Module implements Interpreter.Module {
        public static Module create(Replay replay) {
            TempReportFactory reportFactory = new TempReportFactory();
            Archive archive = new Archive(reportFactory);

            return new Module(archive, replay);
        }

        public Module(Archive archive, Replay replay) {
            this.archive = archive;
            this.replay = replay;
        }

        private final Archive archive;
        private final Replay replay;

        public Interpreter.Binding getBinding(final Application.Binding appBinding) {
            return new Interpreter.Binding() {
                public MessageListener<String> bind(Shutdown s, MessageListener<String> responseListener) {
                    EventInterpreter eventInterpreter = new EventInterpreter(s, responseListener, archive);

                    MessageListener<String> userListener = new UserInterpreter(appBinding, eventInterpreter);

                    SharedMessageListener<String> rootListener = new SharedMessageListener<String>();
                    rootListener.add(userListener);
                    rootListener.add(replay.getLog());

                    return rootListener;
                }
            };
        }
    }

    static class EventInterpreter implements MessageListener<Application.Event> {
        private final Shutdown shutdown;
        private final MessageListener<String> display;
        private final Archive archive;


        EventInterpreter(Shutdown shutdown, MessageListener<String> display, Archive archive) {
            this.shutdown = shutdown;
            this.display = display;
            this.archive = archive;
        }

        public void onMessage(Application.Event message) {
            if (Events.SHUTDOWN.equals(message)) {
                shutdown.shutdown();
            }

            if (Events.NoMatch.class.isInstance(message)) {
                Events.NoMatch error = Events.NoMatch.class.cast(message);
                display.onMessage("NoMatch: " + error.query);
            }

            if (Events.AlreadyTaken.class.isInstance(message)) {
                Events.AlreadyTaken error = Events.AlreadyTaken.class.cast(message);
                display.onMessage("AlreadyTaken: " + error.query);
            }

            if (Events.AmbiguousPlayer.class.isInstance(message)) {
                Events.AmbiguousPlayer<Schema.Player> error = Events.AmbiguousPlayer.class.cast(message);
                display.onMessage("AmbiguousPlayer: ");
                for(Schema.Player crnt : error.players) {

                    String id = "unknown";
                    if (crnt.id.containsKey("mlb")) {
                        id = crnt.id.get("mlb");
                    }
                    display.onMessage(" " + crnt.bio.name + " " + id );
                }
            }

            if (Events.DraftUpdate.class.isInstance(message) ) {
                Events.DraftUpdate<Schema.Player> update = Events.DraftUpdate.class.cast(message);
                archive.save(update.slots);

                // TODO
                String lastPlayer = "Draft: ";
                for(Schema.Player player : update.slots) {

                    lastPlayer = "Draft: " + player.bio.name;
                }
                display.onMessage(lastPlayer);

            }
        }
    }

    static final Pattern PARSE_KEEP_REQUEST = Pattern.compile("^k(eep)? (.*)");
    static final Pattern PARSE_PICK_REQUEST = Pattern.compile("^p(ick)? (.*)");
    static final Pattern PARSE_DRAFT_REQUEST = Pattern.compile("draft (.*)");
    static final Pattern PARSE_ADD_REQUEST = Pattern.compile("add (.*)");

    static final Pattern PARSE_LIST_SHOW = Pattern.compile("list (.*)");

    static class UserInterpreter implements MessageListener<String> {
        private final MessageListener<Application.Request> requestMessageListener;

        UserInterpreter(Application.Binding binding, MessageListener<Application.Event> eventListener) {
            this.requestMessageListener = binding.bind(eventListener);
        }

        public void onMessage(String message) {
            Application.Request crnt = new Requests.Comment(message);

            if ("quit".equals(message)) {
                crnt = Requests.QUIT;
            }

            Matcher pick = PARSE_PICK_REQUEST.matcher(message);
            if (pick.find()) {
                crnt = new Requests.Pick(pick.group(2));
            }

            Matcher draft = PARSE_DRAFT_REQUEST.matcher(message);
            if (draft.find()) {
                crnt = new Requests.Pick(draft.group(1));
            }

            Matcher keep = PARSE_KEEP_REQUEST.matcher(message);
            if (keep.find()) {
                crnt = new Requests.Pick(keep.group(2));
            }

            Matcher add = PARSE_ADD_REQUEST.matcher(message);
            if (add.find()) {
                crnt = new Requests.AddPlayer(add.group(1));
            }

            Matcher listShow = PARSE_LIST_SHOW.matcher(message);
            if(listShow.find()) {
                // TODO
            }

            requestMessageListener.onMessage(crnt);
        }
    }

}
