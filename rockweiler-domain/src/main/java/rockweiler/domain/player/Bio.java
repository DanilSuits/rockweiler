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
public class Bio implements ValueObject<Bio> {
    private final Name name;
    private final DateOfBirth dob;

    public Bio(Name name, DateOfBirth dob) {
        this.name = name;
        this.dob = dob;
    }

    public boolean sameValueAs(Bio rhs) {
        return false;  //TODO: To change body of implemented methods use File | Settings | File Templates.
    }
}
