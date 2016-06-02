/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import rockweiler.console.core.DumbTerminal;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TerminalView implements ConsoleView {
    private final DumbTerminal terminal;

    public TerminalView(DumbTerminal terminal) {
        this.terminal = terminal;
    }

    public void onStatus(String status) {
        terminal.onMessage(status);
    }

    public void onPlayers(List players) {
        for(Object player : players) {
            terminal.onMessage(player.toString());
        }
    }
}
