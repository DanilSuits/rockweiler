/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.util.bktree;

import com.google.common.collect.Maps;
import rockweiler.util.similarity.Similarity;

import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class BKTree<T> {
    public interface Builder<T> {
        Builder insert(T rhs);
        BKTree build();
    }

    public static <T> Builder<T> createBuilder(Similarity<? super T> distance) {
        return new DefaultBuilder(distance);
    }

    final T value;
    final Map<Integer,BKTree<T>> children;

    protected BKTree(T value, Map<Integer, BKTree<T>> children) {
        this.value = value;
        this.children = children;
    }

    T getValue () {
        return value;
    }

    private static class DefaultBuilder<T> implements Builder<T> {
        private BKTree root = null;
        private final Similarity<? super T> distance;

        private DefaultBuilder(Similarity<? super T> distance) {
            this.distance = distance;
        }

        public Builder insert(T rhs) {
            if ( null == root ) {
                root = createNode(rhs);
            } else {
                insert(root,rhs);
            }

            return this;
        }

        public void insert(BKTree<T> tree, T rhs) {
            T lhs = tree.getValue();
            int score = distance.compare(lhs, rhs);
            while(tree.children.containsKey(score)) {
                tree = tree.children.get(score);
                score = distance.compare(tree.getValue(), rhs);
            }

            tree.children.put(score,createNode(rhs));

        }

        private BKTree createNode(T rhs) {
            return new BKTree(rhs, Maps.newHashMap());
        }

        public BKTree build() {
            return root;
        }

    }
}
