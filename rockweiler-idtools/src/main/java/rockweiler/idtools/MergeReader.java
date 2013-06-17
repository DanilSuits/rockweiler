/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.idtools;

import rockweiler.idtools.player.PlayerCollector;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class MergeReader {
    private final DatabaseWriter master;
    private final DatabaseWriter missing;
    private final DatabaseWriter conflict;

    public MergeReader(DatabaseWriter master, DatabaseWriter missing, DatabaseWriter conflict) {
        this.master = master;
        this.missing = missing;
        this.conflict = conflict;
    }

    public void collect(PlayerMerge merge) {
        merge.collectMasterDatabase(master.collector());
        merge.collectMissingDatabase(missing.collector());
        merge.collectConflictDatabase(conflict.collector());
    }

    public void onEnd() {
        master.onEnd();
        missing.onEnd();
        conflict.onEnd();
    }
}
