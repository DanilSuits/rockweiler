/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class PlayerDictionaryFactory {
    ObjectMapper om = new ObjectMapper();

    Map<String,Map<String,String>> readFile(String source) {

        Scanner scanner = new Scanner(source);
        Map<String, Map<String, String>> players = parse(scanner);

        scanner.close();
        return players;
    }

    Map<String, Map<String, String>> parse(Scanner scanner) {
        Map<String, Map<String,String>> players = Maps.newHashMap();
        while (scanner.hasNext()) {
            String json = scanner.nextLine();
            try {
                JsonNode root = om.readTree(json);
                String type = root.get("event_type").asText();
                if ("PlayerReferenceDiscovered".equals(type)) {
                    JsonNode data = root.get("data");

                    Map<String,String> player = Maps.newHashMap();
                    player.put("id", data.get("id").get("bbref").asText());
                    player.put("name", data.get("bio").get("name").asText());
                    player.put("dob", data.get("bio").get("dob").asText());

                    players.put(player.get("id"), player);

                }

            } catch (IOException e) {
                throw new RuntimeException(json, e);
            }
        }
        return players;
    }


}
