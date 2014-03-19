/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.util.Map;
import java.util.Set;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ConsistencyCheck {

    public static void main(String[] args) {
        Set<Schema.Player> conflicts = Sets.newHashSet();

        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");

        Map<String,Map<String,Schema.Player>> idMap = Maps.newHashMap();

        for(Schema.Player p : repository.getPlayers()) {
            for (Map.Entry<String,String> entry : p.id.entrySet()) {
                Map<String, Schema.Player> source = idMap.get(entry.getKey());
                if (null == source) {
                    source = Maps.newHashMap();
                    idMap.put(entry.getKey(),source);
                }

                if (source.containsKey(entry.getValue())) {
                    conflicts.add(p);
                    break;
                }

                source.put(entry.getValue(),p);
            }
        }

        for(Schema.Player p : conflicts) {
            System.out.println(p.bio.dob + " " + p.bio.name);
        }



    }
}
