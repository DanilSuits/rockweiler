/**
 * Copyright Vast 2016. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package rockweiler.console.spikes.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class PlayerDigestProjection implements PlayerPool<PlayerDigestProjection.Digest> {
    private final Supplier<? extends Iterable<Digest>> supplier;

    public PlayerDigestProjection(Supplier<? extends Iterable<Digest>> supplier) {
        this.supplier = supplier;
    }

    public List<Digest> query(String hint) {
        final Iterable<Digest> digests = supplier.get();

        String query = hint.toLowerCase();

        final List results = Lists.newArrayList();
        for(Digest digest : digests) {
            if (digest.id.contains(query)) {
                results.add(digest);
                continue;
            }

            if (digest.name.toLowerCase().contains(query)) {
                results.add(digest);
                continue;
            }

            if (digest.normalized.toLowerCase().contains(query)) {
                results.add(digest);
            }
        }
        return results;
    }

    public static class Digest {
        final String id;
        final String dob;
        final String name;
        final String normalized;

        public Digest(String id, String dob, String name, String normalized) {
            this.id = id;
            this.dob = dob;
            this.name = name;
            this.normalized = normalized;
        }

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(id);
            b.append(" ").append(dob);
            b.append(" ").append(name);
            return b.toString();
        }
    }

    public static class Factory {
        private final ObjectMapper om = new ObjectMapper();
        static final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

        List<Digest> parse(Scanner scanner) {
            List<Digest> results = Lists.newArrayList();

            while (scanner.hasNext()) {
                String json = scanner.nextLine();
                try {
                    JsonNode root = om.readTree(json);
                    String type = root.get("event_type").asText();
                    if ("PlayerReferenceDiscovered".equals(type)) {
                        JsonNode data = root.get("data");

                        String bbref = data.get("id").get("bbref").asText();
                        String dob = data.get("bio").get("dob").asText();
                        String name = data.get("bio").get("name").asText();
                        String normalized = stripAccents(name);

                        Digest crnt = new Digest(bbref, dob, name, normalized);
                        results.add(crnt);

                    }

                } catch (IOException e) {
                    throw new RuntimeException(json, e);
                }
            }
            return results;
        }

        private String stripAccents(String name) {
            // http://stackoverflow.com/questions/1008802/converting-symbols-accent-letters-to-english-alphabet
            String nfdNormalizedString = Normalizer.normalize(name, Normalizer.Form.NFD);
            String normalized = pattern.matcher(nfdNormalizedString).replaceAll("");

            return name.equals(normalized) ? name : normalized;
        }

    }
}
