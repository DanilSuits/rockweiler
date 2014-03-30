/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.jline;

import jline.ConsoleReader;
import rockweiler.console.core.ExceptionListener;
import rockweiler.console.core.MessageListener;
import rockweiler.console.core.Main;

import java.io.IOException;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class UserInput implements Main.Body {
    private final ConsoleReader reader;
    private final MessageListener<String> listener;
    private final ExceptionListener error;

    public UserInput(ConsoleReader reader, MessageListener<String> listener, ExceptionListener error) {
        this.reader = reader;
        this.listener = listener;
        this.error = error;
    }

    public void cycle() {
        try {
            String userInput = reader.readLine();
            if (null != userInput) {
                listener.onMessage(userInput);
            }
        } catch (IOException e) {
            error.onException(e);
        }

    }
}
