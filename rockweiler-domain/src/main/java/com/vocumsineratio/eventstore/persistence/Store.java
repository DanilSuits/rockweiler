/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface Store<K,V> {
    V read(K key);
    void store(K key, V value);
}
