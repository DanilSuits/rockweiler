/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.cqrs;

import com.vocumsineratio.domain.Value;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class EventName implements Value<EventName> {
    public final String name;

    public EventName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean isSameValue(EventName other) {
        return name.equals(other.name);
    }
}
