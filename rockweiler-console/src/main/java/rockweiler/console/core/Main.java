/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.core;

import rockweiler.console.core.lifecycle.*;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Main {
    public interface Body {
        void cycle();
    }

    private final RunningState state;
    private final Body theApp;

    public Main(RunningState state, Body theApp) {
        this.state = state;
        this.theApp = theApp;
    }

    public void run() {
        while(state.isRunning()) {
            theApp.cycle();
        }
    }
}
