/** AssertionTest.java
 *
 * Copyright 2017 President and Fellows of Harvard College
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
package org.datakurator.ffdq.runner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssertionTest {
    public static final String MEASURE = "MEASURE";
    public static final String VALIDATION = "VALIDATION";
    public static final String AMENDMENT = "AMENDMENT";

    public static final String SINGLE_RECORD = "SINGLERECORD";
    public static final String MULTI_RECORD = "MULTIRECORD";

    private String guid;
    private String label;
    private String description;
    private String specification;
    private String assertionType;
    private String resourceType;
    private String dimension;
    private List<String> informationElement;

    private Class cls;
    private Method method;
    private List<TestParam> parameters;

    public AssertionTest() {
        // default constructor for test runner
    }

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
        if (!Arrays.asList(SINGLE_RECORD, MULTI_RECORD).contains(resourceType.toUpperCase())) {
            throw new IllegalArgumentException("Invalid value for resource type \"" + resourceType + "\" for test: "
                    + guid);
        }

        if (assertionType.equalsIgnoreCase(MEASURE) && (dimension.isEmpty() || dimension == null)) {
            throw new IllegalArgumentException("Test is defined to be a measure but is missing a value for \"Dimension\": " + guid);
        }

        if (informationElement.isEmpty()) {
            throw new IllegalArgumentException("No information elements declared for test: " + guid);
        }

        if (!Arrays.asList(MEASURE, VALIDATION, AMENDMENT).contains(assertionType.toUpperCase())) {
            throw new IllegalArgumentException("Invalid value for assertion type \"" + assertionType + "\" for test: "
                    + guid);
        }
    }

    public AssertionTest(String guid, Class cls, Method method) {
        this.guid = guid;
        this.cls = cls;
        this.method = method;
    }

    public Object invoke(Object instance, Map<String, String> record) throws InvocationTargetException, IllegalAccessException {
        Set<String> keys = record.keySet();

        // Check that record contains required fields
        for (TestParam param : parameters) {
            if (!keys.contains(param.getTerm())) {
                // throw new RuntimeException("Record argument missing one or more required fields: " + parameters);
                // TODO: warning isntead of exception
            }
        }

        // Test method arguments
        String[] args = new String[parameters.size()];

        // Map keys to method arguments by term name
        for (int i = 0; i < args.length; i++) {
            TestParam param = parameters.get(i);

            String value = record.get(param.getTerm());
            args[param.getIndex()] = value;
        }

        return method.invoke(instance, args);
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getGuid() {
        return guid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getAssertionType() {
        return assertionType;
    }

    public void setAssertionType(String assertionType) {
        this.assertionType = assertionType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public List<String> getInformationElement() {
        return informationElement;
    }

    public void setInformationElement(List<String> informationElement) {
        this.informationElement = informationElement;
    }

    public Class getCls() {
        return cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public List<TestParam> getParameters() {
        return parameters;
    }

    public void setParameters(List<TestParam> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "AssertionTest{" +
                "guid='" + guid + '\'' +
                ", label='" + label + '\'' +
                ", description='" + description + '\'' +
                ", specification='" + specification + '\'' +
                ", assertionType='" + assertionType + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", dimension='" + dimension + '\'' +
                ", informationElement=" + informationElement +
                ", cls=" + cls +
                ", method=" + method +
                ", parameters=" + parameters +
                '}';
    }
}
