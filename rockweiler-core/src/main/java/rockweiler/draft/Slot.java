/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.draft;

import rockweiler.player.Player;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Slot<T> {
    interface State {}

    public enum BasicStates implements State {
        PENDING,SKIPPED,USED
    }

    public static class Owner {}

    public final int pickId;
    public final Owner owner;
    public final State state;
    public final T player;

    public Slot(int pickId, Owner owner, State state, T player) {
        this.pickId = pickId;
        this.owner = owner;
        this.state = state;
        this.player = player;
    }
}
