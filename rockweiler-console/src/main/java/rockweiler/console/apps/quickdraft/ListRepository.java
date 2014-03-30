package rockweiler.console.apps.quickdraft;

import rockweiler.player.jackson.Schema;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/29/14
 * Time: 11:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ListRepository {
    List<Schema.Player> get(String key);
}
