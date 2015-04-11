/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import rockweiler.player.Player;

import java.io.IOException;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class IdSerializer extends JsonSerializer<Player.Ids> {
    @Override
    public void serialize(Player.Ids ids, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        for(String key : ids.all()) {
            jsonGenerator.writeStringField(key,ids.get(key));
        }
        jsonGenerator.writeEndObject();
    }
}
