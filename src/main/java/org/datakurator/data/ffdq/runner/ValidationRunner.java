package org.datakurator.data.ffdq.runner;

import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Consulted;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.api.DQValidation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 12/14/16.
 */

public class ValidationRunner {
    private static final int ACTED_UPON = 0;
    private static final int CONSULTED = 1;

    private Class cls;
    private List<ValidationTest> tests = new ArrayList<>();

    public ValidationRunner(Class cls) {
        this.cls = cls;
        processMethods();
    }

    private void processParameters(ValidationTest test) {
        Method method = test.getMethod();

        Annotation[][] annotatedParams = test.getMethod().getParameterAnnotations();

        for (Annotation[] annotatedParam : annotatedParams) {
            if (annotatedParam.length > 0) {
                ValidationParam param = new ValidationParam();

                for (Annotation annotation : annotatedParam) {
                    if (annotation.annotationType().equals(ActedUpon.class)) {
                        ActedUpon actedUpon = (ActedUpon) annotation;
                        param.setTerm(actedUpon.value());
                        param.setUsage(ACTED_UPON);
                    } else if (annotation.annotationType().equals(Consulted.class)) {
                        Consulted consulted = (Consulted) annotation;
                        param.setTerm(consulted.value());
                        param.setUsage(CONSULTED);
                    }

                    test.addParam(param);
                }
            }
        }
    }

    private void processMethods() {
        Method[] methods = cls.getDeclaredMethods();

        for (final Method method : methods) {
            if (method.isAnnotationPresent(Provides.class)) {
                // Parse method annotations
                Provides provides = method.getAnnotation(Provides.class);

                ValidationTest test = new ValidationTest(provides.value(), method);
                tests.add(test);

                // Parse parameter annotations
                processParameters(test);
            }

        }
        //System.out.println(tests);
    }

    public void validate(Map<String, String> record) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object instance = cls.newInstance();

        for (ValidationTest test : tests) {
            List<ValidationParam> inputs = test.getInputs();
            String[] args = new String[inputs.size()];

            for (int i = 0; i < args.length; i++) {
                ValidationParam input = inputs.get(i);
                String term = input.getTerm();
                String value = record.get(term);

                args[i] = value;
            }

            DQValidation retVal = (DQValidation) test.getMethod().invoke(instance, args);

            System.out.println("Ran test " + test.getName() + " using method " + test.getMethod().getName() + ": " + retVal.getResultState() + " | " + retVal.getComment());
        }
    }

}
