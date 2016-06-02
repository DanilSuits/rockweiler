/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import com.google.common.base.Supplier;
import com.google.common.util.concurrent.MoreExecutors;
import junit.framework.Assert;
import org.testng.annotations.Test;
import rockweiler.console.spikes.events.PlayerDictionaryFactory;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class PlayerDictionaryTest {
    @Test
    public void testLoadDictionary () {
        InputStream in = getClass().getResourceAsStream("players.sample");
        Assert.assertNotNull(in);
        Scanner scanner = new Scanner(in);
        PlayerDictionaryFactory factory = new PlayerDictionaryFactory();
        Map<String,Map<String,String>> players = factory.parse(scanner);
    }

    @Test
    public void testSimpleQuery () {
        Supplier<Map<String,Map<String,String>>> source = new Supplier<Map<String, Map<String, String>>>() {
            public Map<String, Map<String, String>> get() {
                InputStream in = getClass().getResourceAsStream("players.sample");
                Assert.assertNotNull(in);
                Scanner scanner = new Scanner(in);
                PlayerDictionaryFactory factory = new PlayerDictionaryFactory();
                Map<String,Map<String,String>> players = factory.parse(scanner);

                return players;
            }
        } ;

        ProjectedPlayerPool pool = new ProjectedPlayerPool(source);
        List<Map<String,String>> results = pool.query("kershcl01");

        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testReload () {
        Supplier<Map<String,Map<String,String>>> source = new Supplier<Map<String, Map<String, String>>>() {
            public Map<String, Map<String, String>> get() {
                InputStream in = getClass().getResourceAsStream("players.sample");
                Assert.assertNotNull(in);
                Scanner scanner = new Scanner(in);
                PlayerDictionaryFactory factory = new PlayerDictionaryFactory();
                Map<String,Map<String,String>> players = factory.parse(scanner);

                return players;
            }
        } ;

        ReloadableSupplier<Map<String,Map<String,String>>> reloadableSupplier = new ReloadableSupplier<Map<String, Map<String, String>>>(source, Collections.EMPTY_MAP);

        ProjectedPlayerPool pool = new ProjectedPlayerPool(reloadableSupplier);
        List<Map<String,String>> results = pool.query("kershcl01");

        Assert.assertTrue(results.isEmpty());

        reloadableSupplier.reload(MoreExecutors.newDirectExecutorService());
        results = pool.query("kershcl01");

        Assert.assertEquals(results.size(), 1);


    }
}
