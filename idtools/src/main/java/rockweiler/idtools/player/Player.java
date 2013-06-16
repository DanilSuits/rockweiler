/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import rockweiler.idtools.player.json.IdSerializer;

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
