/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player.jackson;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import rockweiler.idtools.player.Player;

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
