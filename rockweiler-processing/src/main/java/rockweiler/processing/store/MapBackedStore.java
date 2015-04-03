/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.processing.store;

import java.util.Map;

/**
* @author Danil Suits (danil@vast.com)
*/
public class MapBackedStore<T> implements Store<T> {
    private final Map<String, T> items;

    public MapBackedStore(Map<String, T> items) {
        this.items = items;
    }

    public void insert(String key, T value) {
        items.put(key,value);
    }

    public void delete(String key) {
        items.put(key,null);
    }

    public T get(String key) {
        return items.get(key);
    }

    public void writeTo(Store<T> target) {
        for(Map.Entry<String, T> entry : items.entrySet()) {
            if (null == entry.getValue()) {
                target.delete(entry.getKey());
            } else {
                target.insert(entry.getKey(), entry.getValue());
            }
        }
    }

    protected boolean contains(String key) {
        return items.containsKey(key);
    }
}
