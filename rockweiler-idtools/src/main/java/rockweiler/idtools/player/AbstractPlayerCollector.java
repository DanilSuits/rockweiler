/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player;

/**
 * @author Danil Suits (danil@vast.com)
 */
public abstract class AbstractPlayerCollector implements PlayerCollector {
    public void collectAll(Iterable<? extends Player> allPlayers) {
        for(Player p : allPlayers) {
            this.collect(p);
        }
    }
}
