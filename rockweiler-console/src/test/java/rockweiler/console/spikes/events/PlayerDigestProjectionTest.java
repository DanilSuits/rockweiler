/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import com.google.common.base.Supplier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class PlayerDigestProjectionTest {
    @Test
    public void testQuery () {
        final PlayerDigestProjection.Factory factory = new PlayerDigestProjection.Factory();

        Supplier<? extends Iterable<PlayerDigestProjection.Digest>> supplier = new Supplier<Iterable<PlayerDigestProjection.Digest>>() {
            public Iterable<PlayerDigestProjection.Digest> get() {
                InputStream in = getClass().getResourceAsStream("players.sample");
                junit.framework.Assert.assertNotNull(in);
                Scanner scanner = new Scanner(in);

                try {
                    return factory.parse(scanner);
                } finally {
                    scanner.close();
                }
            }
        };

        PlayerDigestProjection projection = new PlayerDigestProjection(supplier);
        List players = projection.query("kersh");
        Assert.assertEquals(players.size(), 1);
        Assert.assertEquals(players.get(0).toString(), "kershcl01 19880319 Clayton Kershaw");
    }
}
