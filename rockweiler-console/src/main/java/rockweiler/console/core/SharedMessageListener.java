/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.core;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class SharedMessageListener<T> implements MessageListener<T> {
    final List<MessageListener<T>> listeners = Lists.newArrayList();

    public void add(MessageListener<T> listener) {
        listeners.add(listener);
    }

    public void onMessage(T message) {
        for(MessageListener<T> listener : listeners) {
            listener.onMessage(message);
        }
    }
}
