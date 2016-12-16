/**  DQReport.java
 *
 * Copyright 2016 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datakurator.data.ffdq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.datakurator.data.ffdq.assertions.DQReportStage;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Data qaulity report consists of stages that contain ffdq assertions
 *
 * @author lowery
 */
public class DQReport {
    private String recordId;

    private List<DQReportStage> stages = new ArrayList<>();

    public DQReport() { }

    public DQReport(String recordId) {
        this.recordId = recordId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public List<DQReportStage> getStages() {
        return stages;
    }

    public void setStages(List<DQReportStage> stages) {
        this.stages = stages;
    }

    public void addStage(DQReportStage reportStage) {
        stages.add(reportStage);
    }

    /**
     * Serialize the report as json
     *
     * @param writer
     * @throws IOException
     */
    public void write(Writer writer) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.writerWithDefaultPrettyPrinter().writeValue(writer, this);
    }
}
