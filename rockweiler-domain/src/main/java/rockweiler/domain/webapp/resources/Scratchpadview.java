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
    public Object dto;

    public Scratchpadview(Map<String,Object> dto) {
        super("scratchpad.ftl");
        this.dto = dto;
    }

    public Object getDto() {
        return dto;
    }
}
