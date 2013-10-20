/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler;

import org.testng.Assert;
import org.testng.annotations.Test;
import rockweiler.tools.DraftTool;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class AvailabilityTest {
    @Test
    public void testDraft () {
        DraftTool tool = new DraftTool();
        final String player = "Bob";
        tool.draft(player);
        Assert.assertFalse(tool.isAvailable(player));

    }
}
