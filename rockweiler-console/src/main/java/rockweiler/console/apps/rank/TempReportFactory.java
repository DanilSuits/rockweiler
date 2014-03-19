/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.rank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TempReportFactory {

    public OutputStream openReport() {
        File reportRoot = new File(System.getProperty("java.io.tmpdir"));
        File report = new File(reportRoot,String.valueOf(System.currentTimeMillis()));

        try {
            return new FileOutputStream(report);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("unable to open report " + report,e);
        }

    }

}
