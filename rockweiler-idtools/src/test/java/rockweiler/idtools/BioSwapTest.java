/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import org.testng.Assert;
import org.testng.annotations.Test;
import rockweiler.player.Player;
import rockweiler.player.PlayerBuilder;
import rockweiler.player.jackson.JsonPlayerFactory;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class BioSwapTest {

    @Test
    public void swapBio() {
        // {"id":{"lahman":"milonto01"},"bio":{"name":"Tom Milone","dob":"19870216"}
        // {"id":{"espn":"31706"},"bio":{"dob":"19870216","name":"Tommy Milone"}}

        JsonPlayerFactory factory = new JsonPlayerFactory();

        Player lhs = factory.toPlayer("{\"id\":{\"espn\":\"31706\"},\"bio\":{\"dob\":\"19870216\",\"name\":\"Tommy Milone\"}}");
        Player.Bio src = lhs.getBio();

        Player rhs = factory.toPlayer("{\"id\":{\"lahman\":\"milonto01\"},\"bio\":{\"name\":\"Tom Milone\",\"dob\":\"19870216\"}}");

        PlayerBuilder builder = new PlayerBuilder();

        Player result = builder.withBio(src).withIds(rhs.getIds()).build() ;
        Assert.assertEquals(result.getIds().get("lahman"),"milonto01","The id is wrong");
        Assert.assertEquals(result.getBio().getDob(), "19870216", "DOB got corrupted");
        Assert.assertEquals(result.getBio().getName(), "Tommy Milone","The name didn't get replaced");




    }
}
