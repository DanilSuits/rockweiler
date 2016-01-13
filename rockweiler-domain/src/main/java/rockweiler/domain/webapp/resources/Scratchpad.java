/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.webapp.resources;

import com.google.common.collect.Maps;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

/**
 * @author Danil Suits (danil@vast.com)
 */
@Path("/scratchpad")
public class Scratchpad {

    @GET
    @Path("/new")
    @Produces(MediaType.TEXT_HTML)
    public Scratchpadview scratchPad() {

        Map<String, String> dto = Maps.newHashMap();

        final URI self = toForm()
                .build();

        dto.put("self", self.getPath());
        dto.put("home", Dashboard.toHome().build().getPath());
        dto.put("create", self.getPath());

        final Scratchpadview view = new Scratchpadview(dto);
        return view;
    }

    @POST
    @Path("/new")
    @Produces(MediaType.TEXT_HTML)
    public Response create() {
        String id = UUID.randomUUID().toString();
        final URI uri = toItem()
                .build(id);
        return Response.seeOther(uri).build();
    }

    @GET
    @Path("/{id}")
    public String showItem(@PathParam("id") String id) {
        return id;
    }

    public static UriBuilder toItem() {
        return UriBuilder
                .fromResource(Scratchpad.class)
                .path(Scratchpad.class, "showItem");
    }

    public static UriBuilder toForm() {
        return UriBuilder
                .fromResource(Scratchpad.class)
                .path(Scratchpad.class, "scratchPad");
    }
}
