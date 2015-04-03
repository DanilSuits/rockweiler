/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.draft.board;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DraftBoard<P> {
    public static class Team<P> {
        public Owner owner;
        public List<P> players = Lists.newArrayList();
    }

    public static class Owner {
        public String id;

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (null == obj) return false;

            if (obj instanceof Owner) {
                Owner rhs = (Owner)obj;
                return this.id.equals(rhs.id);
            }

            return false;
        }
    }

    public static class Slot<P> {
        interface State {};

        public enum CoreStates implements State {
            PENDING,SKIPPED,USED
        }

        // What's the current state of this specific slot?  For
        // presentation purposes, we need to distinguish various
        // "empty" states
        public State state;

        public String id;

        public Owner owner;

        public P pick;

        public List<String> comments = Lists.newArrayList();

    }

    public List<Team<P>> teams = Lists.newArrayList();
    public List<Slot<P>> slots = Lists.newArrayList();
}
