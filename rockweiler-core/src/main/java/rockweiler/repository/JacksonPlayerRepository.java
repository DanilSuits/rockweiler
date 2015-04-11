/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import rockweiler.player.jackson.Schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JacksonPlayerRepository implements PlayerRepository<Schema.Player> {
    public static final TypeReference<List<Schema.Player>> SCHEMA_PLAYER_REPO = new TypeReference<List<Schema.Player>>() {
    };

    public JacksonPlayerRepository(List<Schema.Player> players, List<Schema.Player> provisional) {
        this.players = players;
        this.provisional = provisional;
    }

    public static JacksonPlayerRepository create(String resourceName) {
        try {
            InputStream is = JacksonPlayerRepository.class.getResourceAsStream(resourceName);
            // TODO: this stream should be closed.
            return create(is);
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize database " + resourceName,e);
        }
    }

    public static JacksonPlayerRepository create(InputStream is) throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        List<Schema.Player> players = om.readValue(is, SCHEMA_PLAYER_REPO);
        List<Schema.Player> provisional = Lists.newArrayList();

        return new JacksonPlayerRepository(players, provisional);
    }

    private final List<Schema.Player> players;
    private final List<Schema.Player> provisional;

    public Iterable<Schema.Player> getPlayers() {
        return Iterables.concat(players,provisional);
    }

    public void add(Schema.Player player) {
        provisional.add(player);
    }
}
