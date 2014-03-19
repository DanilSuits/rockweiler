/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.jackson;

import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Schema {
    public static class Player {
        public Map<String, String> id;
        public Bio bio;
    }

    public static class Bio {
        public String name;
        public String dob;
    }
}
