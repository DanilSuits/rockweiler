/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.draft.board;

import com.google.common.collect.Lists;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DraftBoardService<P> {
    private DraftBoard<P> board;

    public void reset() {
        this.board = new DraftBoard<P>();
    }

    public void addTeam(String id) throws DuplicateOwnerException {
        DraftBoard.Owner owner = new DraftBoard.Owner();
        owner.id = id;
        addTeam(owner);
    }

    public void addSlot(String id) throws UnrecognizedOwnerException {
        DraftBoard.Team<P> team = getTeam(id);
        if (null == team ) {
            throw new UnrecognizedOwnerException(id);
        }

        addSlot(team.owner);
    }


    void addTeam(DraftBoard.Owner owner) throws DuplicateOwnerException {
        DraftBoard.Team<P> team = getTeam(owner);

        if (null != team) {
            throw new DuplicateOwnerException(owner);
        }

        team = new DraftBoard.Team<P>();
        team.owner = owner;
        team.players = Lists.newArrayList();

        board.teams.add(team);
    }

    void addSlot(DraftBoard.Owner owner) throws UnrecognizedOwnerException {
        addSlot(Integer.toString(board.slots.size()), owner);
    }

    void addSlot(String id, DraftBoard.Owner owner) throws UnrecognizedOwnerException {
        if (null == getTeam(owner)) {
            throw new UnrecognizedOwnerException(owner);
        }

        DraftBoard.Slot<P> slot = new DraftBoard.Slot<P>();
        slot.state = DraftBoard.Slot.CoreStates.PENDING;
        slot.id = id;
        slot.owner = owner;
        slot.pick = null;

        board.slots.add(slot);
    }

    DraftBoard.Team<P> getTeam(String id) {
        for(DraftBoard.Team<P> team : board.teams) {
            if (team.owner.id.equals(id)) {
                return team;
            }
        }
        return null;
    }

    DraftBoard.Team<P> getTeam(DraftBoard.Owner owner) {
        for(DraftBoard.Team<P> team : board.teams) {
            if (team.owner.equals(owner)) {
                return team;
            }
        }

        return null;
    }

    static class UnrecognizedOwnerException extends Exception {
        public UnrecognizedOwnerException(DraftBoard.Owner owner) {
            this(owner.id);
        }

        public UnrecognizedOwnerException(String id) {
            super(id);
        }
    }

    static class DuplicateOwnerException extends Exception {
        public DuplicateOwnerException(DraftBoard.Owner owner) {
            super(owner.id);
        }
    }
}
