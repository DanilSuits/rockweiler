/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.web.harvester.webapp;

import com.lmax.disruptor.RingBuffer;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import rockweiler.web.harvester.core.UpdateRequest;
import rockweiler.web.harvester.webapp.resources.TestResource;
import rockweiler.web.harvester.webapp.resources.UpdateQueue;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class WebApp extends Application<WebAppConfiguration> {
    @Override
    public void initialize(Bootstrap<WebAppConfiguration> bootstrap) {
        //TODO: To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void run(WebAppConfiguration configuration, Environment environment) throws Exception {
        TestResource testResource = new TestResource();
        environment.jersey().register(testResource);

        final RingBuffer<UpdateRequest> requestQueue = configuration.messageQueue.build(environment);

        environment.jersey().register(new UpdateQueue(requestQueue));
    }

    public static void main(String[] args) throws Exception {
        WebApp theApp = new WebApp();
        theApp.run(args);
    }
}
