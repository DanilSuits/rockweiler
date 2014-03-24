/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.database;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.testng.Assert;
import org.testng.annotations.Test;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class VerifyDatabaseTest {

    @Test
    public void testCheckForDuplicates () {
        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");

        Map<String,Map<String,Schema.Player>> idMap = Maps.newHashMap();

        List<Schema.Player> duplicates = Lists.newArrayList();

        for(Schema.Player p : repository.getPlayers()) {
            for (Map.Entry<String,String> id : p.id.entrySet()) {
                Map<String,Schema.Player> group = idMap.get(id.getKey());
                if (null == group) {
                    group = Maps.newHashMap();
                    idMap.put(id.getKey(),group);
                }

                if (group.containsKey(id.getValue())) {
                    System.err.println(id.getKey() + " : " +id.getValue());
                    System.err.println( p.bio.dob + " " + p.bio.name);

                    duplicates.add(p);
                    break;
                } else {
                    group.put(id.getValue(),p);
                }
            }

        }

        if (duplicates.size() > 0) {
            Assert.fail("Master database contains duplicates");
        }
    }
}
