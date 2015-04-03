/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.web.harvester.webapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;
import io.dropwizard.Configuration;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import rockweiler.web.harvester.core.UpdateRequest;
import rockweiler.web.harvester.engine.UpdateProcessor;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class WebAppConfiguration extends Configuration {
    @JsonProperty("requestQueue")
    public RequestQueueFactory messageQueue = new RequestQueueFactory();

    public static class RequestQueueFactory {
        @JsonProperty("size")
        public int size = 1024;

        @JsonProperty("remoteClients")
        public List<RemoteClient> remoteClients;

        public RingBuffer<UpdateRequest> build(Environment environment) {

            EventFactory<UpdateRequest> compressionRequestEventFactory = new EventFactory<UpdateRequest>() {
                public UpdateRequest newInstance() {
                    return new UpdateRequest();
                }
            };

            final RingBuffer<UpdateRequest> requestQueue = RingBuffer.createMultiProducer(compressionRequestEventFactory, size);

            List<BatchEventProcessor<UpdateRequest>> processors = Lists.newArrayList();
            for(RemoteClient remote : remoteClients) {

                final URI remoteHost = URI.create(remote.uri);
                final UpdateProcessor remoteClient = new UpdateProcessor(remoteHost, TimeUnit.SECONDS.toMillis(remote.waitTime));

                final EventHandler<UpdateRequest> requestHandler = new EventHandler<UpdateRequest>() {
                    public void onEvent(UpdateRequest event, long sequence, boolean endOfBatch) throws Exception {
                        remoteClient.process(event);
                    }
                };

                final BatchEventProcessor<UpdateRequest> requestProcessor = new BatchEventProcessor(requestQueue, requestQueue.newBarrier(), requestHandler);
                requestQueue.addGatingSequences(requestProcessor.getSequence());

                processors.add(requestProcessor);
            }

            final ExecutorService requestService = environment.lifecycle().executorService("updateProcessorThread")
                    .minThreads(processors.size())
                    .build();

            environment.lifecycle().manage(createManaged(requestService, processors));

            return requestQueue;
        }
    }

    static private Managed createManaged(final ExecutorService executorService, final Collection<? extends EventProcessor> tasks) {
        return new Managed() {
            public void start() throws Exception {
                for (Runnable task : tasks) {
                    executorService.submit(task);
                }
            }

            public void stop() throws Exception {
                for (EventProcessor task : tasks) {
                    task.halt();
                }
            }
        };
    }

    public static class RemoteClient {
        public String name;
        public String uri;
        public int waitTime;
    }

}
