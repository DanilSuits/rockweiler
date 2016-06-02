/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore.api;

import com.vocumsineratio.domain.Value;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ExpectedVersion implements Value<ExpectedVersion> {
    public final int version;

    private ExpectedVersion(int version) {
        this.version = version;
    }

    public ExpectedVersion next(int size) {
        if (Any == this) {
            throw new IllegalStateException("ExpectedVersion.Any.next()");
        }

        if (0 == size) {
            return this;
        }

        return new ExpectedVersion(version + size);
    }

    public static final ExpectedVersion of (int version) {
        if (version < 0) {
            throw new IllegalArgumentException("Invalid expected expected");
        }
        return new ExpectedVersion(version);
    }

    public static final ExpectedVersion Any = new ExpectedVersion(-2);
    public static final ExpectedVersion NoStream = new ExpectedVersion(-1);


    @Override
    public boolean isSameValue(ExpectedVersion other) {
        return version == other.version;
    }
}
