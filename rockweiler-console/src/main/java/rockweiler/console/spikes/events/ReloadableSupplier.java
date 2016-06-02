/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import com.google.common.base.Supplier;

import java.util.concurrent.ExecutorService;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ReloadableSupplier<T> implements Supplier<T> {
    private final Supplier<T> source;
    private volatile T cachedValue;

    public ReloadableSupplier(Supplier<T> source, T cachedValue) {
        this.source = source;
        this.cachedValue = cachedValue;
    }

    public ReloadableSupplier(Supplier<T> source) {
        this(source, source.get());
    }

    public void reload(ExecutorService exec) {
        exec.submit(new Runnable() {
            public void run() {
                T data = source.get();
                cachedValue = data;
            }
        });
    }

    public T get() {
        return cachedValue;
    }
}
