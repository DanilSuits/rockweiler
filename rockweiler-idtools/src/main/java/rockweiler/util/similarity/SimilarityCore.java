/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.util.similarity;

import rockweiler.util.bktree.BKTree;
import rockweiler.util.bktree.BasicNeighborhoodQuery;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class SimilarityCore<T> {
    public static <T> SimilarityDatabase<T> create(final Similarity<? super T> distance, Iterable<? extends T> candidates) {

        BKTree.Builder<T> builder = BKTree.createBuilder(distance);
        for (T crnt : candidates) {
            builder.insert(crnt);
        }

        final BKTree root = builder.build();

        final BasicNeighborhoodQuery.Builder<T> queryBuilder = BasicNeighborhoodQuery.Builder.<T>create(distance);

        return new SimilarityDatabase<T>() {
            public Iterable<? extends T> find(T target, int tolerance) {
                return queryBuilder.centeredAt(target).closerThan(tolerance).build().search(root);
            }
        };
    }
}
