/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import rockweiler.player.Player;

import java.io.IOException;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class SchemaPlayerFactory {
    public static SchemaPlayerFactory create () {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        return new SchemaPlayerFactory(om);
    }

    public static final TypeReference<Schema.Player> SCHEMA_PLAYER_TYPE = new TypeReference<Schema.Player>() {
    };

    public SchemaPlayerFactory(ObjectMapper om) {
        this.om = om;
    }

    private final ObjectMapper om;

    public Player toPlayer(String src) {
        try {
            Schema.Player p = om.readValue(src, Schema.Player.class);
            return Schema.TRANSFORM.apply(p);
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse " + src, e);
        }
    }
}
