/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class BootStrap {
    public static void main(String[] args) {
        String externalSources[] = {
                "espn.players.json"
                , "lahman.players.json"
                , "mlb.players.json"
                , "oliver.players.json"
                , "oool.players.json"
                , "rotoworld.players.json"
                , "yahoo.players.json"

        };

        // First pass - read through all of the bios
        // to select which will be considered valid

        // Second pass - read the players, and merge
        // the players with valid bios.

        // Write the results.
    }
}
