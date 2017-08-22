package org.datakurator.data.ffdq.runner;

import org.datakurator.data.ffdq.model.report.DataResource;
import org.datakurator.data.ffdq.model.solutions.Mechanism;
import org.datakurator.data.ffdq.model.solutions.Specification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lowery on 8/1/17.
 */
public class AssertionTest {
    private String guid;
    private String specification;
    private String mechanism;

    private Class cls;
    private Method method;
    private List<Parameter> parameters;

    public AssertionTest(String guid, String specification, String mechanism, Class cls, Method method, List<Parameter> parameters) {
        this.guid = guid;
        this.specification = specification;
        this.mechanism = mechanism;

        this.cls = cls;
        this.method = method;
        this.parameters = parameters;
    }

    public Object invoke(Object instance, Map<String, String> record) throws InvocationTargetException, IllegalAccessException {
        Set<String> keys = record.keySet();

        // Check that record contains required fields
        for (Parameter param : parameters) {
            if (!keys.contains(param.getTerm())) {
                // throw new RuntimeException("Record argument missing one or more required fields: " + parameters);
                // TODO: log warning instead
            }
        }

        // Test method arguments
        String[] args = new String[parameters.size()];

        // Map keys to method arguments by term name
        for (int i = 0; i < args.length; i++) {
            Parameter param = parameters.get(i);

            String value = record.get(param.getTerm());
            args[i] = value;
        }

        return method.invoke(instance, args);
    }

    public String getGuid() {
        return guid;
    }

    public String getSpecification() {
        return specification;
    }

    public String getMechanism() {
        return mechanism;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public String getClassName() {
        return cls.getName();
    }

    @Override
    public String toString() {
        return "AssertionTest{" +
                "guid='" + guid + '\'' +
                ", specification='" + specification + '\'' +
                ", mechanism='" + mechanism + '\'' +
                ", method=" + method +
                ", parameters=" + parameters +
                '}';
    }
}
