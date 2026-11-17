/** FFDQModel.java
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
package org.datakurator.ffdq.rdf;

import org.datakurator.dwcloud.Vocabulary;
import org.datakurator.ffdq.model.DataResource;
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.Amendment;
import org.datakurator.ffdq.model.context.DataQualityNeed;
import org.datakurator.ffdq.model.context.Issue;
import org.datakurator.ffdq.model.context.Measure;
import org.datakurator.ffdq.model.context.Validation;
import org.datakurator.ffdq.model.report.MeasurementResponse;
import org.datakurator.ffdq.model.report.Response;
import org.datakurator.ffdq.model.solutions.DataQualityMethod;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by lowery on 11/14/17.
 *
 * @author mole
 * @version $Id: $Id
 */
public class FFDQModel extends BaseModel {
    private final Vocabulary vocab;

    private final static Log logger = LogFactory.getLog(FFDQModel.class);
    
    /**
     * <p>Constructor for FFDQModel.</p>
     */
    public FFDQModel() {
        super();

        this.vocab = Vocabulary.defaultInstance();
    }

    /** Prefixes used in the queries for Tests, Methods, and Specifications. */
    private static final String TEST_PREFIXES = "PREFIX bdqffdq: <https://rs.tdwg.org/bdqffdq/terms/> "
            + "PREFIX dcterms: <http://purl.org/dc/terms/> ";

    /** The Method classes and the properties that relate each Method to its Test. */
    private static final String METHOD_VALUES = "VALUES (?methodType ?forTest) { "
            + "(bdqffdq:ValidationMethod bdqffdq:forValidation) "
            + "(bdqffdq:MeasurementMethod bdqffdq:forMeasure) "
            + "(bdqffdq:AmendmentMethod bdqffdq:forAmendment) "
            + "(bdqffdq:IssueMethod bdqffdq:forIssue) } ";

    /**
     * The IRI of a mechanism (or other resource) given as an IRI, a urn:uuid:, or a bare uuid.
     *
     * @param guid the identifier
     * @return an IRI, in angle brackets for use in SPARQL
     */
    private static String iriRef(String guid) {
        String iri = guid.contains(":") ? guid : "urn:uuid:" + guid;
        return "<" + iri.replace(">", "%3E") + ">";
    }

    /**
     * The IRIs a Test (or Specification) may have, given as an IRI, a urn:uuid:, or a bare uuid: an IRI
     * stands for itself, and a uuid, urn:uuid:{uuid}, or Test IRI bdqtest:{uuid} for both urn:uuid:{uuid}
     * and bdqtest:{uuid} (a Test defined only in a library is identified by urn:uuid:{uuid}).
     *
     * @param guid the identifier
     * @return the IRIs, in angle brackets for use in SPARQL, separated by spaces
     */
    private static String candidateIriRefs(String guid) {
        String id = guid.trim();
        if (id.startsWith("urn:uuid:")) {
            id = id.substring("urn:uuid:".length());
        } else if (id.startsWith(Namespace.BDQTEST) && id.indexOf('/', Namespace.BDQTEST.length()) < 0) {
            id = id.substring(Namespace.BDQTEST.length());
        } else if (id.contains(":")) {
            return iriRef(id);
        }
        return iriRef("urn:uuid:" + id) + " " + iriRef(Namespace.BDQTEST + id);
    }

