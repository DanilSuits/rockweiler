/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.webapp.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vocumsineratio.cqrs.Event;
import com.vocumsineratio.eventstore.EventStore;
import com.vocumsineratio.eventstore.EventStoreConnection;
import com.vocumsineratio.eventstore.Exceptions;
import com.vocumsineratio.eventstore.api.ExpectedVersion;
import com.vocumsineratio.eventstore.api.StreamId;
import rockweiler.domain.model.domain.Hint;
import rockweiler.domain.model.events.History;
import com.vocumsineratio.domain.Id;
import rockweiler.domain.model.domain.PlayerRanked;
import rockweiler.domain.model.domain.Rank;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
@Path("/scratchpad")
public class Scratchpad {
    // TODO:
    private final EventStoreConnection connection;

    public Scratchpad(EventStoreConnection connection) {
        this.connection = connection;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/create")
    public Response create() {
        Id id = Id.create();
        return seeNextForm(id);
    }

    public Response seeNextForm(Id id) {
        Id eventId = Id.create();

        final URI uri = toItem()
                .build(id, eventId);
        return Response.seeOther(uri).build();
    }

    @GET
    @Path("/{rankingId}/events/{eventId}/addPlayer")
    @Produces(MediaType.TEXT_HTML)
    public Scratchpadview showForm(@PathParam("rankingId") String id, @PathParam("eventId") String event) {

        final Id rankingId = Id.from(id);
        final Id eventId = Id.from(event);

        EventStore eventStore = connection.get();

        List<Object> rankedPlayers = Lists.newArrayList();
        for(Event e : eventStore.get(StreamId.of(rankingId))) {
            if (PlayerRanked.class.isInstance(e.data)) {
                PlayerRanked playerRanked = PlayerRanked.class.cast(e.data);
                if (rankingId.isSameValue(playerRanked.rankingId)) {
                    int rank = playerRanked.rank.id;
                    String playerHint = playerRanked.playerName.hint;
                    rankedPlayers.add(rank, playerHint);
                }
            }
        }

        Rank nextRank = Rank.of(rankedPlayers.size());

        Map<String, Object> dto = Maps.newHashMap();

        Map<String,String> links = Maps.newHashMap();
        links.put("self", toItem().build(rankingId, eventId).getPath());
        links.put("home", Dashboard.toHome().build().getPath());
        links.put("events", toAddPlayer().build(id).getPath() );

        Map<String,String> eventData = Maps.newHashMap();
        eventData.put("rankingId", rankingId.toString());
        eventData.put("eventId", eventId.toString());
        eventData.put("rank", nextRank.toString());

        dto.put("event", eventData);

        Map<String,Object> debug = Maps.newHashMap();
        dto.put("debug", debug);

        dto.put("rankedPlayers", rankedPlayers);

        dto.put("links", links);

        final Scratchpadview view = new Scratchpadview(dto);
        return view;
    }

    @POST
    @Path("/{id}/events")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.TEXT_HTML)
    public Response addPlayer(@PathParam("id") String id, Form form) {
        Id rankingId = Id.from(id);

        final MultivaluedMap<String, String> formData = form.asMap();
        Id eventId = Id.from(formData.getFirst("eventId"));
        Rank rank = Rank.parse(formData.getFirst("rank"));
        Hint playerName = new Hint(formData.getFirst("playerName"));

        PlayerRanked event = new PlayerRanked(rankingId, rank, playerName);

        EventStore eventStore = connection.get();

        try {
            Event<PlayerRanked> playerRankedEvent = event.create(eventId);
            List<? extends Event> events = Lists.newArrayList(playerRankedEvent);
            eventStore.store(StreamId.of(rankingId), ExpectedVersion.Any, events);

            return seeNextForm(rankingId);
        } catch (Exceptions.WrongVersionException e) {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    public static UriBuilder toAddPlayer() {
        return UriBuilder
                .fromResource(Scratchpad.class)
                .path(Scratchpad.class, "addPlayer");
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
