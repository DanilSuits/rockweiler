/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory;

import com.vocumsineratio.eventstore.api.StreamId;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Handle<T> implements Supplier<T> {
    private volatile T ref;

    public Handle(T ref) {
        this.ref = ref;
    }

    public void commit(T ref) {
        this.ref = ref;
    }

    @Override
    public T get() {
        T ref = this.ref;
        return ref;
    }
}