    /**
     * <p>findSpecificationsForMechanism.</p>
     *
     * @param mechanismGuid a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Specification> findSpecificationsForMechanism(String mechanismGuid) {
        String sparql = TEST_PREFIXES +
                "SELECT ?specification " +
                "WHERE { ?implementation bdqffdq:implementedBy " + iriRef(mechanismGuid) + " . " +
                "?implementation bdqffdq:usesSpecification ?specification }";

        return (Map<String, Specification>) findAll(Specification.class, sparql, "specification");
    }
    
    // TODO: Find all specifications for a use case.
    
    /**
     * find all Specifications known to the model.
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Specification> findAllSpecifications() {
        String sparql = TEST_PREFIXES +
                "SELECT ?specification " +
                "WHERE { ?specification a bdqffdq:Specification }";

        return (Map<String, Specification>) findAll(Specification.class, sparql, "specification");
    }
    
    /**
     * Find the Tests (versions of Tests) whose Specifications are used by implementations of a mechanism.
     *
     * @param mechanismGuid the mechanism, as an IRI, urn:uuid:, or bare uuid
     * @return the Tests, keyed by their IRIs
     */
    public Map<String, DataQualityNeed> findVersionOfTestsForMechanism(String mechanismGuid) {
        String sparql = TEST_PREFIXES + 
                "SELECT DISTINCT ?test " +
                "WHERE { ?implementation bdqffdq:implementedBy " + iriRef(mechanismGuid) + " . " +
                "?implementation bdqffdq:usesSpecification ?spec . " +
                METHOD_VALUES +
                "?method a ?methodType ; bdqffdq:hasSpecification ?spec ; ?forTest ?test . }";
        return (Map<String, DataQualityNeed>) findAll(DataQualityNeed.class, sparql, "test");
    }

    /**
     * Find the Method (ValidationMethod, MeasurementMethod, AmendmentMethod, or IssueMethod) for a Test.
     *
     * The Test may be given by its IRI (e.g. https://rs.tdwg.org/bdqtest/terms/{uuid}), by the IRI of a
     * version of it, by urn:uuid:{uuid} or the bare uuid, or by the IRI of the Method's Specification.
     * Both the form of the BDQ standard's bdqtest RDF, where a Method refers to the Test
     * (bdqffdq:forValidation bdqtest:{uuid}), and the form written by kurator-ffdq for libraries, where
     * a Method refers to a version of the Test (bdqtest:{uuid}-{date} dcterms:isVersionOf bdqtest:{uuid}),
     * are found.
     *
     * @param testGuid the Test
     * @return the Method, or null if none is found
     */
    public DataQualityMethod findMethodForSpecification(String testGuid) {
    	DataQualityMethod retval = null;
        String sparql = TEST_PREFIXES +
                "SELECT DISTINCT ?method WHERE { " +
                METHOD_VALUES +
                "VALUES ?given { " + candidateIriRefs(testGuid) + " } " +
                "?method a ?methodType . " +
                "{ ?method ?forTest ?given } UNION " +
                "{ ?method ?forTest ?version . ?version dcterms:isVersionOf ?given } UNION " +
                "{ ?method bdqffdq:hasSpecification ?given } " +
                "} ORDER BY ?method";
        try { 
        	retval = (DataQualityMethod) findOne(DataQualityMethod.class, sparql, "method");
        } catch (RuntimeException e) { 
        	logger.warn(e.getMessage());
        }
        return retval;
    }

    /**
     * findTestsForMechanism, given a mechanism, find DataQualityNeeds (Amendment,
     * Validation, Measure, Issue) that have known implementations and mechanisms
     * associating them with a specified mechanism.
     *
     * @param mechanismGuid the mechanism, as an IRI, urn:uuid:, or bare uuid
     * @return the Tests, keyed by their IRIs
     */
    public Map<String, DataQualityNeed> findTestsForMechanism(String mechanismGuid) {
        String sparql = TEST_PREFIXES +
                "SELECT DISTINCT ?test " +
                "WHERE { ?implementation bdqffdq:implementedBy " + iriRef(mechanismGuid) + " . " +
                "?implementation bdqffdq:usesSpecification ?specification . " +
                METHOD_VALUES +
                "?method a ?methodType ; bdqffdq:hasSpecification ?specification ; ?forTest ?test . }";
        return (Map<String, DataQualityNeed>) findAll(DataQualityNeed.class, sparql, "test");
    }
    
