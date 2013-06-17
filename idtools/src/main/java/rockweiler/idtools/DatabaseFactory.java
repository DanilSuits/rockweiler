/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import rockweiler.idtools.player.IdReader;
import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.SortingCollector;
import rockweiler.idtools.player.database.DefaultDatabaseBuilder;
import rockweiler.idtools.player.json.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DatabaseFactory {
    public static Iterable<Player> createEmptyDatabase() {
        return Collections.EMPTY_LIST;
    }

    public static Iterable<? extends Player> createDatabase(String filename) throws FileNotFoundException {

        List<Player> coreDB = Lists.newArrayList();

        DefaultDatabaseBuilder builder = new DefaultDatabaseBuilder(coreDB);
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

    public static DatabaseWriter createWriter(String filename) throws IOException {
        File destination = new File(filename);
        PrintStream out = new PrintStream(destination);
        return createWriter(out);
    }

    private static final Comparator<Player> NAME_ORDER = new Comparator<Player>() {
        public int compare(Player lhs, Player rhs) {
            return lhs.getBio().getName().compareTo(rhs.getBio().getName());
        }
    };

    public static DatabaseWriter createWriter(final OutputStream out) throws IOException {

        final JsonWriter json = new JsonWriter(out);
        final SortingCollector sort = new SortingCollector(json, NAME_ORDER);

        return new DatabaseWriter(out, sort);
    }
}
