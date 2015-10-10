/**
 * Copyright Vast 2015. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.player;

import rockweiler.domain.glossary.AggregateRoot;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Player implements AggregateRoot<Player> {
    private final PlayerId id;

    public Player(PlayerId id) {
        this.id = id;
    }

    public boolean sameEntityAs(Player rhs) {
        return false;  //TODO: To change body of implemented methods use File | Settings | File Templates.
    }
}
