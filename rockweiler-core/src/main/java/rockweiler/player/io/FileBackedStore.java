/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.io;

import rockweiler.player.Player;
import rockweiler.player.database.DatabaseFactory;
import rockweiler.player.jackson.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class FileBackedStore implements PlayerStore {
    public FileBackedStore(File rootDir) {
        this.rootDir = rootDir;
    }

    public static PlayerStore create(String rootPath) {
        File root = new File(rootPath);
        return new FileBackedStore(root);
    }

    private final File rootDir;

    public Reader createReader() {
        return new Reader() {
            public Iterable<? extends Player> readPlayers(String key) throws KeyNotFoundException {
                try {
                    // ToDo
                    return DatabaseFactory.createDatabase(key);
                } catch (FileNotFoundException e) {
                    throw new KeyNotFoundException(key, e);
                }
            }
        };
    }

    public Writer createWriter() {
        Writer toFile = new Writer() {
            public void writePlayers(String key, Iterable<? extends Player> players) throws KeyNotFoundException, KeyNotUpdatedException {
                PrintStream out = createStream(key);
                JsonWriter writer = new JsonWriter(out);
                for(Player player : players) {
                    writer.collect(player);
                }
            }

            private PrintStream createStream(String key) throws KeyNotFoundException {
                File destination = new File(key);
                try {
                    return new PrintStream(destination);
                } catch (FileNotFoundException e) {
                    throw new KeyNotFoundException(key,e);
                }
            }
        } ;

        return new SortingWriter(toFile);
    }
}
