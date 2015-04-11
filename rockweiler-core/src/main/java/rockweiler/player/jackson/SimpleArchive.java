/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.player.jackson;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class SimpleArchive<T> {
    static final PrettyPrinter PRETTY_PRINTER = new MinimalPrettyPrinter() {
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

    private final ObjectMapper om = new ObjectMapper();

    public void archive(Iterable<? extends T> repo, OutputStream out) throws IOException {
        archive(Lists.newArrayList(repo), out);
    }

    public void archive(List<T> repo, OutputStream out) throws IOException {
        om.writer(PRETTY_PRINTER).writeValue(out,repo);
    }

}
