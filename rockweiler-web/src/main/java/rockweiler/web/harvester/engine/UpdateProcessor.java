/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.web.harvester.engine;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import rockweiler.web.harvester.core.UpdateRequest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class UpdateProcessor {

    private final HttpClient client = HttpClients.createDefault();

    private final URI destination;
    private final long sleepInterval;

    public UpdateProcessor(URI destination, long sleepInterval) {
        this.destination = destination;
        this.sleepInterval = sleepInterval;
    }

    public void process(UpdateRequest request) {
        if (isMatch(request.remoteUri)) {
            download(request);
            try {
                Thread.currentThread().sleep(sleepInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    boolean isMatch(String uri) {
        URI target = URI.create(uri);
        return destination.getAuthority().equals(target.getAuthority());
    }

    public void download(UpdateRequest request) {
        HttpGet get = new HttpGet(request.remoteUri);
        try {

            final HttpResponse response = client.execute(get);
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                // FileOutputStream out = new FileOutputStream(request.localDestination);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                entity.writeTo(out);
                out.close();

                FileOutputStream local = new FileOutputStream(request.localDestination);
                local.write(out.toByteArray());
                local.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
