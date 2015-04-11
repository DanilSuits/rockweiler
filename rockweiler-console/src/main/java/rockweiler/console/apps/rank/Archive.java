/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import rockweiler.player.jackson.SimpleArchive;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Archive<T> {

    private final ReportFactory reportFactory;
    private final SimpleArchive<T> simpleArchive = new SimpleArchive<T> ();

    ObjectMapper om = new ObjectMapper();

    public Archive(ReportFactory reportFactory) {
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
