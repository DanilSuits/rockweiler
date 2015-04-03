/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.processing.store;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Stores {
    static class MapBackedStore<T> implements Store<T> {
        private final Map<String, T> items;

        MapBackedStore(Map<String, T> items) {
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
    }


    public static <T> Store<T> newMapBackedStore() {
        final HashMap<String, T> targetStore = Maps.<String, T>newHashMap();
        return newMapBackedStore(targetStore);
    }

    public static <T> MapBackedStore<T> newMapBackedStore(Map<String, T> targetStore) {
        return new MapBackedStore<T>(targetStore);
    }

    static class AccumulatingMapBackedStore<T> implements Store<T> {
        private final Map<String, T> items;

        AccumulatingMapBackedStore(Map<String, T> items) {
            this.items = items;
        }

        public void insert(String key, T value) {
            items.put(key,value);
        }

        public void delete(String key) {
            if (items.containsKey(key)) {
                items.put(key, null);
            }
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
    }

    public static <T> AccumulatingMapBackedStore<T> newAccumulatingStore(Map<String, T> targetStore) {
        return new AccumulatingMapBackedStore<T>(targetStore);
    }


    private Stores () {}
}
