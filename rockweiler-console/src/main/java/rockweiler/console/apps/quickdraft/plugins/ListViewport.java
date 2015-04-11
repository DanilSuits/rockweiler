package rockweiler.console.apps.quickdraft.plugins;

import com.google.common.collect.Lists;
import rockweiler.console.apps.quickdraft.Events;
import rockweiler.console.apps.quickdraft.ListRepository;
import rockweiler.console.apps.quickdraft.Requests;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.modules.Application;
import rockweiler.player.jackson.Schema;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/30/14
 * Time: 9:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListViewport implements MessageListener<Application.Event> {
    private final MessageListener<String> display;
    private final List<Schema.Player> hiddenPlayers = Lists.newArrayList();
    private final ListRepository listRepository;
    private final LocalWatchRepository watchRepository;

    private String crntList = "";
    private int maxItems = 10;

    public ListViewport(MessageListener<String> display, ListRepository listRepository, LocalWatchRepository watchRepository) {
        this.display = display;
        this.listRepository = listRepository;
        this.watchRepository = watchRepository;
    }

    public void onMessage(Application.Event message) {
        if (Events.FilterResult.class.isInstance(message)) {
            onMessage(Events.FilterResult.class.cast(message));
        }

        if (Events.HidePlayer.class.isInstance(message)) {
            onMessage(Events.HidePlayer.class.cast(message));
        }

        if (Events.WatchPlayer.class.isInstance(message)) {
            onMessage(Events.WatchPlayer.class.cast(message));
        }
    }

    public void onMessage(Events.HidePlayer hidePlayer) {
        hiddenPlayers.add(hidePlayer.player);
    }

    public void onMessage(Events.WatchPlayer watchPlayer) {
        watchRepository.add(watchPlayer.player);
    }

    public void onMessage(Events.FilterResult filter) {
        List<Schema.Player> availablePlayers = filter.players;

        List<Schema.Player> originalList = listRepository.get(crntList);

        int viewportLimit = maxItems;

        int listPosition = 0;

        for (Schema.Player p : originalList) {
            listPosition++;

            if (hiddenPlayers.contains(p)) {
                continue;
            }

            if (availablePlayers.contains(p)) {
                display.onMessage("Available [" + listPosition + "] " + format(p));
                viewportLimit--;
                if (0 == viewportLimit) {
                    break;
                }
            }
        }
    }
    public Application.Request setLimit (int limit) {
        this.maxItems = limit;
        return new Requests.Comment("List.limit : " + limit);
    }
    public Application.Request show (String key) {
        crntList = key;
        List<Schema.Player> players = listRepository.get(key);
        return new Requests.Filter(players);
    }

    String format(Schema.Player p) {
        String id = "unknown";
        if (p.id.containsKey("mlb")) {
            id = p.id.get("mlb");
        }
        return p.bio.name + " " + id;
    }

}

