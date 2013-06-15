/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player.json;

import com.fasterxml.jackson.core.JsonGenerator;
import rockweiler.idtools.player.Player;

import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JsonPlayer implements Player {
    private final Map<String,Object> data;
    private final Ids ids;
    private final Bio bio;

    public JsonPlayer(Map<String, Object> data, Ids ids, Bio bio) {
        this.data = data;
        this.ids = ids;
        this.bio = bio;
    }

    public Ids getIds() {
        return ids;
    }

    public Bio getBio() {
        return bio;
    }

    Map<String,Object> getData() {
        return data;
    }
}
