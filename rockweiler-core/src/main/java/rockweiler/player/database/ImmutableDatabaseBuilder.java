/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.database;

import com.google.common.collect.ImmutableCollection;
import rockweiler.player.Player;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ImmutableDatabaseBuilder extends AbstractDatabaseBuilder {
    private final ImmutableCollection.Builder<Player> builder ;

    public ImmutableDatabaseBuilder(ImmutableCollection.Builder<Player> builder) {
        this.builder = builder;
    }

    @Override
    public Iterable<? extends Player> build() {
        return builder.build();
    }

    @Override
    protected void doAdd(Player player) {
        builder.add(player);
    }
}
