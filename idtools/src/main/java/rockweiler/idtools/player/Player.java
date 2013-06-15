/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface Player {
    interface Bio {
        String getName();
        String getDob();
    }

    interface Ids {
        void add(String key, String value);
        void merge(Ids rhs) throws IdConflictException;
        String get(String key);
        int count();

        Iterable<String> all();
    }

    Ids getIds();
    Bio getBio();
}
