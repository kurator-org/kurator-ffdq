package org.datakurator.postprocess;

import org.datakurator.data.ffdq.DQReport;
import org.datakurator.data.ffdq.DataResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lowery on 11/21/16.
 */
public class FFDQPostProcessor {
    public void postprocess(List<DQReport> reports) throws IOException {
        List<DataResource> data = new ArrayList<>();

        for (DQReport report : reports) {
            DataResource record = report.getDataResource();
            data.add(record);
        }

        DataTable xlsView = new DataTable(data);

        FileOutputStream out = new FileOutputStream("test.xls");
        xlsView.drawXls(out);
    }
}
