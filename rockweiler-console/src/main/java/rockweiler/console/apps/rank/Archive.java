/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rank;

import com.google.common.collect.Lists;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.PrettyPrinter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.util.MinimalPrettyPrinter;
import rockweiler.player.jackson.SimpleArchive;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Archive<T> {

    private final TempReportFactory reportFactory;
    private final SimpleArchive<T> simpleArchive = new SimpleArchive<T> ();

    ObjectMapper om = new ObjectMapper();

    public Archive(TempReportFactory reportFactory) {
        this.reportFactory = reportFactory;
   }

    public void save(Iterable<T> slots) {
        List draft = Lists.newArrayList(slots);

        try {

            OutputStream out = openCurrentReport();
            simpleArchive.archive(slots,out);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to save report", e);
        }
    }

    private OutputStream openCurrentReport() throws FileNotFoundException {
        return reportFactory.openReport();
    }
}
