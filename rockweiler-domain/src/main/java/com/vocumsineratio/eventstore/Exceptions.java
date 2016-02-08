/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.eventstore;

import com.vocumsineratio.eventstore.api.ExpectedVersion;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Exceptions {
    public static class TransactionLostException extends RuntimeException {
        public TransactionLostException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class WrongVersionException extends RuntimeException {
        private final ExpectedVersion actual;
        private final ExpectedVersion expected;

        public WrongVersionException(ExpectedVersion actual, ExpectedVersion expected) {
            this.actual = actual;
            this.expected = expected;
        }

        @Override
        public String getMessage() {
            return (new StringBuilder("actual: "))
                    .append(this.actual.version)
                    .append(" ; expected: ")
                    .append(this.expected.version)
                    .toString();
        }
    }
}
