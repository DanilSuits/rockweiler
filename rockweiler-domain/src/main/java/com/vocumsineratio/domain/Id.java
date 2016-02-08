/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.domain;

import java.util.UUID;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Id<T> implements Value<Id<T>> {
    public final UUID uuid;

    public Id(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean isSameValue(Id<T> other) {
        return uuid.equals(other.uuid);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }

    public static <T> Id<T> create () {
        return new Id<T>(UUID.randomUUID());
    }

    public static <T> Id<T> from(String id) {
        UUID uuid = UUID.fromString(id);
        return new Id<T>(uuid);
    }
}
