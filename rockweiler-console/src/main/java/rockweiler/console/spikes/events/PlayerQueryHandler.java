/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class PlayerQueryHandler implements UserInputHandler {
    private final ConsoleView view;
    private final PlayerPool pool;

    public PlayerQueryHandler(ConsoleView view, PlayerPool pool) {
        this.view = view;
        this.pool = pool;
    }

    public void onInput(String userInput) {
        view.onPlayers(pool.query(userInput));
    }
}
