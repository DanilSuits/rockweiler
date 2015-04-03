/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.web.harvester.webapp.resources;

import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.RingBuffer;
import rockweiler.web.harvester.core.UpdateRequest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Danil Suits (danil@vast.com)
 */
@Path("updates")
@Produces(MediaType.APPLICATION_JSON)
public class UpdateQueue {
    private final RingBuffer<UpdateRequest> ringBuffer;

    public UpdateQueue(RingBuffer<UpdateRequest> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    @POST
    public Response UpdateLocalCopy(UpdateRequest request) {
        Report report = new Report();

        try {
            long slot = ringBuffer.tryNext();
            UpdateRequest copy = ringBuffer.get(slot);
            copy.localDestination = request.localDestination;
            copy.remoteUri = request.remoteUri;

            ringBuffer.publish(slot);

            report.slotId = slot;

            return Response.accepted(report).build();

        } catch (InsufficientCapacityException e) {
            report.status = "Try Again Later";
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(report).build();
        }
    }

    public static class Report {
        public String status = "OK";
        public long slotId = Long.MIN_VALUE;
    }
}
