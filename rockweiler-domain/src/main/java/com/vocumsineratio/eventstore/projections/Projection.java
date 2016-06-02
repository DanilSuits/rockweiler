/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.projections;

import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.api.StreamId;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface Projection {
    Iterable<? extends Event> fromAll();
    Iterable<? extends Event> from(StreamId ... ids);
}
