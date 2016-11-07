package org.kurator.data.ffdq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.kurator.data.ffdq.assertions.*;
import org.kurator.data.provenance.BaseRecord;
import org.kurator.data.provenance.CurationStep;
import org.kurator.data.provenance.GlobalContext;
import org.kurator.data.provenance.NamedContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 11/2/16.
 */
public class DQConfigParser {
    private AssertionsConfig assertions;
    private static DQConfigParser instance;

    private DQConfigParser() { }

    public static DQConfigParser getInstance() {
        if (instance == null) {
            instance = new DQConfigParser();
        }

        return instance;
    }

    public void load(InputStream config) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        assertions = mapper.readValue(config, AssertionsConfig.class);
    }

    public AssertionsConfig getAssertions() {
        return assertions;
    }

    public DQReport generateReport(BaseRecord result) {
        DQReport report = new DQReport();

        GlobalContext globalContext = result.getGlobalContext();
        StrSubstitutor contextProps = new StrSubstitutor(globalContext.getProperties());

        Map<NamedContext, List<CurationStep>> curationStepMap =
                result.getCurationHistoryContexts();

        for (NamedContext context : curationStepMap.keySet()) {
            Assertion assertion = assertions.forContext(context.getName());

            if (assertion instanceof Measure) {
                Measure measure = (Measure) assertion;

                String parsedMechanism = contextProps.replace(measure.getMechanism());
                measure.setMechanism(parsedMechanism);

                System.out.println("MEASURE: ");
                System.out.println("    dimension : " + measure.getDimension() + "\n    specification : "
                        + measure.getSpecification() + "\n    mechanism : " + measure.getMechanism());

                report.pushMeasure(measure);
            } else if (assertion instanceof Validation) {
                Validation validation = (Validation) assertion;

                String parsedMechanism = contextProps.replace(validation.getMechanism());
                validation.setMechanism(parsedMechanism);

                System.out.println("VALIDATION: ");
                System.out.println("    criterion : " + validation.getCriterion() + "\n    specification : "
                        + validation.getSpecification() + "\n    mechanism : " + validation.getMechanism());

                report.pushValidation(validation);
            } else if (assertion instanceof Improvement) {
                Improvement improvement = (Improvement) assertion;

                String parsedMechanism = contextProps.replace(improvement.getMechanism());
                improvement.setMechanism(parsedMechanism);

                System.out.println("IMPROVEMENT: ");
                System.out.println("    enhancement : " + improvement.getEnhancement() + "\n    specification : "
                        + improvement.getSpecification() + "\n    mechanism : " + improvement.getMechanism());

                report.pushImprovement(improvement);
            }
            System.out.println();

            System.out.println("    context : " + context.getName());
            if (!context.getFieldsActedUpon().isEmpty()) {
                System.out.println("    fieldsActedUpon : " + context.getFieldsActedUpon());
            }
            if (!context.getFieldsConsulted().isEmpty()) {
                System.out.println("    fieldsConsulted : " + context.getFieldsConsulted());
            }

            System.out.println();

            List<CurationStep> steps = curationStepMap.get(context);

            for (CurationStep step : steps) {
                System.out.print("    state: " + step.getCurationStatus() + "\n    comments: " + step.getCurationComments());
                System.out.println();
                System.out.println();
            }
        }

        return report;
    }

    public static void main(String[] args) throws IOException {
        DQConfigParser configParser = DQConfigParser.getInstance();

        configParser.load(DQConfigParser.class.getResourceAsStream("/ffdq-assertions.json"));
        System.out.println(configParser.getAssertions().getValidations());
    }
}
