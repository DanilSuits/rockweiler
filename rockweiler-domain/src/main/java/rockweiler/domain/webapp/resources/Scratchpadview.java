/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.webapp.resources;

import io.dropwizard.views.View;
import rockweiler.domain.api.Link;
import rockweiler.domain.api.ScratchRanking;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Scratchpadview extends View {
    public ScratchRanking ranking;
    public List<Link> links;

    public Scratchpadview(ScratchRanking ranking, List<Link> links) {
        super("scratchpad.ftl");
        this.ranking = ranking;
        this.links = links;
    }

    public ScratchRanking getRanking() {
        return ranking;
    }

    public List<Link> getLinks() {
        return links;
    }
}
