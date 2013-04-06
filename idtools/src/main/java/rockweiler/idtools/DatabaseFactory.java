/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import rockweiler.idtools.player.json.JsonPlayerFactory;
import rockweiler.idtools.player.json.JsonPlayerScanner;
import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.IdReader;
import rockweiler.idtools.player.json.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DatabaseFactory {
    public static Iterable<Player> createDatabase (String filename) throws FileNotFoundException {
        File dbSource = new File(filename);
        Scanner dbScanner = new Scanner(dbSource);

        return createDatabase(dbScanner);
    }

    public static Iterable<Player> createDatabase (Scanner dbScanner) {
        JsonPlayerFactory factory = new JsonPlayerFactory();
        JsonPlayerScanner players = new JsonPlayerScanner(dbScanner,factory);

        List<Player> database = Lists.newArrayList();
        while(players.hasNext()) {
            database.add(players.next());
        }

        return database;
    }

    public static Map<String,Player> createIdMap(Iterable<Player> database, IdReader idReader) {
        Map<String, Player> mergeMap = Maps.newHashMap();
        for (Player p : database) {
            String key = idReader.getId(p);
            mergeMap.put(key, p);
        }

        return mergeMap;
    }

    public static DatabaseWriter createWriter(String filename) throws FileNotFoundException {
        File destination = new File(filename);
        PrintStream out = new PrintStream(destination);
        return createWriter(out);
    }

    public static DatabaseWriter createWriter(PrintStream out) {
        JsonWriter json = new JsonWriter(out);
        return new DatabaseWriter(out,json);
    }
}
