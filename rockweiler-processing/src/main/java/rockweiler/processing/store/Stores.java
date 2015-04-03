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

    static class DeltaStore<T> extends MapBackedStore<T> {
        private final Store<T> baseline;

        public DeltaStore(Map<String, T> items, Store<T> baseline) {
            super(items);
            this.baseline = baseline;
        }

        public T get(String key) {
            if (contains(key)) {
                return super.get(key);
            }

            return baseline.get(key);
        }
    }

    public static <T> DeltaStore<T> newDeltaStore(Store<T> baseline) {
        final HashMap<String, T> changes = Maps.<String, T>newHashMap();
        return newDeltaStore(baseline, changes);
    }

    static <T> DeltaStore<T> newDeltaStore(Store<T> baseline, HashMap<String, T> changes) {
        return new DeltaStore<T>(changes, baseline);
    }


    private Stores () {}
}
