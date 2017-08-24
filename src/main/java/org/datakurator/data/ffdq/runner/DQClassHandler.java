package org.datakurator.data.ffdq.runner;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.datakurator.data.ffdq.Namespace;
import org.datakurator.data.ffdq.model.report.DataResource;
import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQAmendmentResponse;
import org.datakurator.ffdq.api.DQMeasurementResponse;
import org.datakurator.ffdq.api.DQValidationResponse;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.filteredpush.qc.date.DwCEventDQ;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by lowery on 8/1/17.
 */
public class DQClassHandler {
    private final Class cls;
    private String mechanism;

    private RDFBeanFactory rdfFactory;

    private List<AssertionTest> measures = new ArrayList<>();
    private List<AssertionTest> validations = new ArrayList<>();
    private List<AssertionTest> amendments = new ArrayList<>();

    public DQClassHandler(RDFBeanFactory factory, Class cls) {
        this.rdfFactory = factory;
        this.cls = cls;

        // Class level annotations
        for (Annotation annotation : cls.getDeclaredAnnotations()) {
            if (annotation.annotationType().equals(Mechanism.class)) {
                mechanism = ((Mechanism) annotation).value();
            }
        }

        // Method level annotations
        Method[] methods = cls.getDeclaredMethods();

        for (final Method method : methods) {
            if (method.isAnnotationPresent(Provides.class)) {
                processMethod(method);
            }
        }
    }

    private void processMethod(Method method) {
        String guid = method.getAnnotation(Provides.class).value();
        String specification = method.getAnnotation(Specification.class).value();

        List<Parameter> parameters = processParameters(method);
        AssertionTest test = new AssertionTest(guid, specification, mechanism, cls, method, parameters);

        // Process methods annotated as measures, validations, amendments
        if (method.isAnnotationPresent(Measure.class)) {
            Measure measure = method.getAnnotation(Measure.class);
            Dimension dimension = measure.dimension();

            String label = measure.label();
            String description = measure.description();

            rdfFactory.createMeasurementMethod(test, dimension.name());
            measures.add(test);
        } else if (method.isAnnotationPresent(Validation.class)) {
            Validation validation = method.getAnnotation(Validation.class);

            String label = validation.label();
            String description = validation.description();

            rdfFactory.createValidationMethod(test, description);
            validations.add(test);
        } else if (method.isAnnotationPresent(Amendment.class)) {
            Amendment amendment = method.getAnnotation(Amendment.class);

            String label = amendment.label();
            String description = amendment.description();

            rdfFactory.createAmendmentMethod(test, description);
            amendments.add(test);
        }
    }

    private List<Parameter> processParameters(Method method) {
        List<Parameter> params = new ArrayList<>();

        // Parameter level annotations
        Annotation[][] annotatedParams = method.getParameterAnnotations();

        for (Annotation[] annotatedParam : annotatedParams) {
            if (annotatedParam.length > 0) {

                for (Annotation annotation : annotatedParam) {
                    String term = null;

                    if (annotation.annotationType().equals(ActedUpon.class)) {
                        ActedUpon actedUpon = (ActedUpon) annotation;
                        term = actedUpon.value();

                    } else if (annotation.annotationType().equals(Consulted.class)) {
                        Consulted consulted = (Consulted) annotation;
                        term = consulted.value();
                    }

                    params.add(new Parameter(term));
                }
            }
        }

        return params;
    }

    public void run(Map<String, String> record) {
        try {
            Object instance = cls.newInstance();

            for (AssertionTest measure : measures) {
                DQMeasurementResponse response = (DQMeasurementResponse) measure.invoke(instance, record);
                rdfFactory.createMeasure(measure, record, response);
            }

            for (AssertionTest validation : validations) {
                DQValidationResponse response = (DQValidationResponse) validation.invoke(instance, record);
                rdfFactory.createValidation(validation, record, response);
            }

            for (AssertionTest amendment : amendments) {
                DQAmendmentResponse response = (DQAmendmentResponse) amendment.invoke(instance, record);
                rdfFactory.createAmendment(amendment, record, response);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, RDFBeanException {
        RDFBeanFactory factory = new RDFBeanFactory();

        DQClassHandler handler = new DQClassHandler(factory, DwCEventDQ.class);
        List<Map<String, String>> records = new LinkedList<Map<String, String>>();

        // Read csv file as a list of Map objects
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader().withoutQuoteChar().withColumnSeparator('\t');

        MappingIterator<Map<String, String>> iterator = mapper.reader(Map.class)
                .with(schema)
                .readValues(DQClassHandler.class.getResourceAsStream("/nine_molluscs.csv"));
        while (iterator.hasNext()) {
            records.add(iterator.next());
        }

        for (Map<String, String> record : records) {
            handler.run(record);
        }

        factory.write(RDFFormat.TURTLE, System.out);
    }
}
