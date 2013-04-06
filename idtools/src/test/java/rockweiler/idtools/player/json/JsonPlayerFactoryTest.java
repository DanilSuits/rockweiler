/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player.json;

import org.testng.Assert;
import org.testng.annotations.Test;
import rockweiler.idtools.player.IdConflictException;
import rockweiler.idtools.player.Player;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JsonPlayerFactoryTest {
    @Test
    public void testParsePlayer() {
        JsonPlayerFactory factory = new JsonPlayerFactory();
        Player p = factory.toPlayer("{\"years\":{\"2013\":{\"depth\":{\"C\":\"2\"},\"team\":\"tor\"}},\"id\":{\"mlb\":\"111072\"},\"bio\":{\"dob\":\"19710829\",\"name\":\"Henry Blanco\",\"role\":\"hitter\"}}");

        Assert.assertEquals(p.getBio().getName(),"Henry Blanco");
        Assert.assertEquals(p.getBio().getDob(),"19710829");
        Assert.assertEquals(p.getIds().get("mlb"),"111072");
    }

    @Test
    public void testMerge() throws IdConflictException {
        JsonPlayerFactory factory = new JsonPlayerFactory();
        Player p = factory.toPlayer("{\"id\":{\"mlb\":\"111072\"},\"bio\":{\"dob\":\"19710829\",\"name\":\"Henry Blanco\"}}");
        Player q = factory.toPlayer("{\"id\":{\"xyzzy\":\"1234\"},\"bio\":{\"dob\":\"19710829\",\"name\":\"Henry Blanco\"}}");

        p.getIds().merge(q.getIds());
        Assert.assertEquals(p.getIds().get("mlb"),"111072");
        Assert.assertEquals(p.getIds().get("xyzzy"),"1234");
    }
}
