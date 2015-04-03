/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.draft.board;

import org.testng.annotations.Test;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DraftBoardServiceTest {
    @Test
    public void testSimpleDraft() throws Exception {
        DraftBoardService<String> board = new DraftBoardService<String>();

        board.reset();
        board.addTeam("Tom");
        board.addTeam("Danil");
        board.addTeam("Daniel");

        board.addSlot("Danil");
        board.addSlot("Tom");
        board.addSlot("Daniel");

    }

}
