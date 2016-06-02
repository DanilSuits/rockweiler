/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import jline.ConsoleReader;
import rockweiler.console.core.DumbTerminal;
import rockweiler.console.core.Main;
import rockweiler.console.core.lifecycle.RunningState;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DraftConsole implements Main.Body {
    private final ConsoleReader reader;
    private final DraftClient client;
    private final DumbTerminal terminal;

    public DraftConsole(ConsoleReader reader, DraftClient client, DumbTerminal terminal) {
        this.reader = reader;
        this.client = client;
        this.terminal = terminal;
    }

    public void cycle() {

        try {
            String userInput = reader.readLine();
            if (null != userInput) {
                client.onInput(userInput);
            }
        } catch (IOException e) {
            terminal.onException(e);
        }
    }

    public static void main(final String[] args) throws IOException {
        ConsoleReader reader = new ConsoleReader();
        DumbTerminal terminal = new DumbTerminal(System.out, System.err);

        final RunningState runningState = RunningState.start();

        TerminalView view = new TerminalView(terminal);

        final Supplier<InputStream> poolEventStream = new Supplier<InputStream>() {
            final String source = args[0];

            public InputStream get() {
                try {
                    return new FileInputStream(source);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(source, e);
                }
            }
        };

        Supplier<? extends Iterable<PlayerDigestProjection.Digest>> supplier = new Supplier<Iterable<PlayerDigestProjection.Digest>>() {
            final PlayerDigestProjection.Factory factory = new PlayerDigestProjection.Factory();

            public Iterable<PlayerDigestProjection.Digest> get() {
                InputStream in = poolEventStream.get();
                Scanner scanner = new Scanner(in);

                try {
                    return factory.parse(scanner);
                } finally {
                    scanner.close();
                }
            }
        };

        PlayerDigestProjection projection = new PlayerDigestProjection(supplier);

        List<UserInputHandler> handlers = Lists.newArrayList();
        handlers.add(new PlayerQueryHandler(view, projection));
        handlers.add(new QuitHandler(runningState));

        DraftClient client = new DraftClient(handlers);
        DraftConsole draftConsole = new DraftConsole(reader, client, terminal);

        Main theApp = new Main(runningState, draftConsole);

        theApp.run();
    }
}
