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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssertionTest {
    public static final String MEASURE = "MEASURE";
    public static final String VALIDATION = "VALIDATION";
    public static final String AMENDMENT = "AMENDMENT";
    public static final String ISSUE = "ISSUE";

    public static final String SINGLE_RECORD = "SINGLERECORD";
    public static final String MULTI_RECORD = "MULTIRECORD";

    private String guid;
    private String label;
    private String version;  // from date test was last updated.
    private String description;
    private String criterionLabel;
    private String specification;
    private String assertionType;
    private String resourceType;
    private String dimension;
    private List<String> informationElement;  // will be java class parameters, treated as ActedUpon
    private List<String> actedUpon;  // will be java class parameters
    private List<String> consulted;  // will be java class parameters
    private List<String> testParameters;      // parameters specified in the test to change its behavior.

    private Class cls;
    private Method method;
    private List<TestParam> parameters;

    public AssertionTest() {
        // default constructor for test runner
    }

    public AssertionTest(String guid, String label, String version, String description, String criterionLabel, String specification, String assertionType,
                         String resourceType, String dimension, List<String> informationElement, List<String> actedUpon, List<String> consulted, List<String> testParameters) throws UnsupportedTypeException {

        this.guid = guid;
        this.label = label;
        this.version = version;
        this.description = description;
        this.setCriterionLabel(criterionLabel);
        this.specification = specification;
        this.assertionType = assertionType;
        this.resourceType = resourceType;
        this.dimension = dimension;
        this.informationElement = informationElement;
        this.actedUpon = actedUpon;
        this.consulted = consulted;
        this.testParameters = testParameters;
        

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

        if (!Arrays.asList(MEASURE, VALIDATION, AMENDMENT, ISSUE).contains(assertionType.toUpperCase())) {
            throw new IllegalArgumentException("Invalid value for assertion type \"" + assertionType + "\" for test: " + guid);
        }
    }

    public AssertionTest(String guid, Class cls, Method method) {
        this.guid = guid;
        this.cls = cls;
        this.method = method;
    }

    public Object invoke(Object instance, Map<String, String> record) throws InvocationTargetException {
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

        try {
            return method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not invoke test method: " + cls + "." + method, e);
        }
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getGuid() {
        return guid;
    }
    
    public String getGuidTDWGNamespace()  {
    	if (guid==null) { 
    		return guid;
    	} else { 
    		return "https://rs.tdwg.org/bdq/terms/" + guid.replace("urn:uuid:","");
    	}
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }    
    
    public String getProvidesVersion() { 
    	StringBuilder retval = new StringBuilder();
    	return retval.append(this.getGuidTDWGNamespace()).append("/").append(this.version).toString();
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCriterionLabel() {
		return criterionLabel;
	}

	public void setCriterionLabel(String criterionLabel) {
		this.criterionLabel = criterionLabel;
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
    
    public List<String> getActedUpon() {
        return actedUpon;
    }

    public void setActedUpon(List<String> actedUpon) {
        this.actedUpon = actedUpon;
    }    
    
    public List<String> getConsulted() {
        return consulted;
    }

    public void setConsulted(List<String> consulted) {
        this.consulted = consulted;
    }

    /**
     * Obtain a list of all information elements, acted upon, consulted, and not specified.
     * 
     * @return a list of informationElement plus actedUpon plus consulted.
     */
    public List<String> getAllInformationElements() { 
    	ArrayList<String> retval = new ArrayList<String>();
    	retval.addAll(informationElement);
    	retval.addAll(actedUpon);
    	retval.addAll(consulted);
    	return retval;
    }
    
    /**
	 * @return the testParameters that alter the behavior of the test.
	 */
	public List<String> getTestParameters() {
		return testParameters;
	}

	/**
	 * @param testParameters the testParameters to set to alter the behavior of the test.
	 */
	public void setTestParameters(List<String> testParameters) {
		if (testParameters!=null && testParameters.size()>0 && !testParameters.get(1).equals("")) { 
		    this.testParameters = testParameters;
		} else { 
			this.testParameters = new ArrayList<String>();
		}
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

    /** the information elements as inputs to the test **/
    public List<TestParam> getParameters() {
        return parameters;
    }

    /** These are the information elements as inputs **/
    public void setParameters(List<TestParam> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "AssertionTest{" +
                "guid='" + guid + '\'' +
                ", label='" + label + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", specification='" + specification + '\'' +
                ", assertionType='" + assertionType + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", dimension='" + dimension + '\'' +
                ", informationElement=" + informationElement.toString() +
                ", actedUpon=" + actedUpon.toString() +
                ", consulted=" + consulted.toString() +
                ", testParameters=" + testParameters.toString() + 
                ", cls=" + cls +
                ", method=" + method +
                ", parameters=" + parameters +
                '}';
    }
}
