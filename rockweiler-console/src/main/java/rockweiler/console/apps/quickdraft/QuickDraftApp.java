package rockweiler.console.apps.quickdraft;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import rockweiler.console.apps.rank.ProvisionalFactory;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.modules.Application;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/29/14
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuickDraftApp implements MessageListener<Application.Request> {
    public static class Module implements Application.Module {
        public static Module create( PlayerRepository<Schema.Player> repository) {

            ProvisionalFactory provisionalFactory = new ProvisionalFactory("provisional");
            return new Module(repository, provisionalFactory);
        }

        final PlayerRepository<Schema.Player> playerRepository;
        final ProvisionalFactory provisionalFactory;

        public Module(PlayerRepository<Schema.Player> playerRepository, ProvisionalFactory provisionalFactory) {
            this.playerRepository = playerRepository;
            this.provisionalFactory = provisionalFactory;
        }

        public Application.Binding createBinding() {
            return new Application.Binding() {

                public MessageListener<Application.Request> bind(MessageListener<Application.Event> eventListener) {
                    return new QuickDraftApp(provisionalFactory, Module.this.playerRepository,eventListener);
                }
            };
        }
    }

    private final ProvisionalFactory provisionalFactory;
    private final PlayerRepository<Schema.Player> playerRepository;
    private final MessageListener<Application.Event> eventListener;

    // TODO: need to actually track who has been selected already
    private final List<Schema.Player> draft = Lists.newArrayList();
    private Predicate<Schema.Player> selectedFilter = new Predicate<Schema.Player> (){

        public boolean apply(Schema.Player input) {
            return draft.contains(input);
        }
    };

    public QuickDraftApp(ProvisionalFactory provisionalFactory, PlayerRepository<Schema.Player> playerRepository, MessageListener<Application.Event> eventListener) {
        this.provisionalFactory = provisionalFactory;
        this.playerRepository = playerRepository;
        this.eventListener = eventListener;
    }

    public void onMessage(Application.Request message) {
        if (Requests.QUIT.equals(message)) {
            dispatch(Events.SHUTDOWN);
        }

        if (Requests.AddPlayer.class.isInstance(message)) {
            final Requests.AddPlayer add = Requests.AddPlayer.class.cast(message);
            Schema.Player player = provisionalFactory.create(add.name);
            playerRepository.add(player);
        }

        if (Requests.HidePlayer.class.isInstance(message)) {
            final Requests.HidePlayer hide = Requests.HidePlayer.class.cast(message);

            MatchPlayer match = new MatchPlayer(hide.query);

            Iterable<Schema.Player> resultSet = Iterables.filter(playerRepository.getPlayers(), match);
            List<Schema.Player> players = Lists.newArrayList(resultSet);

            if (players.isEmpty()) {
                dispatch(new Events.NoMatch(hide.query));
            } else {
                if (1 == players.size()) {
                    Schema.Player player = players.get(0);
                    dispatch(new Events.HidePlayer(player));
                } else {
                    dispatch(new Events.AmbiguousPlayer(hide.query,players));
                }
            }
        }

        if (Requests.Watch.class.isInstance(message)) {
            final Requests.Watch watch = Requests.Watch.class.cast(message);
            MatchPlayer match = new MatchPlayer(watch.query);

            Iterable<Schema.Player> resultSet = Iterables.filter(playerRepository.getPlayers(), match);
            List<Schema.Player> players = Lists.newArrayList(resultSet);
            if (players.isEmpty()) {
                dispatch(new Events.NoMatch(watch.query));
            } else {
                if (1 == players.size()) {
                    Schema.Player player = players.get(0);
                    dispatch(new Events.WatchPlayer(player));
                } else {
                    dispatch(new Events.AmbiguousPlayer(watch.query,players));
                }
            }

        }

        if (Requests.View.class.isInstance(message)) {
            final Requests.View view = Requests.View.class.cast(message);
            MatchPlayer match = new MatchPlayer(view.query);
            Iterable<Schema.Player> resultSet = Iterables.filter(playerRepository.getPlayers(), match);
            List<Schema.Player> players = Lists.newArrayList(resultSet);

            dispatch(new Events.AmbiguousPlayer(view.query, players));
        }

        if (Requests.Filter.class.isInstance(message)) {
            final Requests.Filter filter = Requests.Filter.class.cast(message);

            List<Schema.Player> availablePlayers = Lists.newArrayList();
            for(Schema.Player p : filter.players) {
                if (! selectedFilter.apply(p)) {
                    availablePlayers.add(p);
                }
            }

            dispatch(new Events.FilterResult(availablePlayers));
        }

        if (Requests.Pick.class.isInstance(message)) {
            final Requests.Pick pick = Requests.Pick.class.cast(message);

            Predicate<Schema.Player> matchPlayer = new Predicate<Schema.Player>() {
                public boolean apply(Schema.Player input) {
                    if (input.bio.name.contains(pick.query)) {
                        return true;
                    }

                    for(Map.Entry<String,String> id : input.id.entrySet()) {
                        if (id.getValue().contains(pick.query)) {
                            return true;
                        }
                    }

                    return false;
                }
            } ;

            Iterable<Schema.Player> match = Iterables.filter(playerRepository.getPlayers(), matchPlayer);
            List<Schema.Player> players = Lists.newArrayList(match);

            if (players.isEmpty()) {
                dispatch(new Events.NoMatch(pick.query));
            } else {
                if (1 == players.size()) {
                    Schema.Player player = players.get(0);
                    String playerName = player.bio.name;
                    if (selectedFilter.apply(player)) {
                        dispatch(new Events.AlreadyTaken(playerName));
                    } else {
                        select(player);
                        dispatch(new Events.DraftUpdate<Schema.Player>(draft));
                    }
                } else {
                    dispatch(new Events.AmbiguousPlayer(pick.query,players));
                }
            }
        }
    }

    private void select (Schema.Player player) {
        draft.add(player);
    }

    private void dispatch(Application.Event event) {
        eventListener.onMessage(event);
    }

    static class MatchPlayer implements Predicate<Schema.Player> {
        final String query;

        MatchPlayer(String query) {
            this.query = query;
        }

        public boolean apply(rockweiler.player.jackson.Schema.Player input) {
            if (input.bio.name.contains(this.query)) {
                return true;
            }

            for(Map.Entry<String,String> id : input.id.entrySet()) {
                if (id.getValue().contains(this.query)) {
                    return true;
                }
            }

            return false;
        }
    }

}
