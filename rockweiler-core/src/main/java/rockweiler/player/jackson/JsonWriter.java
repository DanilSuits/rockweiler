/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.jackson;

import org.codehaus.jackson.map.ObjectMapper;
import rockweiler.player.Player;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JsonWriter {
    private final OutputStream out;

    public JsonWriter(OutputStream out) {
        this.out = out;
    }

    public void collect(Player player) {
            try {
                ObjectMapper writer = new ObjectMapper();
                out.write(writer.writeValueAsString(player).getBytes("UTF-8"));
                out.write('\n');
            } catch (IOException e) {
                e.printStackTrace(System.err);
                throw new RuntimeException(e);
            }
    }
}
