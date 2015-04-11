/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import rockweiler.player.Player;

import java.io.IOException;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JsonPlayer implements Player,JsonSerializable {
    private final JsonNode root;
    private final Player.Ids ids;
    private final Player.Bio bio;

    public JsonPlayer(JsonNode root, Player.Ids ids, Player.Bio bio) {
        this.root = root;
        this.ids = ids;
        this.bio = bio;
    }

    public Ids getIds() {
        return ids;
    }

    public Bio getBio() {
        return bio;
    }

    public void serialize(JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeTree(root);
    }

    public void serializeWithType(JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSerializer) throws IOException, JsonProcessingException {
        serialize(jsonGenerator,serializerProvider);
    }
}
