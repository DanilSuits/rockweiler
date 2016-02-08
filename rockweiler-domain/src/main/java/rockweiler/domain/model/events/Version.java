/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.domain.model.events;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Version {
    public final int id;

    private Version(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    public Version next() {
        return Version.of(id + 1);
    }

    public static Version of(int id) {
        return new Version(id);
    }

    public static Version seed() {
        return Contants.SEED;
    }

    private static final class Contants {
        private static final Version SEED = new Version(-1);
    }

}
