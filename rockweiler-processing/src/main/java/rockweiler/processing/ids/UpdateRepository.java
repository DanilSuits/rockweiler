/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.processing.ids;

import rockweiler.processing.api.Bio;
import rockweiler.processing.api.ProvisionalBio;
import rockweiler.processing.store.Store;

import java.util.Collection;

/**
* @author Danil Suits (danil@vast.com)
*/
public interface UpdateRepository {
    Store<String> referenceStore();
    Store<ProvisionalBio> provisionalStore();
    Store<Bio> bioStore();

    // TODO: collectionsStore?
    // because I need some place to put
    // ... /rockweiler/players/all
    // ... /rockweiler/bio/all
    // ... /rockweiler/remotes/espn/all
    Store<Collection<String>> idStore();

    // TODO: DateStore?
}
