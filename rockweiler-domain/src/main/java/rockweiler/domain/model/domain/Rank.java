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
public class Rank implements Value<Rank> {
    public final int id;

    private Rank(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    public static Rank of(int id) {
        return new Rank(id);
    }

    public static Rank parse(String id) {
        return of(Integer.valueOf(id));
    }

    @Override
    public boolean isSameValue(Rank other) {
        return id == other.id;
    }
}
