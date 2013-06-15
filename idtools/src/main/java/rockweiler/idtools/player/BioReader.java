/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools.player;

/**
* @author Danil Suits (danil@vast.com)
*/
public class BioReader implements IdReader {
    public String getId(Player p) {
        Player.Bio bio = p.getBio();
        String key = bio.getDob() + bio.getName();

        return key;
    }
}
