/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.core;

import java.io.PrintStream;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DumbTerminal implements MessageListener<String>, ExceptionListener {

    private final PrintStream out;
    private final PrintStream err;

    public DumbTerminal(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    public void onMessage(String message) {
        out.println(message);
    }

    public void onException(Exception e) {
        e.printStackTrace(err);
    }
}
