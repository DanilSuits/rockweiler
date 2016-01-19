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
    public final String json;

    public String getJson () {
        return json;
    }

    public DashboardView(String json) {
        super("dashboard.ftl");
        this.json = json;
    }
}
