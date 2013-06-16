/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player;

import com.google.common.base.Predicate;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Predicates {
    public static final Predicate<Player> HAS_BIO = new Predicate<Player>() {

        public boolean apply(Player input) {
            return null != input.getBio();
        }
    };
}
