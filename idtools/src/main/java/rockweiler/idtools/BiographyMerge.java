/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import rockweiler.idtools.player.BioReader;
import rockweiler.idtools.player.Player;
import rockweiler.idtools.player.PlayerBuilder;
import rockweiler.idtools.player.Predicates;
import rockweiler.util.similarity.Similarity;
import rockweiler.util.similarity.SimilarityCore;
import rockweiler.util.similarity.SimilarityDatabase;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class BiographyMerge {
    private final SimilarityDatabase<Player> database;
    private final Similarity<? super Player> similarity;

    public BiographyMerge(SimilarityDatabase<Player> database, Similarity<? super Player> similarity) {
        this.database = database;
        this.similarity = similarity;
    }

    public Iterable<Player> match(Iterable<? extends Player> updateDatabase) {
        Collection<Player> out = Lists.newArrayList();

        for(Player rhs : updateDatabase) {
            out.add(match(rhs));
        }

        return out;
    }

    Player match(Player rhs) {
        List<Player> results = Lists.newArrayList(database.find(rhs, 5));

        if (1 == results.size()) {
            Player goodBio = results.get(0);

            int distance = similarity.compare(goodBio,rhs);
            if ( distance < 3 ) {
                PlayerBuilder builder = new PlayerBuilder();
                rhs = builder.withIds(rhs.getIds()).withBio(goodBio.getBio()).build();
            }
        }

        return rhs;
    }

    public static void main(String[] args) throws IOException {
        String rootDatabase = "master.players.json";
        Iterable<Player> core = DatabaseFactory.createDatabase(rootDatabase);
        core = Iterables.filter(core, Predicates.HAS_BIO);

        BioReader idReader = new BioReader();
        final BioSimilarity similarity = new BioSimilarity(idReader);

        final SimilarityDatabase<Player> database = SimilarityCore.create(similarity,core);

        Iterable<Player> update = DatabaseFactory.createDatabase("bootstrap.missing.json");
        update = Iterables.filter(update, Predicates.HAS_BIO);

        BiographyMerge theMerge = new BiographyMerge(database, similarity);

        Iterable<Player> out = theMerge.match(update);

        DatabaseWriter mergedOut = DatabaseFactory.createWriter("biography.merged.json");
        mergedOut.collector().collectAll(out);
        mergedOut.onEnd();
    }
}
