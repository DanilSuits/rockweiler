/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.waivers;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.modules.Application;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.PlayerRepository;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class WaiversInterpreter {
    static class UserInterpreter implements MessageListener<String> {
        public static UserInterpreter create(MessageListener<Application.Event> eventListener, PlayerRepository<Schema.Player> repository, final MessageListener<Application.Request> requestListener) {
            Map<Pattern, MatchHandler> actions = Maps.newHashMap();

            UniquePlayerMatcher.Factory factory = new UniquePlayerMatcher.Factory(repository, eventListener);

            UniquePlayerMatcher claimPlayer = factory.create(new MessageListener<Schema.Player>() {
                public void onMessage(Schema.Player player) {
                    Requests.PickPlayer request = new Requests.PickPlayer();
                    request.pickPlayer = player;
                    requestListener.onMessage(request);
                }
            } );

            actions.put(Pattern.compile("^k(eep)? (.*)"), claimPlayer);
            actions.put(Pattern.compile("^o(wner)? (.*)"), NO_OP);
            actions.put(Pattern.compile("^p(ick)? (.*)"), claimPlayer);
            actions.put(Pattern.compile("^c(laim)? (.*)"), claimPlayer);

            UniquePlayerMatcher waivePlayer = factory.create(new MessageListener<Schema.Player>() {
                public void onMessage(Schema.Player player) {
                    Requests.WaivePlayer request = new Requests.WaivePlayer();
                    request.waivePlayer = player;
                    requestListener.onMessage(request);
                }
            });

            actions.put(Pattern.compile("^w(aive)? (.*)"), waivePlayer);
            // Comment
            actions.put(Pattern.compile("^#"), NO_OP);

            return new UserInterpreter(eventListener, actions);
        }

        private final Map<Pattern, MatchHandler> actions;
        private final MessageListener<Application.Event> eventListener;

        UserInterpreter(MessageListener<Application.Event> eventListener, Map<Pattern, MatchHandler> actions) {
            this.eventListener = eventListener;
            this.actions = actions;
        }

        public void onMessage(String message) {
            if (message.isEmpty()) {
                return;
            }

            for (Map.Entry<Pattern, MatchHandler> entry : actions.entrySet()) {
                Pattern pattern = entry.getKey();
                Matcher m = pattern.matcher(message);
                if (m.find()) {
                    MatchHandler action = entry.getValue();
                    action.onMatch(m);
                    return;
                }
            }
            eventListener.onMessage(new Events.UnknownCommand(message));
        }
    }

    interface MatchHandler {
        void onMatch(Matcher m);
    }

    private static final MatchHandler NO_OP = new MatchHandler() {
        public void onMatch(Matcher m) {
            // NoOp
        }
    };

    static class UniquePlayerMatcher implements MatchHandler {
        static final class Factory {
            private final PlayerRepository<Schema.Player> localRepository;
            private final MessageListener<Application.Event> eventListener;

            Factory(PlayerRepository<Schema.Player> localRepository, MessageListener<Application.Event> eventListener) {
                this.localRepository = localRepository;
                this.eventListener = eventListener;
            }

            UniquePlayerMatcher create(MessageListener<Schema.Player> action) {
                return new UniquePlayerMatcher(localRepository,eventListener,action);
            }
        }
        private final PlayerRepository<Schema.Player> localRepository;
        private final MessageListener<Application.Event> eventListener;
        private final MessageListener<Schema.Player> action;

        UniquePlayerMatcher(PlayerRepository<Schema.Player> localRepository, MessageListener<Application.Event> eventListener, MessageListener<Schema.Player> action) {
            this.localRepository = localRepository;
            this.eventListener = eventListener;
            this.action = action;
        }


        public void onMatch(Matcher m) {
            final String query = getQuery(m);

            Predicate<Schema.Player> matchPlayer = new Predicate<Schema.Player>() {
                public boolean apply(Schema.Player input) {
                    if (input.bio.name.contains(query)) {
                        return true;
                    }

                    for (Map.Entry<String, String> id : input.id.entrySet()) {
                        if (id.getValue().contains(query)) {
                            return true;
                        }
                    }

                    return false;
                }
            };

            Iterable<Schema.Player> match = Iterables.filter(localRepository.getPlayers(), matchPlayer);
            onQuery(query, match);
        }

        private void onQuery(String query, Iterable<Schema.Player> match) {
            List<Schema.Player> players = Lists.newArrayList(match);

            if (players.isEmpty()) {
                Events.NoMatch event = new Events.NoMatch(query);
                eventListener.onMessage(event);

            } else {
                if (1 != players.size()) {
                    Events.AmbiguousPlayer<Schema.Player> event = new Events.AmbiguousPlayer<Schema.Player>(query,players);
                    eventListener.onMessage(event);
                } else {
                    action.onMessage(players.get(0));
                }
            }
        }

        Pattern ID_QUERY = Pattern.compile("([a-z]+):(\\S+)");

        private String getQuery(Matcher m) {
            String rawQuery = m.group(2);

            Matcher idMatch = ID_QUERY.matcher(rawQuery);
            if (idMatch.find()) {
                System.err.println(idMatch.group(2));

                return idMatch.group(2);
            }

            return rawQuery;
        }
    }

}
