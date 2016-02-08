/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.model.domain;

import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.cqrs.EventName;
import com.vocumsineratio.domain.ComparisonChain;
import com.vocumsineratio.domain.Id;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class PlayerRanked implements Event.Data<PlayerRanked> {
    public final Id rankingId;
    public final Rank rank;
    public final Hint playerName;

    public PlayerRanked(Id rankingId, Rank rank, Hint playerName) {
        this.rankingId = rankingId;
        this.rank = rank;
        this.playerName = playerName;
    }

    @Override
    public boolean isSameValue(PlayerRanked other) {
        return ComparisonChain
                .start(this,other)
                .compare(rankingId, other.rankingId)
                .compare(rank, other.rank)
                .compare(playerName, other.playerName)
                .end();
    }

    @Override
    public Event<PlayerRanked> create(Id id) {
        return new Event<PlayerRanked>(Constants.EVENT_NAME, id, this);
    }


    private static class Constants {
        static final EventName EVENT_NAME = new EventName("playerRanked");
    }
}
