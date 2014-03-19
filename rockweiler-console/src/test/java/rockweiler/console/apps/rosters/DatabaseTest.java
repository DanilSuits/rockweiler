/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rosters;

import org.testng.annotations.Test;
import rockweiler.repository.JacksonPlayerRepository;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DatabaseTest {

    @Test
    public void testDatabaseLoad() {
        JacksonPlayerRepository db = JacksonPlayerRepository.create("/master.player.json");
    }
}
