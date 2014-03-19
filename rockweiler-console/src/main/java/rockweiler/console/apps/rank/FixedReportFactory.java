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
public class FixedReportFactory implements ReportFactory {
    private final File report;

    public FixedReportFactory(File report) {
        this.report = report;
    }

    public OutputStream openReport() {
        try {
            return new FileOutputStream(report);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to open: " + report, e);
        }
    }
}
