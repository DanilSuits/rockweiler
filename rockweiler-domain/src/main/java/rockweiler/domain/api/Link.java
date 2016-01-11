/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Link {
    public String rel;
    public String uri;

    public Link(String rel, String uri) {
        this.rel = rel;
        this.uri = uri;
    }

    public String getRel() {
        return rel;
    }

    public String getUri() {
        return uri;
    }
}
