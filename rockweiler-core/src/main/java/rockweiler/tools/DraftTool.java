/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.tools;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DraftTool {
    private final Set<String> draftedPlayers = Sets.newHashSet();

    public void draft(String player) {
        draftedPlayers.add(player);
    }

    public boolean isAvailable(String player) {
        return ! draftedPlayers.contains(player);
    }
}
