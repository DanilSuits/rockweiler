/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.webapp.resources;

import io.dropwizard.views.View;
import rockweiler.domain.api.Link;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DashboardView extends View {
    public final List<Link> links;

    public String getTitle () {
        return "Dashboard";
    }

    public List<Link> getLinks() {
        return links;
    }

    public DashboardView(List<Link> links) {
        super("dashboard.ftl");
        this.links = links;
    }
}
