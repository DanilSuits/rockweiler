/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.repository;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TrivialPlayerRepository implements PlayerRepository<String> {
    public boolean isAvailable(String player) {
        return true;
    }
}
