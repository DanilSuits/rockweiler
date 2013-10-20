/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.core.lifecycle;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class RunningState implements Shutdown{
    public static RunningState start() {
        return new RunningState();
    }

    private boolean isRunning = true;

    public boolean isRunning() {
        return isRunning;
    }

    public void shutdown() {
        isRunning = false;
    }
}
