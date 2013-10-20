/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler;

import com.google.common.collect.Lists;
import rockweiler.draft.Draft;
import rockweiler.draft.Slot;
import rockweiler.draft.TrivialDraft;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class SimpleDraftScript {
    public static Draft<String> createTrivialDraft() {
        List<Slot<String>> slots = Lists.newArrayList();
        for (int x = 0; x < 5; ++x) {
            slots.add(new Slot(x, null, Slot.BasicStates.PENDING, null));
        }

        return new TrivialDraft(slots);
    }

    public static void run(Demo demo) {
        demo.draft("Alice");
        demo.skip();
        demo.draft("Bob");
        demo.undo();
        demo.draft("Betty");

        demo.show();
    }
}
