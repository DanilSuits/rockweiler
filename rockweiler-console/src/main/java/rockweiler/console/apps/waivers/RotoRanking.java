/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.waivers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import rockweiler.console.core.MessageListener;
import rockweiler.player.jackson.Schema;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class RotoRanking {
    // TODO: Factory
    public static Schema.Player toPlayer(JsonNode root) {
        Schema.Player player = new Schema.Player();
        player.id = Maps.newTreeMap();
        JsonNode id = root.get("id");
        Iterator<String> keys = id.fieldNames();
        while (keys.hasNext()) {
            String key = keys.next();
            player.id.put(key, id.get(key).asText());
        }

        player.bio = new Schema.Bio();
        player.bio.name = root.get("bio").get("name").asText();
        player.bio.dob = root.get("bio").get("dob").asText();

        return player;

    }

    public static void main(String[] args) throws Exception {

        final ShardedRepository repository = new ShardedRepository();
        final Set<Schema.Player> claimed = Sets.newHashSet();

        final ObjectMapper om = new ObjectMapper();

        MessageListener<String> historyInterpreter = new MessageListener<String>() {
            public void onMessage(String message) {
                try {
                    JsonNode rootNode = om.readTree(message);
                    Iterator<String> verbs = rootNode.fieldNames();
                    while (verbs.hasNext()) {
                        String verb = verbs.next();
                        if ("addPlayer".equals(verb)) {
                            JsonNode child = rootNode.get(verb);
                            Schema.Player player = toPlayer(child);
                            repository.addPlayer(player);

                            break;
                        }

                        // TODO: standardize verb spellings
                        if ("pickPlayer".equals(verb)) {
                            JsonNode child = rootNode.get(verb);
                            Iterator<String> idKeys = child.get("id").fieldNames();
                            while(idKeys.hasNext()) {
                                String key = idKeys.next();
                                String id = child.get("id").get(key).asText();
                                Schema.Player player = repository.get(key,id);
                                if (null != player) {
                                    claimed.add(player);
                                    break;
                                }
                            }
                        }

                        if ("waivePlayer".equals(verb)) {
                            JsonNode child = rootNode.get(verb);
                            Iterator<String> idKeys = child.get("id").fieldNames();
                            while(idKeys.hasNext()) {
                                String key = idKeys.next();
                                String id = child.get("id").get(key).asText();
                                Schema.Player player = repository.get(key,id);
                                claimed.remove(player);
                            }
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(message, e);
                }
            }
        };

        String seasonHistory = "/Users/danil/Dropbox/OOOL/data/2014/season/season.history.json";

        readHistory(seasonHistory, historyInterpreter);


        MessageListener<String> rankInterpreter = new MessageListener<String>() {
            public void onMessage(String message) {
                try {
                    RotoRank roto = om.readValue(message,RotoRank.class);
                    for(Map.Entry<String,String> entry : roto.player.id.entrySet()) {
                        Schema.Player known = repository.get(entry.getKey(), entry.getValue());
                        if (null != known) {
                            roto.player = known;
                            if (! claimed.contains(roto.player)) {
                                System.out.println(om.writeValueAsString(roto));
                                return;
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(message, e);
                }
            }
        };

        String ranks = "/Users/danil/Dropbox/OOOL/data/2014/season/rotoworld.rankings.json";
        ranks = "/Users/danil/Dropbox/OOOL/data/2014/season/20140717.rotoworld.waiver.json";

        readHistory(ranks, rankInterpreter);

    }

    private static void readHistory(String seasonHistory, MessageListener<String> historyInterpreter) throws IOException {
        File history = new File(seasonHistory);
        BufferedReader br = new BufferedReader(new FileReader(history));
        String line;
        while ((line = br.readLine()) != null) {
            historyInterpreter.onMessage(line);
        }
        br.close();
    }

    static class RotoRank {
        public String pos;
        public int rank;
        public Schema.Player player;
    }
}
