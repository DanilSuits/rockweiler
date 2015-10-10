/**
 * Copyright Vast 2015. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.player;

import rockweiler.domain.glossary.ValueObject;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Name implements ValueObject<Name> {
    private final String name;

    public Name(String name) {
        this.name = name;
    }

    public boolean sameValueAs(Name rhs) {
        return false;
    }
}
