/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.processing.ids;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import rockweiler.processing.core.StateMachineRunner;
import rockweiler.processing.core.TaskRunner;

import java.util.Collection;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class CorrectionProcess {
    private final Supplier<TaskRunner<Instance>> instanceFactory;

    public CorrectionProcess(Supplier<TaskRunner<Instance>> instanceFactory) {
        this.instanceFactory = instanceFactory;
    }

    public void correct(String from, String to) {
        Instance instance = new Instance();
        instance.removeFrom.bio = from;
        instance.mergeTo.bio = to;

        runInstance(instance);
    }

    private void runInstance(Instance crnt) {
        TaskRunner<Instance> runner = instanceFactory.get();
        runner.run(crnt);
    }

    static class Instance {
        Entry removeFrom = new Entry();
        Entry mergeTo = new Entry();

        UpdateRepository updates = new MapBackedRepository();

        static class Entry {
            String bio;
            String id;
        }
    }

    static enum State {
        LOOKUP_FROM,
        LOOKUP_TO,
        UPDATE_STORES,
        END
    }

    static enum Trigger {
        NO_PLAYER_MATCHED,
        AMBIGUOUS_MATCH,
        UNIQUE_MATCH,
        STORES_UPDATED
    }

    interface Task extends Function<Instance, Trigger> {
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

            return Trigger.STORES_UPDATED;
        }
    }

    static class V1Builder {
        private final UpdateRepository repository;

        V1Builder(UpdateRepository repository) {
            this.repository = repository;
        }

        void applyChanges(StateMachineRunner.Builder<State,Trigger,Instance> builder) {
            builder.configure(State.UPDATE_STORES, new UpdateStores(repository))
                    .permit(Trigger.STORES_UPDATED, State.END);
        }

        public Supplier<TaskRunner<Instance>> build() {

            StateMachineRunner.Builder<State, Trigger, Instance> builder = StateMachineRunner.Builder.builder();

            applyChanges(builder);

            return builder.build();

        }

        public static V1Builder builder(UpdateRepository repository) {
            return new V1Builder(repository);
        }

    }
}
