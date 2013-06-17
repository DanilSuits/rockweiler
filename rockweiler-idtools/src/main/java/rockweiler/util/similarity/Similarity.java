/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.util.similarity;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface Similarity<T> {
    int compare(T lhs, T rhs);
}
