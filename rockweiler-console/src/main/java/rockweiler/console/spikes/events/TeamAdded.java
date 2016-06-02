/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import org.apache.commons.lang.StringUtils;

import java.util.UUID;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TeamAdded extends Event<TeamAdded.Data>{
    private TeamAdded(UUID eventId, Data data) {
        super("TeamAdded", eventId, data);
    }

    public static class Data {
        public final UUID teamId;
        public final String alias;

        Data(UUID teamId, String alias) {
            this.teamId = teamId;
            this.alias = alias;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        UUID eventId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        String alias = StringUtils.EMPTY;


        Builder withTeamId(UUID teamId) {
            this.teamId = teamId;
            return this;
        }

        Builder usingAlias(String alias) {
            this.alias = alias;
            return this;
        }

        Builder withEventId(UUID eventId) {
            this.eventId = eventId;
            return this;
        }

        public TeamAdded build () {
            Data data = new Data(teamId, alias);
            return new TeamAdded(eventId, data);
        }

    }
}
