/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.database;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import rockweiler.player.IdReader;
import rockweiler.player.Player;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DatabaseFactory {
    public static Collection<Player> createEmptyDatabase() {
        return Lists.newArrayList();
    }

    public static Iterable<? extends Player> createDatabase(String filename) throws FileNotFoundException {
        DefaultDatabaseBuilder builder = new DefaultDatabaseBuilder(createEmptyDatabase());
        builder.addFromFile(filename);
        return builder.build();
    }

    public static Map<String, Player> createIdMap(Iterable<? extends Player> database, IdReader idReader) {
        Map<String, Player> mergeMap = Maps.newHashMap();
        for (Player p : database) {
            String key = null;
            try {
                key = idReader.getId(p);
            } catch (Exception e) {

            }
            mergeMap.put(key, p);
        }

        return mergeMap;
    }
}
