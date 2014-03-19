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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class Archive<T> {

    private final TempReportFactory reportFactory;
    ObjectMapper om = new ObjectMapper();
    PrettyPrinter printer = new MinimalPrettyPrinter() {
        @Override
        public void writeArrayValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
            super.writeArrayValueSeparator(jg);
            jg.writeRaw('\n');
        }

        @Override
        public void writeEndArray(JsonGenerator jg, int nrOfValues) throws IOException, JsonGenerationException {
            super.writeEndArray(jg, nrOfValues);
            jg.writeRaw('\n');
        }
    };

    public Archive(TempReportFactory reportFactory) {
        this.reportFactory = reportFactory;
   }

    public void save(Iterable<T> slots) {
        List draft = Lists.newArrayList(slots);

        try {

            OutputStream out = openCurrentReport();

            om.prettyPrintingWriter(printer).writeValue(out,draft);
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
