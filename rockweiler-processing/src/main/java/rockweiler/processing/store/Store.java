/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.processing.store;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface Store<T> {
    void insert(String key, T value);
    void delete(String key);

    T get(String key);

    void writeTo(Store<T> target);
}
