/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.persistence.memory;

import com.vocumsineratio.eventstore.Exceptions;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.persistence.async.Tasks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Client implements Tasks.AsyncClient<MemoryStore.WriteStatus, MemoryStore.ReadResult> {

    private Tasks.Client<MemoryStore.WriteStatus, MemoryStore.ReadResult> gateway;

    public Client(Tasks.Client<MemoryStore.WriteStatus, MemoryStore.ReadResult> gateway) {
        this.gateway = gateway;
    }

    @Override
    public void commit(Future<MemoryStore.WriteStatus> task) {
        waitFor(task);
    }

    @Override
    public Iterable<Event> read(Future<MemoryStore.ReadResult> task) {
        MemoryStore.ReadResult result = waitFor(task);
        return result.events;
    }

    @Override
    public Future<MemoryStore.ReadResult> get(StreamId streamId) {
        return gateway.get(streamId);
    }

    @Override
    public Future<MemoryStore.WriteStatus> store(StreamId streamId, ExpectedVersion version, Iterable<? extends Event> events) {
        return gateway.store(streamId, version, events);
    }

    <T> T waitFor(Future<T> task) {
        // TODO: SLA
        try {
            return task.get();
        } catch (InterruptedException e) {
            // OK, so the thread is being cancelled.  From our perspective, that's an undeterminable result
            // (when the task is actually executing on another thread, we have no way of knowing at this point
            // how far it got, so we want to communicate to the caller that a retry is appropriate.

            // First, reset the interrupt flag, so that the future checks can know that we are attempting
            // to shut down
            Thread.currentThread().interrupt();

            throw new Exceptions.TransactionLostException("Task interrupted", e);
        } catch (ExecutionException e) {
            throw new Exceptions.TransactionLostException("Task failed", e);
        }
    }


}
