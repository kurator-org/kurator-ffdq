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
 * Created by lowery on 11/2/16.
 */
public class DQReportBuilder {
    private AssertionsConfig assertions;
    private BaseRecord result;
    private List<DQReport> reports = new ArrayList<>();

    public DQReportBuilder(InputStream config) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        assertions = mapper.readValue(config, AssertionsConfig.class);
    }

    public AssertionsConfig getAssertions() {
        return assertions;
    }

    public List<DQReport> createReport(BaseRecord result) {
        this.result = result;

        for (CurationStage stage : result.getCurationStages().values()) {
            DQReport report = processStage(stage);
            reports.add(report);
        }

        return reports;
    }

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
}
