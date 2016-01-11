/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.webapp.resources;

import com.google.common.collect.Lists;
import rockweiler.domain.api.Link;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Danil Suits (danil@vast.com)
 */
@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class Dashboard {
    @GET
    public List<Link> dashboard() {
        Link self = new Link("self", UriBuilder.fromResource(Dashboard.class).build().getPath());

        String id = UUID.randomUUID().toString();
        Link create = new Link("create", UriBuilder.fromResource(Scratchpad.class).build(id).getPath());


        return Lists.newArrayList(self, create);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public DashboardView view () {
        return new DashboardView(dashboard());
    }
}
