/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player;

import com.google.common.base.Predicate;

/**
 * @author Danil Suits (danil@vast.com)
 */
public abstract class Biography {
    public enum BIO implements Player.Bio {
        MISSING;

        public String getName() {
            return "_BIO_MISSING_";
        }

        public String getDob() {
            return "00000000";
        }
    }

    public static final Predicate<Player> HAS_BIO_FILTER = new Predicate<Player>() {

        public boolean apply(Player input) {
            Player.Bio bio = input.getBio();
            if (BIO.MISSING == bio) return false;
            return null != input.getBio();
        }
    };



    private Biography() {}
}
