/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import rockweiler.idtools.player.AbstractPlayerCollector;
import rockweiler.idtools.player.PlayerCollector;
import rockweiler.idtools.player.SortingCollector;
import rockweiler.idtools.player.json.JsonPlayerFactory;
import rockweiler.idtools.player.json.JsonPlayerScanner;
import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.IdReader;
import rockweiler.idtools.player.json.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DatabaseFactory {
    public static Iterable<Player> createDatabase(String filename) throws FileNotFoundException {
        File dbSource = new File(filename);
        Scanner dbScanner = new Scanner(dbSource);

        return createDatabase(dbScanner);
    }

    public static Iterable<Player> createDatabase(Scanner dbScanner) {
        JsonPlayerFactory factory = new JsonPlayerFactory();
        JsonPlayerScanner players = new JsonPlayerScanner(dbScanner, factory);

        return createDatabase(players);
    }

    public static Iterable<Player> createDatabase(JsonPlayerScanner players) {

        List<Player> database = Lists.newArrayList();
        while (players.hasNext()) {
            database.add(players.next());
        }

        return database;
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
        JsonGenerator generator = new JsonFactory().createJsonGenerator(out, JsonEncoding.UTF8);

        final JsonWriter json = new JsonWriter(out);
        final SortingCollector sort = new SortingCollector(json, NAME_ORDER);

        return new DatabaseWriter(out, sort);
    }
}
