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
public class UpdateRepositories {

    private UpdateRepositories () {}

    static UpdateRepository createLocalRepository () {
        final Store<String> referenceStore = Stores.newMapBackedStore();
        final Store<ProvisionalBio> provisionalBioStore = Stores.newMapBackedStore();
        final Store<Bio> bioStore = Stores.newMapBackedStore();
        final Store<Collection<String>> idStore = Stores.newMapBackedStore();

        return createRepository(referenceStore, provisionalBioStore, bioStore, idStore);
    }

    public static UpdateRepository createRepository(final Store<String> referenceStore, final Store<ProvisionalBio> provisionalBioStore, final Store<Bio> bioStore, final Store<Collection<String>> idStore) {
        return new UpdateRepository () {

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
        };
    }

    static UpdateRepository createClientRepository(UpdateRepository remote) {
        final Store<String> referenceStore = Stores.newDeltaStore(remote.referenceStore());
        final Store<ProvisionalBio> provisionalBioStore = Stores.newDeltaStore(remote.provisionalStore());
        final Store<Bio> bioStore = Stores.newDeltaStore(remote.bioStore());
        final Store<Collection<String>> idStore = Stores.newDeltaStore(remote.idStore());

        return createRepository(referenceStore, provisionalBioStore, bioStore, idStore);

    }
}
