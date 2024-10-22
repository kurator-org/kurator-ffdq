
/**
 * AssertionTest.java
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
 *
 * @author mole
 * @version $Id: $Id
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
    /** Constant <code>MEASURE="MEASURE"</code> */
    public static final String MEASURE = "MEASURE";
    /** Constant <code>VALIDATION="VALIDATION"</code> */
    public static final String VALIDATION = "VALIDATION";
    /** Constant <code>AMENDMENT="AMENDMENT"</code> */
    public static final String AMENDMENT = "AMENDMENT";
    /** Constant <code>ISSUE="ISSUE"</code> */
    public static final String ISSUE = "ISSUE";

    /** Constant <code>SINGLE_RECORD="SINGLERECORD"</code> */
    public static final String SINGLE_RECORD = "SINGLERECORD";
    /** Constant <code>MULTI_RECORD="MULTIRECORD"</code> */
    public static final String MULTI_RECORD = "MULTIRECORD";

    private String guid;
    private String historyNumber; // github issue number for rationalle management
    private String label;
    private String version;  // from date test was last updated.
    private String description;
    private String criterionLabel;
    private String specification;
    private String authoritiesDefaults;  // source authorities and default values for parameters.
    private String assertionType;
    private String resourceType;
    private String dimension;  // bdqdim: terms
    private String criterion;  // bdqcrit: terms
    private String enhancement; // bdqenh: terms
    private List<String> informationElement;  // will be java class parameters, treated as ActedUpon
    private List<String> actedUpon;  // will be java class parameters
    private List<String> consulted;  // will be java class parameters
    private List<String> testParameters;      // parameters specified in the test to change its behavior.
    private List<String> useCases; // labels for UseCases related to test.
    private String examples;  // list of examples
    private String references;   // dcterms:bibliographicCitation, string.  (dcterms:references for resource iri)
    private String note;    // skos:note
    private String issued; // dcterms:issued for DataQualityNeed 
    private String historyNoteSource; // skos:historyNote for Source, on bdqffdq:DataQualityMethod
    
    // additional strings for classes holding more of the framework structure of the test
    // can be loaded from a file for consistent generation of RDF.
    // guid for AmendmentAssertion/Measurement/IssueAssertion/ValidationAssertion Method
    private String methodGuid;
    // guid for Contexturalized Enhancement/Dimension/InvertedCriterion/Criterion
    private String specificationGuid;
    // guid AmendmentAssertion/Measurement/IssueAssertion/ValidationAssertion Policy, 
    private String policyGuid;
    
    private Class cls;
    private Method method;
    private List<TestParam> parameters;
	private String prefLabel;
	private String historyNoteUrl;

    /**
     * <p>Constructor for AssertionTest.</p>
     */
    public AssertionTest() {
        // default constructor for test runner
    }

    /**
     * <p>Constructor for AssertionTest.</p>
     *
     * @param guid a {@link java.lang.String} object.
     * @param label a {@link java.lang.String} object.
     * @param version a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param criterionLabel a {@link java.lang.String} object.
     * @param specification a {@link java.lang.String} object.
     * @param authoritiesDefaults a {@link java.lang.String} object.
     * @param assertionType a {@link java.lang.String} object.
     * @param resourceType a {@link java.lang.String} object.
     * @param dimension a {@link java.lang.String} object.
     * @param criterion a {@link java.lang.String} object.
     * @param enhancement a {@link java.lang.String} object.
     * @param informationElement a {@link java.util.List} object.
     * @param actedUpon a {@link java.util.List} object.
     * @param consulted a {@link java.util.List} object.
     * @param testParameters a {@link java.util.List} object.
     * @param useCases a {@link java.util.List} object.
     * @param examples a string containing a list of examples.
     * @throws org.datakurator.ffdq.runner.UnsupportedTypeException if any.
     */
    public AssertionTest(String guid, String label, String version, String description, 
    		String criterionLabel, String specification, String authoritiesDefaults,
    		String assertionType, String resourceType, String dimension, 
    		String criterion, String enhancement,
            List<String> informationElement, List<String> actedUpon, 
            List<String> consulted, List<String> testParameters, List<String> useCases, String examples) throws UnsupportedTypeException {

        this.guid = guid;
        this.label = label;
        this.version = version;
        this.description = description;
        this.setCriterionLabel(criterionLabel);
        this.specification = specification;
        this.authoritiesDefaults = authoritiesDefaults;
        this.assertionType = assertionType;
        this.resourceType = resourceType;
        this.dimension = dimension;
        this.setCriterion(criterion);
        this.setEnhancement(enhancement);
        this.informationElement = informationElement;
        this.actedUpon = actedUpon;
        this.consulted = consulted;
        this.testParameters = testParameters;
        this.setUseCases(useCases);
        this.examples = examples;

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

    /**
     * <p>Constructor for AssertionTest.</p>
     *
     * @param guid a {@link java.lang.String} object.
     * @param cls a {@link java.lang.Class} object.
     * @param method a {@link java.lang.reflect.Method} object.
     */
    public AssertionTest(String guid, Class cls, Method method) {
        this.guid = guid;
        this.cls = cls;
        this.method = method;
    }

    /**
     * <p>invoke.</p>
     *
     * @param instance a {@link java.lang.Object} object.
     * @param record a {@link java.util.Map} object.
     * @return a {@link java.lang.Object} object.
     * @throws java.lang.reflect.InvocationTargetException if any.
     */
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

    /**
     * <p>Setter for the field <code>guid</code>.</p>
     *
     * @param guid a {@link java.lang.String} object.
     */
    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * <p>Getter for the field <code>guid</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGuid() {
        return guid;
    }
    
    /**
     * <p>getGuidTDWGNamespace.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGuidTDWGNamespace()  {
    	if (guid==null) { 
    		return guid;
    	} else { 
    		return "https://rs.tdwg.org/bdqcore/terms/" + guid.replace("urn:uuid:","");
    	}
    }

    /**
     * <p>Getter for the field <code>label</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLabel() {
        return label;
    }

    /**
     * <p>Setter for the field <code>label</code>.</p>
     *
     * @param label a {@link java.lang.String} object.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * <p>Getter for the field <code>version</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVersion() {
        return version;
    }

    /**
     * <p>Setter for the field <code>version</code>.</p>
     *
     * @param version a {@link java.lang.String} object.
     */
    public void setVersion(String version) {
        this.version = version;
    }    
    
    /**
     * <p>getProvidesVersion.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getProvidesVersion() { 
    	StringBuilder retval = new StringBuilder();
    	return retval.append(this.getGuidTDWGNamespace()).append("/").append(this.version).toString();
    }
    
    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Unused.  
     * 
     * <p>Getter for the field <code>criterionLabel</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Deprecated 
    public String getCriterionLabel() {
		return criterionLabel;
	}

	/**
	 * <p>Setter for the field <code>criterionLabel</code>.</p>
	 *
	 * @param criterionLabel a {@link java.lang.String} object.
	 */
    @Deprecated
	public void setCriterionLabel(String criterionLabel) {
		this.criterionLabel = criterionLabel;
	}

	/**
	 * <p>Getter for the field <code>specification</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSpecification() {
        return specification;
    }

    /**
     * <p>Setter for the field <code>specification</code>.</p>
     *
     * @param specification a {@link java.lang.String} object.
     */
    public void setSpecification(String specification) {
        this.specification = specification;
    }

	/**
	 * <p>Getter for the field <code>authoritiesDefaults</code>.</p>
	 *
	 * @return the authoritiesDefaults
	 */
	public String getAuthoritiesDefaults() {
		return authoritiesDefaults;
	}

	/**
	 * <p>Setter for the field <code>authoritiesDefaults</code>.</p>
	 *
	 * @param authoritiesDefaults the authoritiesDefaults to set
	 */
	public void setAuthoritiesDefaults(String authoritiesDefaults) {
		this.authoritiesDefaults = authoritiesDefaults;
	}

	/**
	 * <p>Getter for the field <code>assertionType</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAssertionType() {
        return assertionType;
    }

    /**
     * <p>Setter for the field <code>assertionType</code>.</p>
     *
     * @param assertionType a {@link java.lang.String} object.
     */
    public void setAssertionType(String assertionType) {
        this.assertionType = assertionType;
    }

    /**
     * <p>Getter for the field <code>resourceType</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * <p>Setter for the field <code>resourceType</code>.</p>
     *
     * @param resourceType a {@link java.lang.String} object.
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * <p>Getter for the field <code>dimension</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDimension() {
        return dimension;
    }

    /**
     * <p>Setter for the field <code>dimension</code>.</p>
     *
     * @param dimension a {@link java.lang.String} object.
     */
    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

	/**
	 * <p>Getter for the field <code>criterion</code>.</p>
	 *
	 * @return the criterion
	 */
	public String getCriterion() {
		return criterion;
	}

	/**
	 * <p>Setter for the field <code>criterion</code>.</p>
	 *
	 * @param criterion the criterion to set
	 */
	public void setCriterion(String criterion) {
		this.criterion = criterion;
	}

	/**
	 * <p>Getter for the field <code>enhancement</code>.</p>
	 *
	 * @return the enhancement
	 */
	public String getEnhancement() {
		return enhancement;
	}

	/**
	 * <p>Setter for the field <code>enhancement</code>.</p>
	 *
	 * @param enhancement the enhancement to set
	 */
	public void setEnhancement(String enhancement) {
		this.enhancement = enhancement;
	}

	/**
	 * <p>Getter for the field <code>informationElement</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<String> getInformationElement() {
        return informationElement;
    }

    /**
     * <p>Setter for the field <code>informationElement</code>.</p>
     *
     * @param informationElement a {@link java.util.List} object.
     */
    public void setInformationElement(List<String> informationElement) {
        this.informationElement = informationElement;
    }
    
    /**
     * <p>Getter for the field <code>actedUpon</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getActedUpon() {
        return actedUpon;
    }

    /**
     * <p>Setter for the field <code>actedUpon</code>.</p>
     *
     * @param actedUpon a {@link java.util.List} object.
     */
    public void setActedUpon(List<String> actedUpon) {
        this.actedUpon = actedUpon;
    }    
    
    /**
     * <p>Getter for the field <code>consulted</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getConsulted() {
        return consulted;
    }

    /**
     * <p>Setter for the field <code>consulted</code>.</p>
     *
     * @param consulted a {@link java.util.List} object.
     */
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
	 * <p>Getter for the field <code>testParameters</code>.</p>
	 *
	 * @return the testParameters that alter the behavior of the test.
	 */
	public List<String> getTestParameters() {
		return testParameters;
	}

	/**
	 * <p>Setter for the field <code>testParameters</code>.</p>
	 *
	 * @param testParameters the testParameters to set to alter the behavior of the test.
	 */
	public void setTestParameters(List<String> testParameters) {
		if (testParameters!=null && testParameters.size()>0 && !testParameters.get(1).equals("")) { 
		    this.testParameters = testParameters;
		} else { 
			this.testParameters = new ArrayList<String>();
		}
	}

	/**
	 * <p>Getter for the field <code>cls</code>.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public Class getCls() {
        return cls;
    }

    /**
     * <p>Setter for the field <code>cls</code>.</p>
     *
     * @param cls a {@link java.lang.Class} object.
     */
    public void setCls(Class cls) {
        this.cls = cls;
    }

    /**
     * <p>Getter for the field <code>method</code>.</p>
     *
     * @return a {@link java.lang.reflect.Method} object.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * <p>Setter for the field <code>method</code>.</p>
     *
     * @param method a {@link java.lang.reflect.Method} object.
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * the information elements as inputs to the test *
     *
     * @return a {@link java.util.List} object.
     */
    public List<TestParam> getParameters() {
        return parameters;
    }

    /**
     * These are the information elements as inputs *
     *
     * @param parameters a {@link java.util.List} object.
     */
    public void setParameters(List<TestParam> parameters) {
        this.parameters = parameters;
    }

    /** {@inheritDoc} */
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
                ", useCases=" + useCases.toString() + 
                ", cls=" + cls +
                ", method=" + method +
                ", parameters=" + parameters +
                '}';
    }

	/**
	 * <p>Getter for the field <code>useCases</code>.</p>
	 *
	 * @return the useCases related to this test.
	 */
	public List<String> getUseCases() {
		return useCases;
	}

	/**
	 * <p>Setter for the field <code>useCases</code>.</p>
	 *
	 * @param useCases the useCases to set
	 */
	public void setUseCases(List<String> useCases) {
		this.useCases = useCases;
	}

	/**
	 * <p>Getter for the field <code>methodGuid</code>.</p>
	 *
	 * @return the methodGuid
	 */
	public String getMethodGuid() {
		return methodGuid;
	}

	/**
	 * <p>Setter for the field <code>methodGuid</code>.</p>
	 *
	 * @param methodGuid the methodGuid to set
	 */
	public void setMethodGuid(String methodGuid) {
		this.methodGuid = methodGuid;
	}

	/**
	 * <p>Getter for the field <code>specificationGuid</code>.</p>
	 *
	 * @return the specificationGuid
	 */
	public String getSpecificationGuid() {
		return specificationGuid;
	}

	/**
	 * <p>Setter for the field <code>specificationGuid</code>.</p>
	 *
	 * @param specificaitonGuid the specificationGuid to set
	 */
	public void setSpecificationGuid(String specificaitonGuid) {
		this.specificationGuid = specificaitonGuid;
	}

	/**
	 * <p>Getter for the field <code>policyGuid</code>.</p>
	 *
	 * @return the policyGuid
	 */
	public String getPolicyGuid() {
		return policyGuid;
	}

	/**
	 * <p>Setter for the field <code>policyGuid</code>.</p>
	 *
	 * @param policyGuid the policyGuid to set
	 */
	public void setPolicyGuid(String policyGuid) {
		this.policyGuid = policyGuid;
	}

	/**
	 * Get the string containing the list of examples 
	 * 
	 * @return string containing examples
	 */
	public String getExamples() {
		return examples;
	}

	/**
	 * @return the historyNumber
	 */
	public String getHistoryNumber() {
		return historyNumber;
	}

	/**
	 * @param historyNumber the historyNumber to set
	 */
	public void setHistoryNumber(String historyNumber) {
		this.historyNumber = historyNumber;
	}

	/**
	 * @return the references
	 */
	public String getReferences() {
		return references;
	}

	/**
	 * @param references the references to set
	 */
	public void setReferences(String references) {
		// TODO: Parse into list
		this.references = references.replace("<ul><li>", "").replace("</li></ul>", "").replaceAll("</li><li>","; ");
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * Obtain the skos:prefLabel
	 * 
	 * @return the prefLabel
 	 */
	public String getPrefLabel() { 
		return this.prefLabel;
	}
	
	/**
	 * Add a skos:prefLabel
	 * 
	 * @param prefLabel new value to set
	 */
	public void setPrefLabel(String prefLabel) {
		// TODO Auto-generated method stub
		this.prefLabel = prefLabel;
	}

	/**
	 * Set the historyNoteUrl 
	 * @param historyNoteUrl value to set
	 */
	public void setHistoryNoteUrl(String historyNoteUrl) { 
		this.historyNoteUrl = historyNoteUrl; 
	}
	
	/**
	 * Get the uri for the issue that serves as the rationale management for this test.
	 * 
	 * @return the historyNoteUrl
	 */
	public String getHistoryNoteUrl() {
		return historyNoteUrl;
	}

	/**
	 * @return the issued
	 */
	public String getIssued() {
		return issued;
	}

	/**
	 * @param issued the issued to set
	 */
	public void setIssued(String issued) {
		this.issued = issued;
	}

	/**
	 * @return the historyNoteSource
	 */
	public String getHistoryNoteSource() {
		return historyNoteSource;
	}

	/**
	 * @param historyNoteSource the historyNoteSource to set
	 */
	public void setHistoryNoteSource(String historyNoteSource) {
		this.historyNoteSource = historyNoteSource;
	}

}
