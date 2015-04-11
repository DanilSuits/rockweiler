/**
 * Copyright Vast 2015. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.apps.cqrs;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import rockweiler.console.core.modules.Startup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class CQRSDraft {
    public static void main(String[] args) throws Exception {
        Startup startup = Startup.create("cqrs");

        Config config = startup.readConfiguration(args);

        File commandLog = new File(System.getProperty("java.io.tmpdir"), config.getString("cqrs.command.log"));

        CommandJournal journal = new CommandJournal(new PrintStream(new FileOutputStream(commandLog)));

        final File startupLog = new File(config.getString("cqrs.startup.log"));
        Snapshot snapshot = new Snapshot(startupLog);
        snapshot.subscribe(journal);
    }

    interface Observer<T> {
        void onNext(T t);

        void onComplete();

        void onError();
    }

    static class Snapshot {
        private final File snapshotLog;
        private final ObjectMapper om = new ObjectMapper();

        Snapshot(File snapshotLog) {
            this.snapshotLog = snapshotLog;
        }

        public void subscribe(Observer<JsonNode> listener) {
            try {
                Scanner scanner = new Scanner(snapshotLog);
                while (scanner.hasNext()) {
                    String json = scanner.nextLine();
                    final JsonNode currentCommand = om.readTree(json);
                    listener.onNext(currentCommand);
                }
                scanner.close();
                listener.onComplete();
            } catch (IOException e) {
                listener.onError();
                throw new RuntimeException(snapshotLog.getAbsolutePath(), e);
            }
        }
    }

    static class CommandJournal implements Observer<JsonNode> {
        private final PrintStream out;
        final JsonFactory factory = new JsonFactory();
        final ObjectMapper om = new ObjectMapper();

        CommandJournal(PrintStream out) {
            this.out = out;
        }

        public void onNext(JsonNode jsonNode) {
            try {
                JsonGenerator json = factory.createGenerator(out);
                om.writeTree(json, jsonNode);
                out.println();

            } catch (IOException e) {
                throw new RuntimeException(out.toString(), e);
            }
        }

        public void onComplete() {
            out.flush();
        }

        public void onError() {

        }
    }
}
