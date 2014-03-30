package rockweiler.console.core.modules;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.cli.*;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/30/14
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Startup {
    public static Startup create(String key) {
        Config fallback = ConfigFactory.load(key);
        return new Startup(fallback);
    }

    static final Option CLI_CONFIG = OptionBuilder
            .isRequired(false)
            .withDescription("typesafe.conf")
            .hasArg().withArgName("path-to-config")
            .create("config");

    private final Config fallbackConfig;

    public Startup(Config fallbackConfig) {
        this.fallbackConfig = fallbackConfig;
    }

    public Config readConfiguration(String[] args) throws ParseException {
        Config config = fallbackConfig;

        CommandLine commandLine = parse(args);
        if (commandLine.hasOption(CLI_CONFIG.getOpt())) {
            String configPath = commandLine.getOptionValue(CLI_CONFIG.getOpt());

            config = ConfigFactory.parseFile(new File(configPath))
                    .withFallback(config)
                    .resolve();

        }

        return config;
    }

    CommandLine parse(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(CLI_CONFIG);

        return new GnuParser().parse(options, args);
    }
}
