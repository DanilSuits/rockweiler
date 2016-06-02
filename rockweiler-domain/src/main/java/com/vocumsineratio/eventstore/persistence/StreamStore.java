package com.vocumsineratio.eventstore.persistence;

import com.vocumsineratio.eventstore.api.StreamId;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface StreamStore extends Store<StreamId, CachedStream> {
}
