/**
 * Copyright Vast 2015. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.web.players.webapp.resources;

import rockweiler.web.players.core.PlayerProjection;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
@Path("players")
@Produces(MediaType.APPLICATION_JSON)
public class PlayersResource {
    @GET
    public List<PlayerProjection> getAllPlayers() {
        return Collections.EMPTY_LIST;
    }

    @PUT
    public CommandResult submitCommand() {
        return new CommandResult();
    }

    static class CommandResult {
        String msg = "OK";
    }
}
