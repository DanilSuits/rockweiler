/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class League {
    void on(Map event) {

    }

    public static class State {
        Map<UUID, Roster> rosters = Maps.newHashMap();
        UUID crntTeam = null;

    }

    public static class Roster {
        final UUID rosterId;
        String teamAlias;
        Map<UUID, Slot> slots = Maps.newHashMap();

        Roster(UUID id, String alias) {
            this.rosterId = id;
            this.teamAlias = alias;
        }
    }

    public static class Slot {
        final UUID slotId;
        String playerAlias;

        Slot(UUID id, String alias) {
            this.slotId = id;
            this.playerAlias = alias;
        }

    }
}
