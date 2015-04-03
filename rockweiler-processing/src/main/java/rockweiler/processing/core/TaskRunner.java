/**
 * Copyright Vast 2015. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.processing.core;

/**
* @author Danil Suits (danil@vast.com)
*/
public interface TaskRunner<I> {
    void run(I instance);
}
