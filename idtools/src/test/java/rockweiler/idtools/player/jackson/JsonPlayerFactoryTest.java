/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player.jackson;

import org.testng.Assert;
import org.testng.annotations.Test;
import rockweiler.idtools.DatabaseFactory;
import rockweiler.idtools.DatabaseWriter;
import rockweiler.idtools.player.IdConflictException;
import rockweiler.idtools.player.Player;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    @Test
    public void testConflict() {
        JsonPlayerFactory factory = new JsonPlayerFactory();
        Player p = factory.toPlayer("{\"id\":{\"xyzzy\":\"111072\"},\"bio\":{\"dob\":\"19710829\",\"name\":\"Henry Blanco\"}}");
        Player q = factory.toPlayer("{\"id\":{\"xyzzy\":\"1234\"},\"bio\":{\"dob\":\"19710829\",\"name\":\"Henry Blanco\"}}");

        try {
            p.getIds().merge(q.getIds());
            Assert.fail("Expected IdConflictException");
        } catch (IdConflictException e) {
            // Pass
        }


    }

    @Test
    public void testWrite() {
        JsonPlayerFactory factory = new JsonPlayerFactory();
        Player p = factory.toPlayer("{\"id\":{\"mlb\":\"111072\"},\"bio\":{\"dob\":\"19710829\",\"name\":\"Henry Blanco\"}}");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            DatabaseWriter writer = DatabaseFactory.createWriter(out);
            writer.collector().collect(p);
            System.out.println(out.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
