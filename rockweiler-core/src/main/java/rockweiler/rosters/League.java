/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.rosters;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class League<T> {
    public List<Team<T>> teams;

    public static class Team<T> {
        public String name;
        public List<T> players;
    }
}
