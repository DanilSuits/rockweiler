/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.io;

import rockweiler.player.Player;
import rockweiler.player.database.DatabaseFactory;
import rockweiler.player.jackson.JsonWriter;
import rockweiler.player.jackson.SimpleArchive;
import sun.java2d.pipe.SpanShapeRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;

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
        return new SortingWriter(createRawWriter());
    }

    public Writer createWriter(Comparator<Player> ordering) {
        return new SortingWriter(ordering, createRawWriter());
    }

    Writer createRawWriter() {
        final SimpleArchive<Player> simpleArchive = new SimpleArchive<Player>();

        Writer toFile = new Writer() {
            public void writePlayers(String key, Iterable<? extends Player> players) throws KeyNotFoundException, KeyNotUpdatedException {
                PrintStream out = createStream(key);
                try {
                    simpleArchive.archive(players, out);
                } catch (IOException e) {
                    throw new KeyNotUpdatedException("Unable to update " + key, e);
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

        return toFile;
    }
}
