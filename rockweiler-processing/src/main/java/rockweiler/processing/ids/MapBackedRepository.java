/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.processing.ids;

import rockweiler.processing.api.Bio;
import rockweiler.processing.api.ProvisionalBio;
import rockweiler.processing.store.Store;
import rockweiler.processing.store.Stores;

import java.util.Collection;

/**
* @author Danil Suits (danil@vast.com)
*/
class MapBackedRepository implements UpdateRepository {
    private final Store<String> referenceStore = Stores.newMapBackedStore();
    private final Store<ProvisionalBio> provisionalBioStore = Stores.newMapBackedStore();
    private final Store<Bio> bioStore = Stores.newMapBackedStore();
    private final Store<Collection<String>> idStore = Stores.newMapBackedStore();

    public Store<String> referenceStore() {
        return referenceStore;
    }

    public Store<ProvisionalBio> provisionalStore() {
        return provisionalBioStore;
    }

    public Store<Bio> bioStore() {
        return bioStore;
    }

    public Store<Collection<String>> idStore() {
        return idStore;
    }
}
