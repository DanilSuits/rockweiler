/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.model.domain;

import com.vocumsineratio.domain.Value;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Hint implements Value<Hint> {
    public final String hint;

    public Hint(String hint) {
        this.hint = hint;
    }

    @Override
    public boolean isSameValue(Hint other) {
        return hint.equals(other.hint);
    }
}
