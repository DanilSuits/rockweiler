/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.webapp.resources;

import io.dropwizard.views.View;

import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Scratchpadview extends View {
    public Object links;

    public Scratchpadview(Map<String,String> links) {
        super("scratchpad.ftl");
        this.links = links;
    }

    public Object getLinks() {
        return links;
    }
}
