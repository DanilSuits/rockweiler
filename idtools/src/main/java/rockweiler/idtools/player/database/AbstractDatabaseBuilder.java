/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player.database;

import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.jackson.JsonPlayerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Danil Suits (danil@vast.com)
 */
public abstract class AbstractDatabaseBuilder {
    private final JsonPlayerFactory factory = new JsonPlayerFactory();

    public abstract Iterable<? extends Player> build();

    public AbstractDatabaseBuilder addFromFile(String filename) throws FileNotFoundException {
        File dbSource = new File(filename);
        Scanner dbScanner = new Scanner(dbSource);

        add(dbScanner);

        return this;
    }

    public AbstractDatabaseBuilder add(Scanner scanner) {
        while(scanner.hasNext()) {
            add(scanner.next());
        }
        return this;
    }

    public AbstractDatabaseBuilder add(String json) {
        Player player = factory.toPlayer(json);
        add(player);
        return this;
    }

    public AbstractDatabaseBuilder add(Player player) {
        doAdd(player);
        return this;
    }

    protected abstract void doAdd(Player player);
}
