/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rank;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.modules.Application;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class RankApp implements MessageListener<Application.Request> {
    public static class Module implements Application.Module {
        public static Module create() {
            PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");

            return create(repository);
        }

        public static Module create(PlayerRepository<Schema.Player> repository) {
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
                    return new RankApp(provisionalFactory, Module.this.playerRepository,eventListener);
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

    public RankApp(ProvisionalFactory provisionalFactory, PlayerRepository<Schema.Player> playerRepository, MessageListener<Application.Event> eventListener) {
        this.provisionalFactory = provisionalFactory;
        this.playerRepository = playerRepository;
        this.eventListener = eventListener;
    }

    public void onMessage(Application.Request message) {
        System.out.println(message);

        if (Requests.QUIT.equals(message)) {
            dispatch(Events.SHUTDOWN);
        }

        if (Requests.AddPlayer.class.isInstance(message)) {
            final Requests.AddPlayer add = Requests.AddPlayer.class.cast(message);
            Schema.Player player = provisionalFactory.create(add.name);
            playerRepository.add(player);
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
}
