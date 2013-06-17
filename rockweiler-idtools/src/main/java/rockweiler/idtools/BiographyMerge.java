/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import rockweiler.player.BioReader;
import rockweiler.player.Biography;
import rockweiler.player.Player;
import rockweiler.player.PlayerBuilder;
import rockweiler.player.io.FileBackedStore;
import rockweiler.player.io.KeyStoreException;
import rockweiler.player.io.PlayerStore;
import rockweiler.util.similarity.Similarity;
import rockweiler.util.similarity.SimilarityCore;
import rockweiler.util.similarity.SimilarityDatabase;

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

        for (Player rhs : updateDatabase) {
            out.add(match(rhs));
        }

        return out;
    }

    Player match(Player rhs) {
        List<Player> results = Lists.newArrayList(database.find(rhs, 5));

        if (1 == results.size()) {
            Player goodBio = results.get(0);

            System.out.println(rhs.getBio().getName() + ".");
            System.out.println("\t" + goodBio.getBio().getName() + ".");

            PlayerBuilder builder = new PlayerBuilder();
            rhs = builder.withIds(rhs.getIds()).withBio(goodBio.getBio()).build();
        }

        return rhs;
    }

    public static void main(String[] args) throws KeyStoreException {
        String rootDatabase = "master.players.json";

        final PlayerStore playerStore = FileBackedStore.create("");
        PlayerStore.Reader in = playerStore.createReader();

        Iterable<? extends Player> core = in.readPlayers(rootDatabase);
        core = Iterables.filter(core, Biography.HAS_BIO_FILTER);

        BioReader idReader = new BioReader();
        final BioSimilarity similarity = new BioSimilarity(idReader);

        final SimilarityDatabase<Player> database = SimilarityCore.create(similarity, core);

        Iterable<? extends Player> update = in.readPlayers("bootstrap.missing.json");
        update = Iterables.filter(update, Biography.HAS_BIO_FILTER);

        BiographyMerge theMerge = new BiographyMerge(database, similarity);

        Iterable<Player> out = theMerge.match(update);
        playerStore.createWriter().writePlayers("biography.merged.json", out);
    }
}
