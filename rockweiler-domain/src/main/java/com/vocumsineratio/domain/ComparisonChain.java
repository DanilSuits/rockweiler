/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.domain;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ComparisonChain {

    public static <T extends Value<T>> State start (T lhs, T rhs) {
        return START.compare(lhs,rhs);
    }

    public interface State {
        <T extends Value<T>> State compare(T lhs, T rhs);

        boolean end();
    }

    private static final Terminal SAME = new Terminal(true);
    private static final Terminal DIFFERENT = new Terminal(false);

    private static final State EVALUATING = new Evaluating(SAME) {
        @Override
        public <T extends Value<T>> State compare(T lhs, T rhs) {

            if (lhs.isSameValue(rhs)) {
                return this;
            }

            return DIFFERENT;
        }
    };

    private static final State UNKNOWN = new Evaluating(DIFFERENT) {
        @Override
        public <T extends Value<T>> State compare(T lhs, T rhs) {
            return EVALUATING.compare(lhs,rhs);
        }
    };

    private static final State START = new State() {

        @Override
        public <T extends Value<T>> State compare(T lhs, T rhs) {
            if (lhs == rhs) {
                return SAME;
            }
            return UNKNOWN;
        }

        @Override
        public boolean end() {
            throw new IllegalStateException("No comparisons attempted");
        }
    };

    private static final class Terminal implements State {
        private final boolean result;

        private Terminal(boolean result) {
            this.result = result;
        }

        @Override
        public <T extends Value<T>> State compare(T lhs, T rhs) {
            return this;
        }

        @Override
        public boolean end() {
            return result;
        }
    }

    private static abstract class Evaluating implements State {
        private final Terminal terminal;

        protected Evaluating(Terminal terminal) {
            this.terminal = terminal;
        }

        @Override
        public final boolean end() {
            return terminal.end();
        }
    }

    public static class Equals<T extends Value<T>> {
        private final Class<T> clazz;

        public Equals(Class<T> clazz) {
            this.clazz = clazz;
        }

        public State compare(T lhs, Object obj) {
            if (lhs == obj) {
                return SAME;
            }
            T rhs = clazz.cast(obj);
            if (null == rhs) {
                return DIFFERENT;
            }

            return EVALUATING.compare(lhs, rhs);

        }
    }
}
