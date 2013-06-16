/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.util.similarity;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface SimilarityDatabase<T> {
    Iterable<? extends T> find(T target, int tolerance);
}
