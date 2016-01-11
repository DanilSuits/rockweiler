/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ScratchRanking {
    @JsonProperty
    public String historyId;

    public ScratchRanking(String historyId) {
        this.historyId = historyId;
    }

    public String getHistoryId() {
        return historyId;
    }
}
