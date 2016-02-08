package com.vocumsineratio.eventstore;

import java.util.function.Supplier;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface EventStoreConnection extends Supplier<EventStore> {
}
