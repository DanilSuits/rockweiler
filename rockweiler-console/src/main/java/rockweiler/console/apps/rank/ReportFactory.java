/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rank;

import java.io.OutputStream;

/**
 * @author Danil Suits (danil@vast.com)
 */
public interface ReportFactory {
    OutputStream openReport();
}
