/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ProjectedPlayerPool implements PlayerPool<Map<String,String>> {
    private final Supplier<Map<String,Map<String,String>>> source;

    public ProjectedPlayerPool(Supplier<Map<String, Map<String, String>>> source) {
        this.source = source;
    }

    public List<Map<String, String>> query(String hint) {
        Map<String,Map<String,String>> crnt = source.get();

        List<Map<String,String>> results = Lists.newArrayList();
        for(Map.Entry<String,Map<String,String>> entry : crnt.entrySet()) {
            boolean match = false;

            Map<String,String> player = entry.getValue();
            if (player.get("id").contains(hint)) {
                match = true;
            } else if (player.get("name").contains(hint)) {
                match = true;
            }

            if (match) {
                results.add(player);
            }
        }

        return results;
    }
}
