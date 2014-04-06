package rockweiler.console.apps.quickdraft;

import rockweiler.console.apps.quickdraft.plugins.ListViewport;
import rockweiler.console.apps.rank.Archive;
import rockweiler.console.apps.rank.Replay;
import rockweiler.console.apps.rank.TempReportFactory;
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
        public static Module create(Replay replay, ListRepository listRepository, ListViewport listViewport) {
            TempReportFactory reportFactory = new TempReportFactory();
            Archive archive = new Archive(reportFactory);

            return new Module(archive, replay, listRepository, listViewport);
        }

        public Module(Archive archive, Replay replay, ListRepository listRepository, ListViewport listViewport) {
            this.archive = archive;
            this.replay = replay;
            this.listRepository = listRepository;
            this.listViewport = listViewport;
        }

        private final Archive archive;
        private final Replay replay;
        private final ListRepository listRepository;
        private final ListViewport listViewport;

        public Interpreter.Binding getBinding(final Application.Binding appBinding) {
            return new Interpreter.Binding() {
                public MessageListener<String> bind(Shutdown s, MessageListener<String> responseListener) {
                    EventInterpreter eventInterpreter = new EventInterpreter(s, responseListener, archive, listViewport);

                    MessageListener<String> userListener = new UserInterpreter(appBinding, eventInterpreter, listRepository, listViewport);

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
        private final ListViewport listViewport;

        EventInterpreter(Shutdown shutdown, MessageListener<String> display, Archive archive, ListViewport listViewport) {
            this.shutdown = shutdown;
            this.display = display;
            this.archive = archive;
            this.listViewport = listViewport;
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

                    display.onMessage(" " + describePlayer(crnt));
                }
            }

            if (Events.FilterResult.class.isInstance(message)) {
                listViewport.onMessage(message);
            }

            if (Events.HidePlayer.class.isInstance(message)) {
                listViewport.onMessage(message);
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

        String describePlayer(Schema.Player p) {
            String id = "unknown";
            if (p.id.containsKey("mlb")) {
                id = p.id.get("mlb");
            }
            return p.bio.name + " " + id;
        }
    }

    static final Pattern PARSE_KEEP_REQUEST = Pattern.compile("^k(eep)? (.*)");
    static final Pattern PARSE_PICK_REQUEST = Pattern.compile("^p(ick)? (.*)");
    static final Pattern PARSE_DRAFT_REQUEST = Pattern.compile("draft (.*)");
    static final Pattern PARSE_ADD_REQUEST = Pattern.compile("add (.*)");

    static final Pattern PARSE_LIST_SHOW = Pattern.compile("list (.*)");
    static final Pattern PARSE_LIST_SIZE = Pattern.compile("list.size ([0-9]+)");
    static final Pattern PARSE_HIDE_REQUEST = Pattern.compile("list.hide (.*)");

    static class UserInterpreter implements MessageListener<String> {
        private final MessageListener<Application.Request> requestMessageListener;
        private final ListViewport listViewport;

        UserInterpreter(Application.Binding binding, MessageListener<Application.Event> eventListener, ListRepository listRepository, ListViewport listViewport) {
            this.listViewport = listViewport;
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
                crnt = listViewport.show(listShow.group(1));

            }

            Matcher listHide = PARSE_HIDE_REQUEST.matcher(message);
            if (listHide.find()) {
                crnt = new Requests.HidePlayer(listHide.group(1));
            }

            Matcher listSize = PARSE_LIST_SIZE.matcher(message);
            if(listSize.find()) {
                listViewport.setLimit(Integer.parseInt(listSize.group(1)));
            }

            requestMessageListener.onMessage(crnt);
        }
    }

}