    /**
     * find all DataQualityNeeds (Amendment, Validation, Measure, Issue) defined in the model.
     *
     * Two forms of Test are found: a Test described under its own IRI (bdqtest:{uuid}, with
     * dcterms:hasVersion, as in the BDQ standard's bdqtest RDF), and a version of a Test
     * (bdqtest:{uuid}-{date} dcterms:isVersionOf bdqtest:{uuid}, as kurator-ffdq writes for libraries).
     * If both are present for a Test, the Test described under its own IRI is used.
     * 
     * @param keyMapBy key for the returned map, either test, for the IRI of the node read, or
     * uuid for the bare uuid, or ver for the IRI of the Test (the isVersionOf of a version, without
     * the version date). 
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, DataQualityNeed> findAllSingleRecordTests(String keyMapBy) {
    	if (keyMapBy==null) { 
    		keyMapBy = "test";
    	}
    	if (!keyMapBy.equals("test") &&  !keyMapBy.equals("uuid") && ! keyMapBy.equals("ver")) { 
    		keyMapBy = "test";
    	}

        String sparql = TEST_PREFIXES 
                + "SELECT ?test ?uuid ?ver " 
                + "WHERE { "
                + "  VALUES ?type { bdqffdq:Validation bdqffdq:Amendment bdqffdq:Measure bdqffdq:Issue } "
                + "  ?test a ?type . "
                + "  OPTIONAL { ?test dcterms:isVersionOf ?versionOf } "
                + "  BIND(COALESCE(?versionOf, ?test) AS ?testIri) "
                + "  FILTER (!BOUND(?versionOf) || NOT EXISTS { ?versionOf a ?type }) "
                // a Test identified only by urn:uuid:{uuid} is keyed as the Test IRI bdqtest:{uuid}
                + "  BIND(IF(STRSTARTS(STR(?testIri), 'urn:uuid:'), "
                + "     IRI(CONCAT('" + Namespace.BDQTEST + "', STRAFTER(STR(?testIri), 'urn:uuid:'))), ?testIri) AS ?ver) "
                + "  BIND(REPLACE(STR(?ver),'" + Namespace.BDQTEST + "','') AS ?uuid ) "
                //+ "  ?test bdqffdq:hasResourceType bdqffdq:SingleRecord ."
                + "}";
        logger.info(sparql);
        Map<String, DataQualityNeed> retval = new HashMap<String,DataQualityNeed>();
        retval.putAll(findAllMapBy(DataQualityNeed.class, sparql, "test", keyMapBy));
        return retval;
    }
    
    /**
     * <p>findDataResources.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<DataResource> findDataResources() {
        List<DataResource> dataResources = new ArrayList<>();

        // bdqffdq:appliesTo links each Response to its DataResource (stored by TestRunner via
        // Response.setDataResource()).  The trailing slash on the prefix is required so that
        // e.g. bdqffdq:ValidationResponse expands to the correct full IRI.
        String sparql = "PREFIX bdqffdq: <https://rs.tdwg.org/bdqffdq/terms/> " +
                "SELECT DISTINCT ?dataResource WHERE { " +
                "?assertion bdqffdq:appliesTo ?dataResource " +
                "}";

        TupleQueryResult result = executeQuery(sparql);

        while(result.hasNext()) {
            BindingSet bindingSet = result.next();
            String uri = bindingSet.getValue("dataResource").stringValue();

            Model model = getResource(uri);
            DataResource dataResource = new DataResource(vocab, model);
            dataResources.add(dataResource);
        }

        return dataResources;
    }

    /**
     * <p>findFieldsByAssertionType.</p>
     *
     * @param cls a {@link java.lang.Class} object.
     * @return a {@link java.util.List} object.
     */
    public List<String> findFieldsByAssertionType(Class<? extends Response> cls) {
        List<String> fields = new ArrayList<>();

        // The Test of a Response is reached as the BDQ standard has it: the Implementation that
        // bdqffdq:producesResponse the Response bdqffdq:usesSpecification a Specification, which a
        // Method bdqffdq:hasSpecification, and the Method is bdqffdq:forValidation (etc.) the Test.
        // Reports written before 4.0.0 linked the Response to the Test directly.
        String sparql = TEST_PREFIXES +
                "SELECT DISTINCT ?field WHERE { " +
                "?assertion a ?type . " +
                "VALUES ?type { bdqffdq:" + cls.getSimpleName() +
                (cls == MeasurementResponse.class ? " bdqffdq:MeasureResponse" : "") + " } " +
                "{ ?implementation bdqffdq:producesResponse ?assertion ; bdqffdq:usesSpecification ?specification . " +
                "  ?method bdqffdq:hasSpecification ?specification . " +
                "  VALUES ?forTest { bdqffdq:forValidation bdqffdq:forMeasure bdqffdq:forAmendment bdqffdq:forIssue } " +
                "  ?method ?forTest ?context . } " +
                "UNION { VALUES ?oldContext { bdqffdq:criterionInContext bdqffdq:dimensionInContext " +
                "  bdqffdq:enhancementInContext bdqffdq:hasIssue } ?assertion ?oldContext ?context . } " +
                "VALUES ?hasIe { bdqffdq:hasInformationElement bdqffdq:hasActedUponInformationElement " +
                "  bdqffdq:hasConsultedInformationElement } " +
                "?context ?hasIe ?ie . " +
                "?ie bdqffdq:composedOf ?field " +
                "}";

        TupleQueryResult result = executeQuery(sparql);

        while(result.hasNext()) {
            BindingSet bindingSet = result.next();
            String field = bindingSet.getValue("field").stringValue();

            fields.add(field);
        }

        return fields;
    }

