/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.webapp.resources;

import com.google.common.collect.Lists;
import rockweiler.domain.api.Link;
import rockweiler.domain.api.ScratchRanking;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Danil Suits (danil@vast.com)
 */
@Path("/scratchpad/{id}")
public class Scratchpad {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Scratchpadview scratchPad (@PathParam("id") String id) {

        Link self = new Link("self", UriBuilder.fromResource(Scratchpad.class).build(id).getPath());
        Link home = new Link("home", UriBuilder.fromResource(Dashboard.class).build().getPath());
        Link update = new Link("update", self.uri);

        List<Link> links = Lists.newArrayList(self, home, update);

        ScratchRanking ranking = new ScratchRanking(id);

        return new Scratchpadview(ranking, links);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public Scratchpadview post (@PathParam("id") String id) {
        return scratchPad(id);
    }
}
