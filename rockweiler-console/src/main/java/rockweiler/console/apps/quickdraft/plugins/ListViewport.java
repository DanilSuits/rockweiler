package rockweiler.console.apps.quickdraft.plugins;

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
    private final ListRepository listRepository;

    private String crntList = "";
    private int maxItems = 10;

    public ListViewport(MessageListener<String> display, ListRepository listRepository) {
        this.display = display;
        this.listRepository = listRepository;
    }

    public void onMessage(Application.Event message) {
        if (Events.FilterResult.class.isInstance(message)) {
            onMessage(Events.FilterResult.class.cast(message));
        }
    }

    public void onMessage(Events.FilterResult filter) {
        List<Schema.Player> availablePlayers = filter.players;

        List<Schema.Player> originalList = listRepository.get(crntList);

        int viewportLimit = maxItems;

        int listPosition = 0;

        for (Schema.Player p : originalList) {
            listPosition++;

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

