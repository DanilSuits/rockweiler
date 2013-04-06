/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface PlayerScanner {
    boolean hasNext();
    Player next();
}
