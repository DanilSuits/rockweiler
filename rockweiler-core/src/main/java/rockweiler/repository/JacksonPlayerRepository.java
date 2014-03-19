/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.repository;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import rockweiler.player.jackson.Schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JacksonPlayerRepository {
    private static final TypeReference<List<Schema.Player>> PLAYER_LIST = new TypeReference<List<Schema.Player>>() {
    };

    public JacksonPlayerRepository(List<Schema.Player> players) {
        this.players = players;
    }

    public static JacksonPlayerRepository create(String resourceName) {
        try {
            InputStream is = JacksonPlayerRepository.class.getResourceAsStream(resourceName);
            return create(is);
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize database " + resourceName,e);
        }
    }

    public static JacksonPlayerRepository create(InputStream is) throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        List<Schema.Player> players = om.readValue(is, PLAYER_LIST);

        return new JacksonPlayerRepository(players);
    }

    private final List<Schema.Player> players;
}
