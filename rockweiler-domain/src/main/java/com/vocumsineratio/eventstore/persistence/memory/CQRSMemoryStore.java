/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory;

import java.util.concurrent.ExecutorService;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class CQRSMemoryStore<Model> {
    private final ExecutorService executorService;
    private final Handle<Model> store;
    private volatile Model readModel;

    public CQRSMemoryStore(ExecutorService executorService, Handle<Model> store) {
        this(executorService, store, store.get());
    }

    private CQRSMemoryStore(ExecutorService executorService, Handle<Model> store, Model readModel) {
        this.executorService = executorService;
        this.store = store;
        this.readModel = readModel;
    }



}
