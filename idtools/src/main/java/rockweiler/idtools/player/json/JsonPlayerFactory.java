/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.collect.Maps;
import rockweiler.idtools.player.IdConflictException;
import rockweiler.idtools.player.Player;

import java.io.IOException;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JsonPlayerFactory {
    public JsonPlayer toPlayer(String src) {

        JsonParser parser = null;
        try {
            parser = new JsonFactory().createJsonParser(src);
            parser.nextToken();
            if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
                throw new RuntimeException("failed to parse");
            }

            Map<String, Object> data = toMap(parser);
            return toJsonPlayer(data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse: " + src, e);
        }

    }

    JsonPlayer toJsonPlayer(Map<String, Object> data) {
        final Map<String, Object> idMap = (Map<String, Object>) data.get("id");

        Player.Ids ids = new Player.Ids() {
            public void add(String key, String value) {
                idMap.put(key, value);
            }

            public void merge(Player.Ids rhs) throws IdConflictException {
                for (String rhsKey : rhs.all()) {
                    if (idMap.containsKey(rhsKey)) {
                        if (!idMap.get(rhsKey).equals(rhs.get(rhsKey))) {
                            throw new IdConflictException("Conflict on " + rhsKey);
                        }
                    }
                }

                for (String rhsKey : rhs.all()) {
                    idMap.put(rhsKey, rhs.get(rhsKey));
                }
            }

            public String get(String key) {
                return (String) idMap.get(key);
            }

            public int count() {
                return idMap.size();
            }

            public Iterable<String> all() {
                return idMap.keySet();
            }
        };

        Player.Bio bio = parseBio(data);

        return new JsonPlayer(data, ids, bio);
    }

    private enum BIO implements Player.Bio {
        MISSING;

        public String getName() {
            return "_BIO_MISSING_";
        }

        public String getDob() {
            return "00000000";
        }
    }

    private Player.Bio parseBio(Map<String, Object> data) {
        Player.Bio bio = BIO.MISSING;

        final Map<String, Object> bioMap = (Map<String, Object>) data.get("bio");

        if (null != bioMap) {
            bio = new Player.Bio() {
                public String getName() {
                    return (String) bioMap.get("name");
                }

                public String getDob() {
                    return (String) bioMap.get("dob");
                }
            };
        }
        return bio;
    }

    private Map<String, Object> toMap(JsonParser parser) throws IOException {
        Map<String, Object> data = Maps.newHashMap();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String header = parser.getCurrentName();

            parser.nextToken(); // move to value
            if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
                data.put(header, toMap(parser));
            } else {
                data.put(header, parser.getValueAsString());
            }
        }

        return data;
    }
}
