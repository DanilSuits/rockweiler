/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rank;

import com.google.common.collect.Lists;
import rockweiler.console.core.MessageListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Replay {
    public static Replay create(File script) {
        return create(script,script);
    }

    public static Replay create(File script, File log) {
        List<String> commands = read(script);
        FixedReportFactory logFactory = new FixedReportFactory(log);
        ReplayLog nextReplay = new ReplayLog(logFactory);

        return new Replay(commands,nextReplay);
    }

    public Replay(List<String> commands, ReplayLog log) {
        this.commands = commands;
        this.log = log;
    }

    static List<String> read(File script) {
        List<String> commands = Lists.newArrayList();

        if (! script.exists()) {
            return commands;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(script));
            String line;
            while ((line = br.readLine()) != null) {
               commands.add(line);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read commands from " + script, e);
        }

        return commands;
    }

    private final List<String> commands;
    private final ReplayLog log;

    public void replay(MessageListener<String> listener) {
        for(String crnt : commands) {
            listener.onMessage(crnt);
        }
    }

    public MessageListener<String> getLog() {
        return log;
    }

    static class ReplayLog implements MessageListener<String> {

        private final List<String> history = Lists.newArrayList();
        private final ReportFactory reportFactory;

        ReplayLog(ReportFactory reportFactory) {
            this.reportFactory = reportFactory;
        }

        public void onMessage(String message) {
            history.add(message);
            try {
                OutputStream out = reportFactory.openReport();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                for (String crnt : history) {
                    writer.write(crnt);
                    writer.newLine();
                }
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("Unable to log message", e);
            }
       }
    }
}


