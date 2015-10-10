/**
 * Copyright Vast 2015. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.player;

import rockweiler.domain.glossary.Id;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class PlayerId implements Id<PlayerId> {

    public boolean sameValueAs(PlayerId rhs) {
        return false;
    }
}
