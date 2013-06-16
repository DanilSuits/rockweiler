/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import org.apache.commons.lang.StringUtils;
import rockweiler.idtools.player.BioReader;
import rockweiler.idtools.player.Player;
import rockweiler.util.similarity.Similarity;

/**
* @author Danil Suits (danil@vast.com)
*/
public class BioSimilarity implements Similarity<Player> {
    private final BioReader idReader;

    public BioSimilarity(BioReader idReader) {
        this.idReader = idReader;
    }

    public int compare(Player lhs, Player rhs) {
        return compare( idReader.getId(lhs), idReader.getId(rhs));
    }

    private int compare(String lhs, String rhs) {
        return StringUtils.getLevenshteinDistance(lhs, rhs);
    }
}
