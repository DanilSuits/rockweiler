/**
 * Copyright Vast 2015. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.web.players.webapp;

import de.thomaskrille.dropwizard_template_config.TemplateConfigBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import rockweiler.web.players.webapp.resources.PlayersResource;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class WebApp extends Application<WebAppConfiguration>{
    private final TemplateConfigBundle templateConfig;

    public WebApp(TemplateConfigBundle templateConfig) {
        this.templateConfig = templateConfig;
    }

    @Override
    public void initialize(Bootstrap<WebAppConfiguration> bootstrap) {
        bootstrap.addBundle(templateConfig);
    }

    @Override
    public void run(WebAppConfiguration webAppConfiguration, Environment environment) throws Exception {
        PlayersResource playersResource = new PlayersResource();
        environment.jersey().register(playersResource);
    }

    public static void main(String[] args) throws Exception {
        TemplateConfigBundle templateConfig = new TemplateConfigBundle();
        WebApp theApp = new WebApp(templateConfig);
        theApp.run(args);
    }
}
