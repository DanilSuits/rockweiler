package rockweiler.idtools;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import rockweiler.player.*;
import rockweiler.player.database.DatabaseFactory;
import rockweiler.player.io.FileBackedStore;
import rockweiler.player.io.KeyStoreException;
import rockweiler.player.io.PlayerStore;
import rockweiler.player.jackson.Schema;
import rockweiler.player.jackson.SimpleArchive;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Danil
 * Date: 3/30/14
 * Time: 11:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class RepositoryMerge {
    private final Map<String, Schema.Player> master;
    private final Map<String, Schema.Player> inserts = Maps.newHashMap();

    private final List<Schema.Player> conflictPlayers = Lists.newArrayList();

    private final IdReader idReader;

    public RepositoryMerge(Map<String, Schema.Player> master, IdReader idReader) {
        this.master = master;
        this.idReader = idReader;
    }

    public void merge(Iterable<? extends Schema.Player> updateDatabase) {
        for (Schema.Player rhs : updateDatabase) {
            String key = idReader.getId(rhs);

            if (master.containsKey(key)) {
                Schema.Player lhs = master.get(key);
                merge(lhs,rhs);
            } else {
                if (inserts.containsKey(key)) {
                    Schema.Player lhs = inserts.get(key);
                    merge(lhs, rhs);
                } else {
                    inserts.put(key,rhs);
                }
            }
        }
    }

    private void merge(Schema.Player knownPlayer, Schema.Player rhs) {
        try {

            Set<Map.Entry<String,String>> idUpdates = rhs.id.entrySet();
            for(Map.Entry<String,String> entry : idUpdates) {
                String verifiedId = knownPlayer.id.get(entry.getKey());
                if (null != verifiedId) {
                    if (! verifiedId.equals(entry.getValue())) {
                        throw new IdConflictException(entry.getKey());
                    }
                }
            }

            for(Map.Entry<String,String> entry : idUpdates) {
                knownPlayer.id.put(entry.getKey(),entry.getValue());
            }

        } catch (IdConflictException e) {
            conflictPlayers.add(rhs);
        }
    }


    private static final Predicate<Schema.Player> REJECTED = new Predicate<Schema.Player>() {
        public boolean apply(Schema.Player player) {
            return player.id.size() < 2;
        }
    };

    public List<? extends Schema.Player> collectMasterDatabase() {
        return Lists.newArrayList(master.values());
    }

    public List<? extends Schema.Player> collectInsertDatabase() {
        return Lists.newArrayList(Iterables.filter(inserts.values(), Predicates.not(REJECTED)));
    }

    public List<? extends Schema.Player> collectMissingDatabase() {
        return Lists.newArrayList(Iterables.filter(inserts.values(), REJECTED));
    }

    public List<? extends Schema.Player> collectConflictDatabase() {
        return conflictPlayers;
    }

    public static void main(String[] args) throws KeyStoreException, IOException {
        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");

        Iterable<? extends Schema.Player> players = Iterables.filter(repository.getPlayers(), RepositoryUpdate.HAS_BIO);

        IdReader idReader = new BioReader();
        Map<String, Schema.Player> idMap = Maps.newHashMap();

        for (Schema.Player p : players) {
            String key = idReader.getId(p);
            idMap.put(key, p);
        }

        RepositoryMerge theMerge = new RepositoryMerge(idMap, idReader);

        String updates[] =
                {
                        "update.missing.json"
                };

        for (String updateDatabase : updates) {
            File source = new File(updateDatabase);
            FileInputStream inputStream = new FileInputStream(source);

            Iterable<Schema.Player> update = JacksonPlayerRepository.create(inputStream).getPlayers();
            update = Iterables.filter(update, RepositoryUpdate.HAS_BIO);
            theMerge.merge(update);
        }

        SimpleArchive<Schema.Player> archive = new SimpleArchive<Schema.Player>();
        RepositoryUpdate.ReportGenerator reportGenerator = new RepositoryUpdate.ReportGenerator(archive);

        reportGenerator.write("repository.merged.json", theMerge.collectMasterDatabase());
        reportGenerator.write("repository.insert.json", theMerge.collectInsertDatabase());
        reportGenerator.write("repository.missing.json", theMerge.collectMissingDatabase());
        reportGenerator.write("repository.conflict.json", theMerge.collectConflictDatabase());


    }
}
