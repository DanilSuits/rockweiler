/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.core;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface MessageListener<T> {
    void onMessage(T message);
}
