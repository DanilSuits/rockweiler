/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.repository;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface PlayerRepository<T> {
    boolean isAvailable(T player);
}
