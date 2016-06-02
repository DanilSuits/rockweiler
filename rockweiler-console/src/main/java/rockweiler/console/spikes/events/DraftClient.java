/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DraftClient {
    private final List<UserInputHandler> handlers;

    public DraftClient(List<UserInputHandler> handlers) {
        this.handlers = handlers;
    }

    public void onInput(String userInput) {
        for(UserInputHandler handler : handlers) {
            handler.onInput(userInput);
        }
    }

}
