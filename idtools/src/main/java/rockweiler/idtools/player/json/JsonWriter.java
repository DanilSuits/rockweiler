/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player.json;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

import rockweiler.idtools.player.AbstractPlayerCollector;
import rockweiler.idtools.player.Player;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JsonWriter extends AbstractPlayerCollector {
    private final OutputStream out;

    public JsonWriter(OutputStream out) {
        this.out = out;
    }

    public void collect(Player player) {
        if (player instanceof JsonPlayer) {
            JsonPlayer jsonPlayer = (JsonPlayer) player;
            try {
                ObjectMapper writer = new ObjectMapper();
                byte[] jsonOut = writer.writeValueAsBytes(jsonPlayer.getData());
                out.write(jsonOut);
                out.write('\n');

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
