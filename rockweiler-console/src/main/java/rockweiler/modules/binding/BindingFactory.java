/**
 * Copyright Vast 2013. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.modules.binding;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface BindingFactory<L,R> {
    R create(L lhs);
}
