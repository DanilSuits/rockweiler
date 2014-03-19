/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rosters;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.modules.Application;
import rockweiler.repository.PlayerRepository;
import rockweiler.repository.TrivialPlayerRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class RostersApp implements MessageListener<Application.Request> {
    public static class Module implements Application.Module {
        public static Module create() {
            PlayerRepository<String> repository = TrivialPlayerRepository.create(Collections.EMPTY_LIST);


            return new Module(repository);
        }

        PlayerRepository<String> playerRepository;

        public Module(PlayerRepository<String> playerRepository) {
            this.playerRepository = playerRepository;
        }

        public Application.Binding createBinding() {
            return new Application.Binding() {

                public MessageListener<Application.Request> bind(MessageListener<Application.Event> eventListener) {
                    return new RostersApp(Module.this.playerRepository,eventListener);
                }
            };
        }
    }

    static class RosterSlot<T> {
        private final String team;
        private final T player;

        RosterSlot(String team, T player) {
            this.team = team;
            this.player = player;
        }
    }

    private final PlayerRepository<String> playerRepository;
    private final MessageListener<Application.Event> eventListener;

    private final List<RosterSlot<String>> rosterSlots = Lists.newArrayList();
    private final Set<String> selectedPlayers = Sets.newHashSet();
    private Predicate<String> selectedFilter = new Predicate<String> (){

        public boolean apply(java.lang.String input) {
            return selectedPlayers.contains(input);
        }
    };

    public RostersApp(PlayerRepository<String> playerRepository, MessageListener<Application.Event> eventListener) {
        this.playerRepository = playerRepository;
        this.eventListener = eventListener;
    }

    public void onMessage(Application.Request message) {
        System.out.println(message);

        if (Requests.QUIT.equals(message)) {
            dispatch(Events.SHUTDOWN);
        }

        if (Requests.SetTeam.class.isInstance(message)) {
            Requests.SetTeam set = Requests.SetTeam.class.cast(message);
            dispatch(new Events.CurrentTeam(set.team));
        }

        if (Requests.Pick.class.isInstance(message)) {
            final Requests.Pick pick = Requests.Pick.class.cast(message);
            Predicate<String> matchPlayer = new Predicate<String>() {

                public boolean apply(java.lang.String input) {
                    return false;
                }
            } ;
            Iterable<String> match = Iterables.filter(playerRepository.getPlayers(),matchPlayer);
            List<String> players = Lists.newArrayList(match);

            if (players.isEmpty()) {
                dispatch(new Events.NoMatch(pick.player));
            } else {
                if (1 == players.size()) {
                    String player = players.get(0);
                    if (selectedFilter.apply(player)) {
                        dispatch(new Events.AlreadyTaken(player));
                    } else {
                        String team = pick.team;
                        RosterSlot<String> crntSlot = new RosterSlot<String>(team, player);
                        addToRoster(crntSlot);
                    }
                } else {
                    dispatch(new Events.AmbiguousPlayer<String>(pick.player,players));
                }
            }
        }
    }

    void addToRoster(RosterSlot<String> crntSlot) {
        rosterSlots.add(crntSlot);
        selectedPlayers.add(crntSlot.player);
        dispatch(new Events.RosterUpdate<String>(rosterSlots));
    }

    private void dispatch(Application.Event event) {
        eventListener.onMessage(event);
    }
}
