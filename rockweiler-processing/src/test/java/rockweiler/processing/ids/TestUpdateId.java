/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.processing.ids;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.uri.UriTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;
import rockweiler.processing.api.Bio;
import rockweiler.processing.api.Id;
import rockweiler.processing.api.ProvisionalBio;
import rockweiler.processing.core.TaskRunner;
import rockweiler.processing.store.Stores;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TestUpdateId {

    @Test
    public void testLegacyIdMatching() {
        UpdateRepository repository = new MapBackedRepository();
        final Supplier<TaskRunner<UpdateIdProcess.Instance>> taskRunnerFactory = UpdateIdProcess.V1Builder.builder(repository).build();
        UpdateIdProcess process = new UpdateIdProcess(taskRunnerFactory);
        Bio testBio = new Bio("Aaron Northcraft", "19900528");

        Id espn = new Id("espn", "32602");
        Id mlb = new Id("mlb", "573062");
        Id unknown = new Id("unknown", "9999999");

        process.onUpdate(espn, testBio);
        process.onUpdate(mlb, testBio);
        process.onUpdate(unknown, testBio);

        process.onUpdate(unknown, testBio);

        Assert.assertEquals(repository.idStore().get("/rockweiler/players/00100000/remotes").size(), 3);
    }

    @Test
    public void testDatabaseLoad() throws IOException {
        InputStream is = getClass().getResourceAsStream("/master.player.json");

        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<LegacyPlayer> players = om.readValue(is, SCHEMA_PLAYER_REPO);

        UpdateRepository repository = new MapBackedRepository();
        final Supplier<TaskRunner<UpdateIdProcess.Instance>> taskRunnerFactory = UpdateIdProcess.V1Builder.builder(repository).build();
        UpdateIdProcess process = new UpdateIdProcess(taskRunnerFactory);

        readPlayers(players, process);

        is.close();

        String database[] =
                {"corrections.players.json"
                        , "espn.players.json"
                        , "lahman.players.json"
                        , "mlb.players.json"
                        , "mlb.prospects.json"
                        , "rotoworld.players.json"
                        , "rotoworld.database.json"
                        , "yahoo.players.json"};

        File root = new File("/Users/Danil/Dropbox/OOOL/data/2015/database");
        for (String db : database) {
            is = new FileInputStream(new File(root, db));
            InputStreamReader stream = new InputStreamReader(is, Charset.forName("UTF-8"));

            players = om.readValue(is, SCHEMA_PLAYER_REPO);
            is.close();
            try {
                readPlayers(players, process);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // TODO:
        // merge("/rockweiler/bio/dob/19910326/name/Michael+Taylor","/rockweiler/bio/dob/19910326/name/Michael+A.+Taylor");
        // merge("/rockweiler/bio/dob/19860724/name/Suk-Min+Yoon","/rockweiler/bio/dob/19860724/name/Suk-min+Yoon");
        // merge("/rockweiler/bio/dob/19871125/name/Nate+Karns","/rockweiler/bio/dob/19871125/name/Nathan+Karns");
        // merge("/rockweiler/bio/dob/19911007/name/Michael+Foltynewicz","/rockweiler/bio/dob/19911007/name/Mike+Foltynewicz");
        // merge("/rockweiler/bio/dob/19880308/name/Thomas+Pham","/rockweiler/bio/dob/19880308/name/Tommy+Pham");
        // merge("/rockweiler/bio/dob/19891012/name/Francisco+Pe%C3%B1a","/rockweiler/bio/dob/19891012/name/Francisco+Pena");
        // merge("/rockweiler/bio/dob/19870405/name/Jung-ho+Kang","/rockweiler/bio/dob/19870405/name/Jung+Ho+Kang");
        // merge("/rockweiler/bio/dob/19920816/name/Delino+DeShields+Jr.","/rockweiler/bio/dob/19920816/name/Delino+DeShields");
        // merge("/rockweiler/bio/dob/19921019/name/Samuel+Tuivailala","/rockweiler/bio/dob/19921019/name/Sam+Tuivailala");
        // merge("/rockweiler/bio/dob/19920131/name/Alexander+Claudio","/rockweiler/bio/dob/19920131/name/Alex+Claudio");

        final Supplier<TaskRunner<CorrectionProcess.Instance>> correctionTaskFactory = CorrectionProcess.V1Builder.builder(repository).build();
        CorrectionProcess correction = new CorrectionProcess(correctionTaskFactory);

        // correction("/rockweiler/bio/dob/19870709/name/Rusney+Castillo","/rockweiler/bio/dob/19870907/name/Rusney+Castillo")
        // correction("/rockweiler/bio/dob/19861220/name/Alex+Guerrero",/rockweiler/bio/dob/19861120/name/Alex+Guerrero);
        // correction("/rockweiler/bio/dob/19910809/name/Steven+Moya","/rockweiler/bio/dob/19910908/name/Steven+Moya");
        // correction("/rockweiler/bio/dob/19830610/name/Radhames+Liz","/rockweiler/bio/dob/19831006/name/Radhames+Liz");


        final Map<String, ProvisionalBio> provisionalSummary = Maps.newHashMap();
        repository.provisionalStore().writeTo(Stores.newAccumulatingStore(provisionalSummary));


        final Map<String, Collection<String>> knownIds = Maps.newHashMap();
        repository.idStore().writeTo(Stores.newMapBackedStore(knownIds));

        final Map<String, String> references = Maps.newHashMap();
        repository.referenceStore().writeTo(Stores.newMapBackedStore(references));

        UriTemplate playerId = new UriTemplate("/rockweiler/players/{id}/remotes");
        UriTemplate playerBio = new UriTemplate("/rockweiler/players/{id}/bio");

        UriTemplate dobRoot = new UriTemplate("/rockweiler/bio/dob/{dob}/name/{name}");
        UriTemplate dobPlayers = new UriTemplate("/rockweiler/bio/dob/{dob}/name/{name}/players");

        Map<String, String> urlData = Maps.newHashMap();

        int lostSheep = 0;

        for (Map.Entry<String, Collection<String>> entry : knownIds.entrySet()) {
            String key = entry.getKey();
            urlData.clear();
            if (playerId.match(key, urlData)) {
                boolean found = false;
                for (String remote : entry.getValue()) {
                    if (remote.contains("/mlb/")) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    final String mlbBio = playerBio.createURI(urlData.get("id"));
                    final String mlbBioValue = references.get(mlbBio);

                    Map<String, String> bioData = Maps.newHashMap();
                    if (dobRoot.match(mlbBioValue, bioData)) {

                        if ("19891231".compareTo(bioData.get("dob")) > 0) {
                            continue;
                        }

                        System.out.println(mlbBioValue);
                        System.out.println("\t" + key);

                        for (String remote : entry.getValue()) {
                            System.out.println("\t\t" + remote);
                        }


                        for (Map.Entry<String, Collection<String>> idEntry : knownIds.entrySet()) {
                            if (idEntry.getKey().startsWith(mlbBioValue)) {
                                continue;
                            }

                            Map<String, String> otherBio = Maps.newHashMap();

                            if (dobPlayers.match(idEntry.getKey(), otherBio)) {
                                if (bioData.get("dob").equals(otherBio.get("dob")) || bioData.get("name").equals(otherBio.get("name"))) {
                                    System.out.println("\t" + idEntry.getKey());
                                    for (String candidate : idEntry.getValue()) {
                                        System.out.println("\t\t" + candidate);
                                    }
                                }
                            }
                        }

                    }

                    lostSheep++;
                }
            }

        }

        System.out.println(lostSheep);
    }

    private void readPlayers(List<LegacyPlayer> players, UpdateIdProcess process) {
        for (LegacyPlayer player : players) {
            Bio crntBio = new Bio(player.bio.name, player.bio.dob);
            for (Map.Entry<String, String> entry : player.id.entrySet()) {
                Id id = new Id(entry.getKey(), entry.getValue());

                process.onUpdate(id, crntBio);
            }
        }
    }

    public static class LegacyPlayer {
        public TreeMap<String, String> id;
        public Bio bio;

        public static class Bio {
            public String name;
            public String dob;
        }
    }

    public static final TypeReference<List<LegacyPlayer>> SCHEMA_PLAYER_REPO = new TypeReference<List<LegacyPlayer>>() {
    };


    public static class SearchResult {
        public String name;
        public String url;
    }

    public static final TypeReference<List<SearchResult>> SCHEMA_SEARCH_RESULTS = new TypeReference<List<SearchResult>>() {
    };

    static class SearchMapping {
        public List<ProvisionalBio> triggeredBy = Lists.newArrayList();
        public SearchResult searchResult;
    }
}
