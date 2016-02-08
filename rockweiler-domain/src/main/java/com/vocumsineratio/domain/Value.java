package com.vocumsineratio.domain;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface Value<T extends Value<T>> {
    boolean isSameValue(T other);
}
