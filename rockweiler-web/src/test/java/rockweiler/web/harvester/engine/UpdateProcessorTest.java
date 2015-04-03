/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.web.harvester.engine;

import org.testng.annotations.Test;
import rockweiler.web.harvester.core.UpdateRequest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class UpdateProcessorTest {
    @Test
    public void testSimpleConnection() throws IOException {
        UpdateRequest args = new UpdateRequest();
        args.remoteUri = "http://www.rotoworld.com/player/mlb/4517/clayton-kershaw";

        args.localDestination = File.createTempFile("kershaw.", ".html").toString();

        UpdateProcessor target = new UpdateProcessor(URI.create("http://www.rotoworld.com"), TimeUnit.SECONDS.toMillis(1));
        target.process(args);

        System.out.println(args.localDestination);
    }
}
