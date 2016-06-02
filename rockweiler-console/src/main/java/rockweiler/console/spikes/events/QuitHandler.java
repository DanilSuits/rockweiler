/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import rockweiler.console.core.lifecycle.RunningState;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class QuitHandler implements UserInputHandler {
    private final RunningState runningState;

    public QuitHandler(RunningState runningState) {
        this.runningState = runningState;
    }

    public void onInput(String userInput) {
        if (Parser.QUIT.equals(userInput)) {
            runningState.shutdown();
        }
    }

    private static final class Parser {
        private static final String QUIT = "quit";
    }
}
