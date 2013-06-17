package rockweiler.idtools; /**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */

import com.beust.jcommander.internal.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;
import rockweiler.player.Player;
import rockweiler.player.jackson.JsonPlayerFactory;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class IdentityMergeTest {
    JsonPlayerFactory factory = new JsonPlayerFactory();

    @Test
    public void simpleMerge() {
        Player rhs = factory.toPlayer("{\"id\":{\"xyzzy\":\"1234\"},\"bio\":{\"dob\":\"19710829\",\"name\":\"Henry Blanco\"}}");

        Player mergeResult = getMergedPlayer(rhs);
        Assert.assertEquals(mergeResult.getIds().get("xyzzy"), "1234");
    }

    @Test
    public void rejectDOB() {
        Player rhs = factory.toPlayer("{\"id\":{\"xyzzy\":\"1234\"},\"bio\":{\"dob\":\"19710828\",\"name\":\"Henry Blanco\"}}");
        verifyNoMerge(rhs);
    }

    private void verifyNoMerge(Player rhs) {
        Player mergeResult = getMergedPlayer(rhs);

        Assert.assertNull(mergeResult.getIds().get("xyzzy"));
        Player missing = getMissingPlayer(rhs);
        Assert.assertEquals(missing, rhs);
    }

    @Test
    public void rejectName() {
        Player rhs = factory.toPlayer("{\"id\":{\"xyzzy\":\"1234\"},\"bio\":{\"dob\":\"19710828\",\"name\":\"Henry Blonca\"}}");
        verifyNoMerge(rhs);
    }

    private Player getMergedPlayer(Player rhs) {
        IdentityMerge theMerge = doMerge(rhs);

        TrivialPlayerCollector collector = new TrivialPlayerCollector();
        for(Player crnt : theMerge.collectMissingDatabase()) {
             collector.collect(crnt);
         }
        return collector.found;
    }

    private Player getMissingPlayer(Player rhs) {
        IdentityMerge theMerge = doMerge(rhs);
        TrivialPlayerCollector collector = new TrivialPlayerCollector();
        for(Player crnt : theMerge.collectMissingDatabase()) {
            collector.collect(crnt);
        }
        return collector.found;
    }

    private static class TrivialPlayerCollector {
        Player found = null ;

        public void collect(Player player) {
            found = player;
        }
    }

    private IdentityMerge doMerge(Player rhs) {
        List<Player> mergeDB = Lists.newArrayList();
        mergeDB.add(rhs);

        List<Player> originalDB = Lists.newArrayList();
        Player p = factory.toPlayer("{\"id\":{\"mlb\":\"111072\"},\"bio\":{\"dob\":\"19710829\",\"name\":\"Henry Blanco\"}}");
        originalDB.add(p);

        IdentityMerge theMerge = new IdentityMerge(originalDB);

        theMerge.merge(mergeDB);
        return theMerge;
    }
}
