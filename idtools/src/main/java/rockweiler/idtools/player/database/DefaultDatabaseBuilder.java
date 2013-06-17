/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player.database;

import rockweiler.idtools.player.Player;

import java.util.Collection;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DefaultDatabaseBuilder extends AbstractDatabaseBuilder {
    private final Collection<Player> target;

    public DefaultDatabaseBuilder(Collection<Player> target) {
        this.target = target;
    }

    @Override
    public Iterable<? extends Player> build() {
        return target;
    }

    @Override
    protected void doAdd(Player player) {
        target.add(player);
    }
}
