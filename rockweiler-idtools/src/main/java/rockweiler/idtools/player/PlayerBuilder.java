/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player;

import com.google.common.collect.Lists;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class PlayerBuilder {
    private Player.Ids currentIds;
    private Player.Bio currentBio;

    public PlayerBuilder withIds(Player.Ids src) {
        currentIds = src;
        return this;
    }

    public PlayerBuilder withBio(Player.Bio src) {
        currentBio = src;
        return this;
    }

    public Player build() {
        final Player.Ids targetIds = new ReadonlyIds(currentIds);
        final Player.Bio targetBio = currentBio;

        return new Player() {
            public Ids getIds() {
                return targetIds;
            }

            public Bio getBio() {
                return targetBio;
            }
        } ;
    }

    private static class ReadonlyIds implements Player.Ids {
        private final Player.Ids target;

        private ReadonlyIds(Player.Ids target) {
            this.target = target;
        }

        public void add(String key, String value) {
            throw new NotImplementedException();
        }

        public void merge(Player.Ids rhs) throws IdConflictException {
            throw new NotImplementedException();
        }

        public String get(String key) {
            return target.get(key);
        }

        public int count() {
            return target.count();
        }

        public Iterable<String> all() {
            return target.all();
        }
    }

}
