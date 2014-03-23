/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.jackson;

import com.google.common.base.Function;
import rockweiler.player.IdConflictException;

import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Schema {
    public static class Player {
        public Map<String, String> id;
        public Bio bio;
    }

    public static class Bio {
        public String name;
        public String dob;
    }

    public static final Function<Player,rockweiler.player.Player> TRANSFORM = new Function<Schema.Player, rockweiler.player.Player>() {
        public rockweiler.player.Player apply(final rockweiler.player.jackson.Schema.Player input) {
            if (null == input.id) {
                System.err.println(input);
            }

            final rockweiler.player.Player.Ids ids = new rockweiler.player.Player.Ids() {
                public void add(String key, String value) {
                    input.id.put(key,value);
                }

                public void merge(rockweiler.player.Player.Ids rhs) throws IdConflictException {
                    for(String key : rhs.all()) {
                        input.id.put(key, rhs.get(key));
                    }
                }

                public String get(String key) {
                    return input.id.get(key);
                }

                public int count() {
                    return input.id.entrySet().size();
                }

                public Iterable<String> all() {
                    return input.id.keySet();
                }
            };

            final rockweiler.player.Player.Bio bio = new rockweiler.player.Player.Bio() {
                public String getName() {
                    return input.bio.name;
                }

                public String getDob() {
                    return input.bio.dob;
                }
            };

            return new rockweiler.player.Player() {

                public Ids getIds() {
                    return ids;
                }

                public Bio getBio() {
                    return bio;
                }
            } ;
        }
    } ;

}