    /**
     * <p>listDataResourcesByURI.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<URI> listDataResourcesByURI() {
        List<URI> dataResources = new ArrayList<>();

        String sparql = TEST_PREFIXES +
                "SELECT DISTINCT ?dataResource WHERE { " +
                "?assertion bdqffdq:appliesTo ?dataResource " +
                "}";

        TupleQueryResult result = executeQuery(sparql);

        while(result.hasNext()) {
            BindingSet bindingSet = result.next();
            String uri = bindingSet.getValue("dataResource").stringValue();

            try {
                dataResources.add(new URI(uri));
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid DataResource uri: " + uri);
            }
        }

        return dataResources;
    }

    /**
     * <p>findAssertionsForDataResource.</p>
     *
     * @param dataResource a {@link org.datakurator.ffdq.model.DataResource} object.
     * @param cls a {@link java.lang.Class} object.
     * @return a {@link java.util.List} object.
     */
    public List<Response> findAssertionsForDataResource(DataResource dataResource, Class<? extends Response> cls) {
        // Use bdqffdq:appliesTo (not prov:used) – that is the predicate TestRunner stores.
        // The trailing slash on bdqffdq: is required so bdqffdq:ValidationResponse (etc.)
        // expands to the correct full IRI <https://rs.tdwg.org/bdqffdq/terms/ValidationResponse>.
        String sparql = "PREFIX bdqffdq: <https://rs.tdwg.org/bdqffdq/terms/> " +
                "SELECT ?assertion WHERE { " +
                "?assertion bdqffdq:appliesTo <" + dataResource.getURI() + "> . " +
                "?assertion a ?type . " +
                "VALUES ?type { bdqffdq:" + cls.getSimpleName() +
                // reports written before 4.0.0 used bdqffdq:MeasureResponse
                (cls == MeasurementResponse.class ? " bdqffdq:MeasureResponse" : "") + " } " +
                "}";


        Map<String, Response> responses = findAll(cls, sparql, "assertion");

        return new ArrayList<>(responses.values());
    }

    /**
     * <p>findDataResource.</p>
     *
     * @param uri a {@link java.net.URI} object.
     * @return a {@link org.datakurator.ffdq.model.DataResource} object.
     */
    public DataResource findDataResource(URI uri) {
        Model model = getResource(uri.toString());

        if (model.subjects().size() == 0) {
            return null;
        }

        DataResource dataResource = new DataResource(vocab, model);

        return dataResource;
    }

    /**
     * <p>Getter for the field <code>vocab</code>.</p>
     *
     * @return a {@link org.datakurator.dwcloud.Vocabulary} object.
     */
    public Vocabulary getVocab() {
        return vocab;
    }
}
