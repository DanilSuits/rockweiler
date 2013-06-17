/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.jackson;

import com.google.common.collect.Lists;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import rockweiler.player.Biography;
import rockweiler.player.IdConflictException;
import rockweiler.player.Player;

import java.io.IOException;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class JsonPlayerFactory {
    public JsonPlayer toPlayer(String src) {
        ObjectMapper writer = new ObjectMapper();
        try {
            JsonNode root = writer.readTree(src);
            return toPlayer(root);
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse: " + src, e);
        }
    }

    public JsonPlayer toPlayer(JsonNode root) {
        Player.Ids ids = createIds(root);
        Player.Bio bio = createBio(root);

        return new JsonPlayer(root, ids, bio);
    }

    private Player.Bio createBio(JsonNode root) {
        JsonNode bio = root.with("bio");
        if (bio.isMissingNode()) {
            return Biography.BIO.MISSING;
        }

        return new Bio(bio);
    }

    private Player.Ids createIds(JsonNode root) {
        JsonNode id = root.with("id");

        return new Id(id);
    }

    private static class Bio implements Player.Bio {
        private final JsonNode bio;

        private Bio(JsonNode bio) {
            this.bio = bio;
        }

        public String getName() {
            return bio.path("name").getTextValue();
        }

        public String getDob() {
            return bio.path("dob").getTextValue();
        }
    }

    private static class Id implements Player.Ids {
        private final JsonNode id;

        private Id(JsonNode id) {
            this.id = id;
        }

        public void add(String key, String value) {
            ObjectNode root = (ObjectNode) id;
            root.put(key, value);
        }

        public void merge(Player.Ids rhs) throws IdConflictException {
            ObjectNode root = (ObjectNode) id;
            for(String key : rhs.all()) {
                JsonNode lhs = root.path(key);
                if (lhs.isMissingNode()) {
                    root.put(key, rhs.get(key));
                } else {
                    String leftId = lhs.getTextValue();
                    String rightId = rhs.get(key);

                    if ( ! leftId.equals(rightId)) {
                        throw new IdConflictException(key + "lhs:" + leftId + " rhs:" + rightId);
                    }
                }
            }
        }

        public String get(String key) {
            JsonNode target = id.path(key);
            if (target.isMissingNode()) {
                return null;
            }
            return target.getTextValue();
        }

        public int count() {
            return id.size();
        }

        public Iterable<String> all() {
            List<String> keys = Lists.newArrayList(id.getFieldNames());
            return keys;
        }
    }

}
