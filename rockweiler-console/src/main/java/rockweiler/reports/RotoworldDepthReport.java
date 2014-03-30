package rockweiler.reports;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import rockweiler.console.core.modules.Startup;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/30/14
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class RotoworldDepthReport {
    public static void main(String[] args) throws Exception {
        Startup startup = Startup.create("rotoworld-depth");

        Config config = startup.readConfiguration(args);

    }
}
