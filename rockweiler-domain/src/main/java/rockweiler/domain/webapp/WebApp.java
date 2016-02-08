/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.webapp;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import com.vocumsineratio.eventstore.EventStoreConnection;
import com.vocumsineratio.eventstore.persistence.memory.ConnectionBuilder;
import com.vocumsineratio.eventstore.persistence.memory.MemoryStore;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import rockweiler.domain.webapp.resources.Dashboard;
import rockweiler.domain.webapp.resources.Scratchpad;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class WebApp extends Application<WebAppConfiguration> {
    public static void main(String[] args) throws Exception {
        WebApp theApp = new WebApp();
        theApp.run(args);
    }

    @Override
    public void run(WebAppConfiguration webAppConfiguration, Environment environment) throws Exception {
        environment.jersey().register(new Dashboard());
        final EventStoreConnection connection = ConnectionBuilder.create()
                .with(MoreExecutors.newDirectExecutorService())
                .connect();

        environment.jersey().register(new Scratchpad(connection));
    }

    @Override
    public void initialize(Bootstrap<WebAppConfiguration> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.addBundle(new ViewBundle<WebAppConfiguration>());
    }
}
