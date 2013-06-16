/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.util.bktree;

import com.google.common.collect.Lists;
import rockweiler.util.similarity.Similarity;

import java.util.Collection;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class BasicNeighborhoodQuery<T> {
    public static class Builder<T> {
        public static <T> Builder create(Similarity<? super T> distance) {
            return new Builder<T>(distance);
        }

        private final Similarity<? super T> distance;
        private T center;
        private int tolerance;

        Builder(Similarity<? super T> distance) {
            this.distance = distance;
        }

        public Builder<T> centeredAt(T center) {
            this.center = center;
            return this;
        }

        public Builder<T> closerThan(int tolerance) {
            this.tolerance = tolerance;
            return this;
        }

        public BasicNeighborhoodQuery<T> build() {
            return new BasicNeighborhoodQuery<T>(distance,center,tolerance);
        }
    }

    public Collection<T> search(BKTree<? extends T> root) {
        List<T> neighbors = Lists.newArrayList();

        findNeighbors(root, neighbors);

        return neighbors;
    }

    private void findNeighbors(BKTree<? extends T> root, List<? super T> neighbors) {
        T rhs = root.getValue();
        int crntDistance = distance.compare(center, rhs);
        if (crntDistance < tolerance) {
            neighbors.add(rhs);
        }

        for (int x = crntDistance - tolerance; x <= crntDistance + tolerance; x++) {
            if (root.children.containsKey(x)) {
                findNeighbors(root.children.get(x), neighbors);
            }
        }
    }


    private final Similarity<? super T> distance;
    private final T center;
    private final int tolerance;

    private BasicNeighborhoodQuery(Similarity<? super T> distance, T center, int tolerance) {
        this.distance = distance;
        this.center = center;
        this.tolerance = tolerance;
    }

}
