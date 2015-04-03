/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.processing.ids;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.jersey.api.uri.UriTemplate;
import rockweiler.processing.api.Bio;
import rockweiler.processing.api.Id;
import rockweiler.processing.api.ProvisionalBio;
import rockweiler.processing.core.StateMachineRunner;
import rockweiler.processing.core.TaskRunner;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Set;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class UpdateIdProcess {
    private final Supplier<TaskRunner<Instance>> instanceFactory;

    public UpdateIdProcess(Supplier<TaskRunner<Instance>> instanceFactory) {
        this.instanceFactory = instanceFactory;
    }

    public void onUpdate(Id id, Bio bio) {
        Instance crnt = new Instance();
        crnt.updateId = id;
        crnt.updateBio = bio;

        runInstance(crnt);
    }

    private void runInstance(Instance crnt) {
        TaskRunner<Instance> runner = instanceFactory.get();
        runner.run(crnt);
    }

    static class Instance {
        Id updateId = null;
        Bio updateBio = null;

        // Find all the references!
        String valueBio = null;
        String keyBioPlayers = null;

        String valueId = null;
        String keyIdBio = null;
        String keyIdPlayer = null;


        // Find VerifiedPlayer
        String valuePlayer = null;
        String keyPlayerRemotes = null;

        // Provision changes
        String provisionalLocation = null;
        ProvisionalBio provisionalBio = null;

        UpdateRepository updates = null;

        void setPlayer(String player) {
            this.valuePlayer = player;
            this.keyPlayerRemotes =
                    null == player
                    ? null
                    : valuePlayer + "/remotes";
        }
    }

    enum State {
        FIND_ID_LOCATION,
        FIND_BIO_LOCATION,
        FIND_VERIFIED_PLAYER,

        FIND_VERIFIED_BIO,
        FIND_PROVISIONAL_BIO,

        ADD_PROVISIONAL_CHANGE,
        PROMOTE_PROVISIONAL_CHANGE,

        PREPARE_UPDATE,
        ACCEPT_UPDATE,
        UPDATE_STORES,
        END,
    }

    enum Trigger {
        FOUND_BIO_LOCATION,
        FOUND_ID_LOCATION,

        VERIFIED_PLAYER_FOUND,
        VERIFIED_PLAYER_MISSING,
        VERIFIED_PLAYER_AMBIGUOUS,

        UPDATE_PREPARED,
        CHANGES_COMPLETED,

        PROVISIONAL_CHANGE_FOUND,
        PROVISIONAL_CHANGE_MISSING,
        PROVISIONAL_CHANGE_WAITING,

        STORES_UPDATED,
    }

    interface Task extends Function<Instance, Trigger> {
    }

    static class FindBioLocation implements Task {
        private final Function<Instance, String> locateBio;
        private final Function<Instance, String> locateBio2Player;

        FindBioLocation(Function<Instance, String> locateBio, Function<Instance, String> locateBio2Player) {
            this.locateBio = locateBio;
            this.locateBio2Player = locateBio2Player;
        }

        public Trigger apply(UpdateIdProcess.Instance instance) {
            instance.valueBio = locateBio.apply(instance);
            instance.keyBioPlayers = locateBio2Player.apply(instance);

            return Trigger.FOUND_BIO_LOCATION;
        }
    }

    static class FindIdLocation implements Task {
        private final Function<Instance, String> locateId;
        private final Function<Instance, String> locateId2Bio;
        private final Function<Instance, String> locateId2Player;

        FindIdLocation(Function<Instance, String> locateId, Function<Instance, String> locateId2Bio, Function<Instance, String> locateId2Player) {
            this.locateId = locateId;
            this.locateId2Bio = locateId2Bio;
            this.locateId2Player = locateId2Player;
        }

        public Trigger apply(UpdateIdProcess.Instance input) {
            input.valueId = locateId.apply(input);
            input.keyIdBio = locateId2Bio.apply(input);
            input.keyIdPlayer = locateId2Player.apply(input);

            return Trigger.FOUND_ID_LOCATION;
        }
    }

    static class PrepareUpdate implements Task {
        private final Function<Instance, ? extends Collection<String>> readVerifiedIds;
        private final Function<Instance, UpdateRepository> getCurrentUpdates;

        PrepareUpdate(Function<Instance, ? extends Collection<String>> readVerifiedIds, Function<Instance, UpdateRepository> getCurrentUpdates) {
            this.readVerifiedIds = readVerifiedIds;
            this.getCurrentUpdates = getCurrentUpdates;
        }

        public Trigger apply(Instance input) {
            UpdateRepository updates = getCurrentUpdates.apply(input);
            Set<String> ids = Sets.newHashSet(readVerifiedIds.apply(input));
            ids.add(input.valueId);

            updates.idStore().insert(input.keyPlayerRemotes, ids);
            input.updates = updates;

            return Trigger.UPDATE_PREPARED;
        }
    }

    static class AcceptUpdate implements Task {
        public Trigger apply(Instance input) {
            //
            UpdateRepository updates = input.updates;

            updates.referenceStore().insert(input.keyIdBio, input.valueBio);
            updates.referenceStore().insert(input.keyIdPlayer, input.valuePlayer);

            Set<String> allMatches = Sets.newHashSet(updates.idStore().get(input.keyPlayerRemotes));
            allMatches.add(input.valueId);

            updates.idStore().insert(input.keyPlayerRemotes, allMatches);

            return Trigger.CHANGES_COMPLETED;
        }
    }


    static final String BIO_STEM = "/rockweiler/bio/dob/{dob}/name/{name}";

    static final UriTemplate bioLocationTemplate = new UriTemplate(BIO_STEM);
    static final UriTemplate bio2ProvisionalTemplate = new UriTemplate(BIO_STEM + "/provisional");
    static final UriTemplate bio2Players = new UriTemplate(BIO_STEM + "/players");

    static final Function<Bio, String> CONVERT_BIO_TO_LOCATION = new BioToUriFunction(bioLocationTemplate);
    static final Function<Bio, String> CONVERT_BIO_TO_PLAYERS = new BioToUriFunction(bio2Players);

    static final Function<Bio, String> CONVERT_BIO_TO_PROVISIONAL_CHANGE = new BioToUriFunction(bio2ProvisionalTemplate);

    static final Function<String,String> ENCODER = new Function<String, String>() {
        public String apply(String input) {
            try {
                input = URLEncoder.encode(input, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                //ignored;
            }

            return input;
        }
    };

    static class BioToUriFunction implements Function<Bio,String> {
        private final UriTemplate conversionTemplate;

        BioToUriFunction(UriTemplate conversionTemplate) {
            this.conversionTemplate = conversionTemplate;
        }


        public String apply(Bio input) {
            return conversionTemplate.createURI(input.dob, ENCODER.apply(input.name));
        }
    }
    static final Function<Instance, Bio> GET_UPDATE_BIO = new Function<Instance, Bio>() {
        public Bio apply(Instance instance) {
            return instance.updateBio;
        }
    };

    static final String PLAYER_ROOT = "/rockweiler/players/{id}";
    static final UriTemplate PLAYER_ID_TEMPLATE = new UriTemplate(PLAYER_ROOT);
    static final UriTemplate PLAYER_BIO_TEMPLATE = new UriTemplate(PLAYER_ROOT + "/bio");

    static final String ID_STEM = "/rockweiler/remotes/{key}/{id}";

    static final UriTemplate idLocationTemplate = new UriTemplate(ID_STEM);
    static final UriTemplate idLocation2BioTemplate = new UriTemplate(ID_STEM + "/bio");
    static final UriTemplate idLocation2PlayerTemplate = new UriTemplate(ID_STEM + "/player");

    static final Function<Id, String> CONVERT_ID_TO_LOCATION = new IdToUriFunction(idLocationTemplate);
    static final Function<Id, String> CONVERT_ID_TO_BIO = new IdToUriFunction(idLocation2BioTemplate);
    static final Function<Id, String> CONVERT_ID_TO_PLAYER = new IdToUriFunction(idLocation2PlayerTemplate);


    static class IdToUriFunction implements Function<Id, String> {
        private final UriTemplate conversionTemplate;

        IdToUriFunction(UriTemplate conversionTemplate) {
            this.conversionTemplate = conversionTemplate;
        }


        public String apply(Id input) {
            return conversionTemplate.createURI(input.key, input.id);
        }
    }

    static final Function<Instance, Id> GET_UPDATE_ID = new Function<Instance, Id>() {
        public Id apply(rockweiler.processing.ids.UpdateIdProcess.Instance input) {
            return input.updateId;
        }
    };

    static class FindVerifiedPlayer implements Task {
        private final Function<Instance, String> locateVerifiedPlayer;

        FindVerifiedPlayer(Function<Instance, String> locateVerifiedPlayer) {
            this.locateVerifiedPlayer = locateVerifiedPlayer;
        }

        public Trigger apply(Instance input) {
            input.setPlayer(locateVerifiedPlayer.apply(input));

            return null == input.valuePlayer
                    ? Trigger.VERIFIED_PLAYER_MISSING
                    : Trigger.VERIFIED_PLAYER_FOUND
                    ;
        }
    }

    static class FindVerifiedBio implements Task {
        private final Function<Instance, Collection<String>> locateIdsForBio;

        FindVerifiedBio(Function<Instance, Collection<String>> locateIdsForBio) {
            this.locateIdsForBio = locateIdsForBio;
        }

        public Trigger apply(Instance input) {
            Collection<String> ids = locateIdsForBio.apply(input);

            if ( null == ids || ids.isEmpty()) {
                return Trigger.VERIFIED_PLAYER_MISSING;
            }

            if ( ids.size() > 1) {
                return Trigger.VERIFIED_PLAYER_AMBIGUOUS;
            }

            input.setPlayer(ids.iterator().next());
            return Trigger.VERIFIED_PLAYER_FOUND;
        }
    }

    static class FindProvisionalUpdate implements Task {
        private final Function<Instance, String> locateProvisionalReference;
        private final Function<Instance, ProvisionalBio> lookupProvisionalChange;

        FindProvisionalUpdate(Function<Instance, String> locateProvisionalReference, Function<Instance, ProvisionalBio> lookupProvisionalChange) {
            this.locateProvisionalReference = locateProvisionalReference;
            this.lookupProvisionalChange = lookupProvisionalChange;
        }

        public Trigger apply(Instance input) {
            input.provisionalLocation = locateProvisionalReference.apply(input);
            input.provisionalBio = lookupProvisionalChange.apply(input);

            if (null == input.provisionalBio) {
                return Trigger.PROVISIONAL_CHANGE_MISSING;
            }

            return input.valueId.equals(input.provisionalBio.valueId)
                    ? Trigger.PROVISIONAL_CHANGE_WAITING
                    : Trigger.PROVISIONAL_CHANGE_FOUND
                    ;
        }
    }

    static final Function<Instance, String> READ_UPDATE_ID_TO_PLAYER = new Function<Instance, String>() {
        public String apply(Instance input) {
            return input.keyIdPlayer;
        }
    };

    static final Function<Instance, String> READ_UPDATE_BIO_TO_PLAYERS = new Function<Instance, String>() {
        public String apply(Instance input) {
            return input.keyBioPlayers;
        }
    };

    static final Function<Instance, String> READ_VERIFIED_PLAYER_REMOTES = new Function<Instance, String>() {
        public String apply(Instance input) {
            return input.keyPlayerRemotes;
        }
    };

    static final Function<Instance, ProvisionalBio> CREATE_PROVISIONAL_CHANGE = new Function<Instance, ProvisionalBio>() {
        public ProvisionalBio apply(Instance input) {
            ProvisionalBio change = new ProvisionalBio();
            change.valueId = input.valueId;
            change.keyIdBio = CONVERT_ID_TO_BIO.apply(input.updateId);
            change.keyIdPlayer = CONVERT_ID_TO_PLAYER.apply(input.updateId);

            change.valueBio = input.valueBio;
            change.keyBioPlayers = input.keyBioPlayers;

            change.bio = input.updateBio;

            return change;
        }
    };

    static class AddProvisionalChange implements Task {
        private final Function<Instance, ProvisionalBio> createProvisionalChange;
        private final Function<Instance, UpdateRepository> getCurrentUpdates;

        AddProvisionalChange(Function<Instance, ProvisionalBio> createProvisionalChange, Function<Instance, UpdateRepository> getCurrentUpdates) {
            this.createProvisionalChange = createProvisionalChange;
            this.getCurrentUpdates = getCurrentUpdates;
        }

        public Trigger apply(Instance input) {
            input.provisionalBio = createProvisionalChange.apply(input);

            UpdateRepository updates = getCurrentUpdates.apply(input);
            updates.provisionalStore().insert(input.provisionalLocation, input.provisionalBio);

            input.updates = updates;
            return Trigger.CHANGES_COMPLETED;
        }
    }

    static class UpdateStores implements Task {
        private final UpdateRepository repository;

        UpdateStores(UpdateRepository repository) {
            this.repository = repository;
        }

        public Trigger apply(Instance input) {
            UpdateRepository updates = input.updates;
            updates.referenceStore().writeTo(repository.referenceStore());
            updates.bioStore().writeTo(repository.bioStore());
            updates.idStore().writeTo(repository.idStore());
            updates.provisionalStore().writeTo(repository.provisionalStore());

            return Trigger.STORES_UPDATED;
        }
    }

    static class PromoteProvisionalChange implements Task {
        private final Supplier<String> playerIdFactory;
        private final Function<Instance, UpdateRepository> getCurrentUpdates;

        PromoteProvisionalChange(Supplier<String> playerIdFactory, Function<Instance, UpdateRepository> getCurrentUpdates) {
            this.playerIdFactory = playerIdFactory;
            this.getCurrentUpdates = getCurrentUpdates;
        }

        public Trigger apply(Instance input) {

            String id = playerIdFactory.get();
            input.setPlayer(PLAYER_ID_TEMPLATE.createURI(id));

            final ProvisionalBio provisionalBio = input.provisionalBio;

            UpdateRepository updates = getCurrentUpdates.apply(input);

            // Delete the entry from the provisional store - we no longer need it.
            updates.provisionalStore().insert(input.provisionalLocation, null);

            // Insert a new entry into the bioStore
            updates.bioStore().insert(provisionalBio.valueBio, provisionalBio.bio);

            // Create a new entry in the idStore
            updates.idStore().insert(input.keyPlayerRemotes, Sets.newHashSet(provisionalBio.valueId));
            updates.idStore().insert(provisionalBio.keyBioPlayers, Sets.newHashSet(input.valuePlayer));


            // Create new entries in the referenceStore
            updates.referenceStore().insert(PLAYER_BIO_TEMPLATE.createURI(id), provisionalBio.valueBio);
            updates.referenceStore().insert(provisionalBio.keyIdBio, provisionalBio.valueBio);
            updates.referenceStore().insert(provisionalBio.keyIdPlayer, input.valuePlayer);

            input.updates = updates;
            return Trigger.UPDATE_PREPARED;

        }
    }

    static class V1Builder {
        private final UpdateRepository repository;

        V1Builder(UpdateRepository repository) {
            this.repository = repository;
        }


        private Function<Instance, Collection<String>> lookupVerifiedPlayers(Function<Instance, String> lookupRef) {
            return Functions.compose( new Function<String, Collection<String>> () {
                public Collection<String> apply(String input) {
                    return repository.idStore().get(input);
                }
            } , lookupRef ) ;
        }

        private Function<Instance, String> getVerifiedPlayerReader(Function<Instance, String> lookupRef) {
            return Functions.compose(new Function<String, String>() {
                public String apply(String input) {
                    return repository.referenceStore().get(input);
                }
            }, lookupRef);
        }



        public Supplier<TaskRunner<Instance>> build() {

            StateMachineRunner.Builder<State, Trigger, Instance> builder = StateMachineRunner.Builder.builder();

            normalizeInput(builder);
            findVerifiedPlayer(builder);

            findProvisionalUpdate(builder);

            promoteProvisional(builder);

            applyChanges(builder);

            builder.fromInitialState(Suppliers.ofInstance(State.FIND_BIO_LOCATION))
                    .toEndState(Mocks.TRIVIAL_END_CHECK);


            return builder.build();

        }

        void applyChanges(StateMachineRunner.Builder<State,Trigger,Instance> builder) {
            builder.configure(State.UPDATE_STORES, new UpdateStores(repository))
                    .permit(Trigger.STORES_UPDATED, State.END);
        }

        void promoteProvisional(StateMachineRunner.Builder<State, Trigger, Instance> builder) {
            builder.configure(State.PROMOTE_PROVISIONAL_CHANGE, new PromoteProvisionalChange(Mocks.PLAYER_ID_FACTORY, Mocks.GET_CURRENT_UPDATES))
                    .permit(Trigger.UPDATE_PREPARED, State.ACCEPT_UPDATE);
        }

        void findProvisionalUpdate(StateMachineRunner.Builder<State, Trigger, Instance> builder) {
            Function<Instance, String> locateProvisionalReference = Functions.compose(CONVERT_BIO_TO_PROVISIONAL_CHANGE, GET_UPDATE_BIO);
            Function<Instance, ProvisionalBio> lookupProvisionalChange = Mocks.LOOKUP_PROVISIONAL_CHANGE;
            builder.configure(State.FIND_PROVISIONAL_BIO, new FindProvisionalUpdate(locateProvisionalReference, lookupProvisionalChange))
                .permit(Trigger.PROVISIONAL_CHANGE_FOUND, State.PROMOTE_PROVISIONAL_CHANGE)
                .permit(Trigger.PROVISIONAL_CHANGE_MISSING, State.ADD_PROVISIONAL_CHANGE)
                .permit(Trigger.PROVISIONAL_CHANGE_WAITING, Mocks.IGNORE_REDUNDANT_UPDATE);

            builder.configure(State.ADD_PROVISIONAL_CHANGE, new AddProvisionalChange(CREATE_PROVISIONAL_CHANGE, Mocks.GET_CURRENT_UPDATES))
                    .permit(Trigger.CHANGES_COMPLETED, State.UPDATE_STORES);

        }

        void findVerifiedPlayer(StateMachineRunner.Builder<State, Trigger, Instance> builder) {
            // Check to see if we have already mapped the input id to a player
            final Function<Instance, String> lookupVerifiedPlayer = getVerifiedPlayerReader(READ_UPDATE_ID_TO_PLAYER);
            builder.configure(State.FIND_VERIFIED_PLAYER, new FindVerifiedPlayer(lookupVerifiedPlayer))
                    .permit(Trigger.VERIFIED_PLAYER_FOUND, Mocks.IGNORE_REDUNDANT_UPDATE)
                    .permit(Trigger.VERIFIED_PLAYER_MISSING, State.FIND_VERIFIED_BIO);

            builder.configure(State.FIND_VERIFIED_BIO, new FindVerifiedBio(lookupVerifiedPlayers(READ_UPDATE_BIO_TO_PLAYERS)))
                    .permit(Trigger.VERIFIED_PLAYER_FOUND, State.PREPARE_UPDATE)
                    .permit(Trigger.VERIFIED_PLAYER_MISSING, State.FIND_PROVISIONAL_BIO)
                    .permit(Trigger.VERIFIED_PLAYER_AMBIGUOUS, Mocks.IGNORE_AMBIGUOUS_UPDATE);

            builder.configure(State.PREPARE_UPDATE, new PrepareUpdate(lookupVerifiedPlayers(READ_VERIFIED_PLAYER_REMOTES), Mocks.GET_CURRENT_UPDATES))
                    .permit(Trigger.UPDATE_PREPARED, State.ACCEPT_UPDATE);

            builder.configure(State.ACCEPT_UPDATE, new AcceptUpdate())
                    .permit(Trigger.CHANGES_COMPLETED, State.UPDATE_STORES);
        }

        void normalizeInput(StateMachineRunner.Builder<State, Trigger, Instance> builder) {
            // Biographical Information is the key to identifying the player.  Specifically, figuring out whether we
            // already know who this person is.  So the process must begin with a unique identifier for the
            // biographical data
            final Function<Instance, String> getBioLocation = Functions.compose(CONVERT_BIO_TO_LOCATION, GET_UPDATE_BIO);
            final Function<Instance, String> getBioPlayers = Functions.compose(CONVERT_BIO_TO_PLAYERS, GET_UPDATE_BIO);
            builder.configure(State.FIND_BIO_LOCATION, new FindBioLocation(getBioLocation, getBioPlayers))
                    .permit(Trigger.FOUND_BIO_LOCATION, State.FIND_ID_LOCATION);

            // We also want to be able to track the different data sources, so that we can readily interact with those
            // systems.  So we need to create the identifier for the player in this data source.
            final Function<Instance, String> createIdLocation = Functions.compose(CONVERT_ID_TO_LOCATION, GET_UPDATE_ID);
            final Function<Instance, String> createIdToBioLocation = Functions.compose(CONVERT_ID_TO_BIO, GET_UPDATE_ID);
            final Function<Instance, String> createIdToPlayerLocation = Functions.compose(CONVERT_ID_TO_PLAYER, GET_UPDATE_ID);
            builder.configure(State.FIND_ID_LOCATION, new FindIdLocation(createIdLocation, createIdToBioLocation, createIdToPlayerLocation))
                    .permit(Trigger.FOUND_ID_LOCATION, State.FIND_VERIFIED_PLAYER);

        }

        public static V1Builder builder(UpdateRepository repository) {
            return new V1Builder(repository);
        }
    }

    static class Mocks {
        static final Function<Instance, ProvisionalBio> LOOKUP_PROVISIONAL_CHANGE = new Function<Instance, ProvisionalBio>() {
            public ProvisionalBio apply(Instance input) {
                UpdateRepository updates = GET_CURRENT_UPDATES.apply(input);
                return updates.provisionalStore().get(input.provisionalLocation);
            }
        };

        static Supplier<String> PLAYER_ID_FACTORY = new Supplier<String>() {
            long currentId = 100000L;
            public String get() {
                return String.format("%08d", currentId++);
            }
        };


        static Function<Instance, UpdateRepository> GET_CURRENT_UPDATES = new Function<Instance, UpdateRepository>() {
            private final UpdateRepository updates = new MapBackedRepository();

            public UpdateRepository apply(Instance input) {
                return updates;
            }
        };

        static final Predicate<State> TRIVIAL_END_CHECK = new Predicate<State>() {
            public boolean apply(rockweiler.processing.ids.UpdateIdProcess.State input) {
                return State.END.equals(input);
            }
        };

        static final State IGNORE_REDUNDANT_UPDATE = State.END;
        static final State IGNORE_AMBIGUOUS_UPDATE = State.END;
    }

}
