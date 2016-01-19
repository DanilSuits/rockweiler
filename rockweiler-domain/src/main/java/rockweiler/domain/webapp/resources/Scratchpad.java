/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.webapp.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Danil Suits (danil@vast.com)
 */
@Path("/scratchpad")
public class Scratchpad {

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/create")
    public Response create() {
        String id = UUID.randomUUID().toString();
        final URI uri = toItem()
                .build(id, -1);
        return Response.seeOther(uri).build();
    }

    @GET
    @Path("/{id}/versions/{version}/form")
    @Produces(MediaType.TEXT_HTML)
    public Scratchpadview showForm(@PathParam("id") String id, @PathParam("version") int version) {
        Map<String, Object> dto = Maps.newHashMap();

        final URI self = toForm()
                .build(id, version);

        Map<String,String> links = Maps.newHashMap();
        links.put("self", self.getPath());
        links.put("home", Dashboard.toHome().build().getPath());

        dto.put("links", links);

        final Scratchpadview view = new Scratchpadview(dto);
        return view;
    }

    @POST
    @Path("/{id}/versions/{version}/form")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.TEXT_HTML)
    public Scratchpadview addPlayer(@PathParam("id") String id, @PathParam("version") int version, Form form) {

        List<Object> rankedPlayers = Lists.newArrayList();
        Map<String,String> player = Maps.newHashMap();
        player.put("name", form.asMap().getFirst("name"));
        rankedPlayers.add(0,player);


        Map<String,Object> debug = Maps.newHashMap();
        debug.put("query", form.asMap());

        Map<String, Object> dto = Maps.newHashMap();
        dto.put("debug", debug);

        dto.put("rankedPlayers", rankedPlayers);

        final URI self = toForm()
                .build(id, version);

        Map<String,String> links = Maps.newHashMap();
        links.put("self", self.getPath());
        links.put("home", Dashboard.toHome().build().getPath());

        dto.put("links", links);

        final Scratchpadview view = new Scratchpadview(dto);
        return view;
    }


    public static UriBuilder toItem() {
        return UriBuilder
                .fromResource(Scratchpad.class)
                .path(Scratchpad.class, "showForm");
    }

    public static UriBuilder toForm() {
        return UriBuilder
                .fromResource(Scratchpad.class)
                .path(Scratchpad.class, "create");
    }
}
