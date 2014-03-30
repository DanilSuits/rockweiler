package rockweiler.console.apps.quickdraft;

import rockweiler.player.jackson.Schema;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/29/14
 * Time: 11:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class TrivialListRepository implements ListRepository {
    public List<Schema.Player> get(String key) {
        return Collections.EMPTY_LIST;
    }
}
