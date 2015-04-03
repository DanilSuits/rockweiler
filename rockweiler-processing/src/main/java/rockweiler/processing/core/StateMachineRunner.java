/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.processing.core;

import com.github.oxo42.stateless4j.StateConfiguration;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class StateMachineRunner<S,T,C> implements TaskRunner<C>{
    private final Function<S,Function<? super C,T>> tasks;
    private final StateMachine<S,T> stateMachine;
    private final Predicate<? super S> checkFinished;

    public StateMachineRunner(Function<S, Function<? super C, T>> tasks, StateMachine<S, T> stateMachine, Predicate<? super S> checkFinished) {
        this.tasks = tasks;
        this.stateMachine = stateMachine;
        this.checkFinished = checkFinished;
    }

    public void run(C context) {
        S state = stateMachine.getState();
        while(! checkFinished.apply(state)) {
            Function<? super C, T> task = tasks.apply(state);
            T trigger = task.apply(context);
            stateMachine.fire(trigger);
            state = stateMachine.getState();
        }
    }

    public static class Builder<S,T,C> {
        private final ImmutableMap.Builder<S, Function<? super C, T>> taskMapper;
        private final StateMachineConfig<S,T> stateMachineConfig;
        private Supplier<S> initialState;
        private Predicate<? super S> endTest;

        Builder(ImmutableMap.Builder<S, Function<? super C, T>> taskMapper, StateMachineConfig<S, T> stateMachineConfig) {
            this.taskMapper = taskMapper;
            this.stateMachineConfig = stateMachineConfig;
        }

        Builder<S,T,C> self () {
            return this;
        }

        public Builder<S,T,C> fromInitialState(Supplier<S> state) {
            this.initialState = state;
            return self();
        }

        public Builder<S,T,C> toEndState(Predicate<? super S> endTest) {
            this.endTest = endTest;
            return self();
        }


        public StateConfiguration<S,T> configure(S state, Function<? super C, T> task) {
            taskMapper.put(state, task);
            return stateMachineConfig.configure(state);
        }

        public Supplier<TaskRunner<C>> build () {
            final Function<S, Function<? super C, T>> tasks = Functions.forMap(taskMapper.build());
            final Supplier<S> initialState = this.initialState;
            final Predicate<? super S> endTest = this.endTest;

            return new Supplier<TaskRunner<C>>() {

                public TaskRunner<C> get() {
                    StateMachine<S,T> machine = new StateMachine<S,T>(initialState.get(), stateMachineConfig);

                    return new StateMachineRunner(tasks, machine, endTest);
                }
            };

        }

        public static <S,T,I> Builder<S,T,I> builder() {

            return new Builder<S,T,I> (new ImmutableMap.Builder<S,Function<? super I, T>>(), new StateMachineConfig<S, T>());
        }

    }
}
