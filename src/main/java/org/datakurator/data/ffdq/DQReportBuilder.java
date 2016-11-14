/**  DQReportBuilder.java
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
import org.apache.commons.lang3.text.StrSubstitutor;
import org.datakurator.data.ffdq.assertions.*;
import org.datakurator.data.provenance.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder utility class provides a bridge between the data model used during analysis and assertions defined using
 * FFDQ. Generates a report that contains the FFDQ assertions.
 *
 * @author lowery
 */
public class DQReportBuilder {
    private AssertionsConfig assertions;
    private BaseRecord result;
    private List<DQReport> reports = new ArrayList<>();

    /**
     * Constructor loads definitions from config.
     *
     * @param config
     * @throws IOException
     */
    public DQReportBuilder(InputStream config) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        assertions = mapper.readValue(config, AssertionsConfig.class);
    }

    /**
     * Produce a summary of data quality reports that correspond to curation stages.
     *
     * @param result
     * @return list of reports
     */
    public List<DQReport> createReport(BaseRecord result) {
        this.result = result;

        for (CurationStage stage : result.getCurationStages().values()) {
            DQReport report = processStage(stage);
            reports.add(report);
        }

        return reports;
    }

    /**
     * Private helper method processes a curation stage and creates a report.
     *
     * @param stage
     * @return data quality report
     */
    private DQReport processStage(CurationStage stage) {
        DQReport report = new DQReport(stage.getStageClassifier());

        Map<NamedContext, List<CurationStep>> curationStepMap =
                stage.getCurationHistory();

        for (NamedContext context : curationStepMap.keySet()) {

            // Add context properties
            Map<String, String> contextProps = new HashMap<>();
            GlobalContext globalContext = result.getGlobalContext();
            contextProps.putAll(globalContext.getProperties());

            Assertion assertion = assertions.forContext(context.getName());
            contextProps.putAll(context.getProperties());

            // String substitution
            StrSubstitutor sub = new StrSubstitutor(contextProps);

            String specification = sub.replace(assertion.getSpecification());
            String mechanism = sub.replace(assertion.getMechanism());

            assertion.setSpecification(specification);
            assertion.setMechanism(mechanism);

            // Set context
            assertion.setContext(new Context(context.getName(), context.getFieldsActedUpon(), context.getFieldsConsulted()));

            // Combine curation steps
            List<CurationStep> curationSteps = curationStepMap.get(context);

            Map<String, String> initialValues = curationSteps.get(0).getInitialElementValues();
            Map<String, String> curatedValues = new HashMap<>();
            List<String> comments = new ArrayList<>();
            CurationStatus status = curationSteps.get(curationSteps.size()-1).getCurationStatus();

            for (CurationStep step : curationSteps) {
                curatedValues.putAll(step.getFinalElementValues());
                comments.addAll(step.getCurationComments());
            }

            if (assertion instanceof Measure) {
                Measure measure = (Measure) assertion;

                String dimension = sub.replace(measure.getDimension());
                measure.setDimension(dimension);

                // Create result
                Result dataResource = new Result(initialValues, curatedValues, comments, status.toString());
                assertion.setResult(dataResource);

                for (CurationStep step : curationSteps) {
                    System.out.println(step);
                }

                report.pushMeasure(measure);
            } else if (assertion instanceof Validation) {
                Validation validation = (Validation) assertion;

                String criterion = sub.replace(validation.getCriterion());
                validation.setCriterion(criterion);

                // Create result
                Result dataResource = new Result(initialValues, curatedValues, comments, status.toString());
                assertion.setResult(dataResource);

                report.pushValidation(validation);
            } else if (assertion instanceof Improvement) {
                Improvement improvement = (Improvement) assertion;

                String enhancement = sub.replace(improvement.getEnhancement());
                improvement.setEnhancement(enhancement);

                Result dataResource = new Result(initialValues, curatedValues, comments, status.toString());
                assertion.setResult(dataResource);

                report.pushImprovement(improvement);
            }
        }

        return report;
    }

    public AssertionsConfig getAssertions() {
        return assertions;
    }
}
