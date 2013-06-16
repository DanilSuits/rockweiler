/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player;

import org.testng.Assert;
import org.testng.annotations.Test;
import rockweiler.idtools.BioSimilarity;
import rockweiler.idtools.player.json.JsonPlayerFactory;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class BioSimilarityTest {
    @Test
    public void testBioSimilarity () {
        JsonPlayerFactory factory = new JsonPlayerFactory();

        Player lhs = factory.toPlayer("{\"id\":{\"espn\":\"5880\",\"bbref\":\"riosal01\",\"rotoworld\":\"3793\",\"mlb\":\"425567\",\"yahoo\":\"7254\"},\"bio\":{\"dob\":\"19810218\",\"name\":\"Alex Rios\",\"role\":\"hitter\"}}");
        Player rhs = factory.toPlayer("{\"id\":{\"lahman\":\"riosal01\"},\"bio\":{\"name\":\"Alexis Rios\",\"dob\":\"19810218\"}}");

        BioReader idReader = new BioReader();

        Assert.assertEquals(idReader.getId(lhs),"19810218Alex Rios");
        Assert.assertEquals(idReader.getId(rhs),"19810218Alexis Rios");

        BioSimilarity similarity = new BioSimilarity(idReader);
        Assert.assertEquals(similarity.compare(lhs,rhs),2);
    }
}
