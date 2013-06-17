/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player.csv;

import com.google.common.base.Predicate;
import org.testng.Assert;
import org.testng.annotations.Test;
import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.jackson.JsonPlayerFactory;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class CsvPlayerFactoryTest {
    @Test
    public void testParse() {
        JsonPlayerFactory jsonCore = new JsonPlayerFactory();
        CsvPlayerFactory csvCore = new CsvPlayerFactory(jsonCore);

        String data = "hernafe02,433587,Felix Hernandez,team:SEA pos:SP depth:SEA.SP.1";
        Player result = csvCore.toJsonPlayer(data);

        Assert.assertEquals(result.getBio().getName(), "Felix Hernandez");
        Assert.assertEquals(result.getIds().get("mlb"), "433587");
        Assert.assertEquals(result.getIds().get("lahman"), "hernafe02");

        Predicate<Player> query = csvCore.toSearch(data);
        Assert.assertTrue(query.apply(result));


    }
}
