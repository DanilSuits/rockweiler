/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import rockweiler.player.jackson.IdSerializer;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface Player {
    interface Bio {
        String getName();
        String getDob();
    }

    @JsonSerialize(using = IdSerializer.class)
    interface Ids {
        void add(String key, String value);
        void merge(Ids rhs) throws IdConflictException;
        String get(String key);
        int count();

        Iterable<String> all();
    }

    @JsonProperty("id")
    Ids getIds();

    Bio getBio();
}
