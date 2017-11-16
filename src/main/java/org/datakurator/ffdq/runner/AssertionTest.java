package org.datakurator.ffdq.runner;

import org.datakurator.ffdq.runner.Parameter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lowery on 11/16/17.
 */
public class AssertionTest {
    private String guid;
    private String label;
    private String description;
    private String specification;
    private String assertionType;
    private String resourceType;
    private String dimension;
    private List<String> informationElement;

    // TODO: use these in runner?
    private Class cls;
    private Method method;
    private List<Parameter> parameters;

    public AssertionTest(String guid, String label, String description, String specification, String assertionType,
                         String resourceType, String dimension, List<String> informationElement) {

        this.guid = guid;
        this.label = label;
        this.description = description;
        this.specification = specification;
        this.assertionType = assertionType;
        this.resourceType = resourceType;
        this.dimension = dimension;
        this.informationElement = informationElement;

        // Validate
        if (!Arrays.asList("SINGLERECORD", "MULTIRECORD").contains(resourceType.toUpperCase())) {
            throw new IllegalArgumentException("Invalid value for resource type \"" + assertionType + "\" for test: "
                    + guid);
        }

        if (assertionType.equalsIgnoreCase("MEASURE") && (dimension.isEmpty() || dimension == null)) {
            throw new IllegalArgumentException("Test is defined to be a measure but is missing a value for \"Dimension\": " + guid);
        }

        if (informationElement.isEmpty()) {
            throw new IllegalArgumentException("No information elements declared for test: " + guid);
        }

        if (!Arrays.asList("MEASURE", "VALIDATION", "AMENDMENT").contains(assertionType.toUpperCase())) {
            throw new IllegalArgumentException("Invalid value for assertion type \"" + assertionType + "\" for test: "
                    + guid);
        }
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

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getSpecification() {
        return specification;
    }

    public String getAssertionType() {
        return assertionType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getDimension() {
        return dimension;
    }

    public List<String> getInformationElement() {
        return informationElement;
    }
}
