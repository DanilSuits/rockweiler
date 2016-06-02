/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.webapp;

import com.google.common.util.concurrent.MoreExecutors;
import com.vocumsineratio.eventstore.EventStoreConnection;
import com.vocumsineratio.eventstore.persistence.memory.ConnectionBuilder;
import com.vocumsineratio.eventstore.persistence.memory.StreamPosition;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import rockweiler.domain.webapp.resources.Dashboard;
import rockweiler.domain.webapp.resources.Scratchpad;

import java.util.function.Consumer;

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

        Consumer<StreamPosition> messageBus = new Consumer<StreamPosition>() {
            @Override
            public void accept(StreamPosition item) {
                StringBuilder msg = new StringBuilder("onUpdate {")
                        .append(item.streamId.streamId)
                        .append(":")
                        .append(item.expectedVersion.version)
                        .append("}");

                System.out.println(msg.toString());
                System.out.flush();
            }
        };

        final EventStoreConnection connection = ConnectionBuilder.create()
                .with(MoreExecutors.newDirectExecutorService())
                .onUpdateNotify(messageBus)
                .connect();

        environment.jersey().register(new Scratchpad(connection));
    }

    @Override
    public void initialize(Bootstrap<WebAppConfiguration> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.addBundle(new ViewBundle<WebAppConfiguration>());
    }
}
