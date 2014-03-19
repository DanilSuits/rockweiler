/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.repository;

import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class CoreRepositoryTest {
    @Test
    public void testAddPlayer () {
        List<String> knownPlayers = Lists.newArrayList();
        List<String> provisionalPlayers = Lists.newArrayList();

        PlayerRepository<String> repo = new CoreRepository<String>(knownPlayers,provisionalPlayers);

        verifyPlayerCount(repo,0);
        repo.add("Bob");
        verifyPlayerCount(repo,1);

        List<String> allPlayers = Lists.newArrayList(repo.getPlayers());
        Assert.assertEquals(allPlayers.get(0), "Bob");

    }

    private <T> void verifyPlayerCount(PlayerRepository<T> repo, int expectedCount) {
        int count = 0;
        for(T player : repo.getPlayers()) {
            count++;
        }
        Assert.assertEquals(count, expectedCount, "wrong number of players in database");
    }
}
