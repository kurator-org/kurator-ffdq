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
import org.datakurator.ffdq.model.context.DataQualityNeed;
import org.datakurator.ffdq.model.report.Assertion;
import org.datakurator.ffdq.model.solutions.DataQualityMethod;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by lowery on 11/14/17.
 *
 * @author mole
 * @version $Id: $Id
 */
public class FFDQModel extends BaseModel {
    private final Vocabulary vocab;

    /**
     * <p>Constructor for FFDQModel.</p>
     */
    public FFDQModel() {
        super();

        this.vocab = Vocabulary.defaultInstance();
    }

    /**
     * <p>findSpecificationsForMechanism.</p>
     *
     * @param mechanismGuid a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Specification> findSpecificationsForMechanism(String mechanismGuid) {
        Set<String> guids = new HashSet<>();

        String sparql = "PREFIX ffdq: <https://rs.tdwg.org/bdqffdq/terms/> " +
                "SELECT ?specification " +
                "WHERE { ?implementation ffdq:implementedBy <" + mechanismGuid + "> . " +
                "?implementation ffdq:hasSpecification ?specification }";

        return (Map<String, Specification>) findAll(Specification.class, sparql, "specification");
    }

    /**
     * <p>findMethodForSpecification.</p>
     *
     * @param testGuid a {@link java.lang.String} object.
     * @return a {@link org.datakurator.ffdq.model.solutions.DataQualityMethod} object.
     */
    public DataQualityMethod findMethodForSpecification(String testGuid) {
        String sparql = "PREFIX ffdq: <https://rs.tdwg.org/bdqffdq/terms> " +
                "SELECT ?method WHERE { " +
                "{ ?method a ffdq:MeasurementMethod } UNION " +
                "{ ?method a ffdq:ValidationMethod } UNION " +
                "{ ?method a ffdq:AmendmentMethod } . " +
                "?method ffdq:hasSpecification <" + testGuid + "> }";

        return (DataQualityMethod) findOne(DataQualityMethod.class, sparql, "method");
    }

    /**
     * findNeedsForMechanism, given a mechanism, find DataQualityNeeds (Amenement,
     * Validation, Measurement, Issue that have known implementations and mechanisms
     * associating them with a specified mechanism.
     *
     * @param mechanismGuid a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public Map<String, DataQualityNeed> findTestsForMechanism(String mechanismGuid) {
        Set<String> guids = new HashSet<>();

        String sparql = "PREFIX bdqffffdq: <https://rs.tdwg.org/bdqffdq/terms/> " +
                "SELECT ?test " +
                "WHERE "
                + "{ ?implementation bdqffdq:implementedBy <" + mechanismGuid + "> . " +
                "?implementation bdqffdq:hasSpecification ?specification "
                + "?specification bdqffdq:forValidation ?test "
                + "} UNION " 
                + "{ ?implementation bdqffdq:implementedBy <" + mechanismGuid + "> . " +
                "?implementation bdqffdq:hasSpecification ?specification "
                + "?specification bdqffdq:forAmendment ?test "
                + "} UNION " 
                + "{ ?implementation bdqffdq:implementedBy <" + mechanismGuid + "> . " +
                "?implementation bdqffdq:hasSpecification ?specification "
                + "?specification bdqffdq:forMeasure ?test "
                + "} UNION " 
                + "{ ?implementation bdqffdq:implementedBy <" + mechanismGuid + "> . " +
                "?implementation bdqffdq:hasSpecification ?specification "
                + "?specification bdqffdq:forIssue ?test "
                + "}";
        return (Map<String, DataQualityNeed>) findAll(DataQualityNeed.class, sparql, "specification");
    }
    
    /**
     * <p>findDataResources.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<DataResource> findDataResources() {
        List<DataResource> dataResources = new ArrayList<>();

        String sparql = "PREFIX ffdq: <https://rs.tdwg.org/bdqffdq/terms> " +
                "PREFIX prov: <http://www.w3.org/ns/prov#> " +
                "SELECT DISTINCT ?dataResource WHERE { " +
                "?assertion prov:used ?dataResource " +
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
    public List<String> findFieldsByAssertionType(Class<? extends Assertion> cls) {
        List<String> fields = new ArrayList<>();

        String sparql = "PREFIX ffdq: <https://rs.tdwg.org/bdqffdq/terms> " +
                "PREFIX prov: <http://www.w3.org/ns/prov#> " +
                "SELECT DISTINCT ?field WHERE { " +
                "?assertion a ffdq:" + cls.getSimpleName() + " . " +
                "{ ?assertion ffdq:dimensionInContext ?context } UNION " +
                "{ ?assertion ffdq:criterionInContext ?context } UNION " +
                "{ ?assertion ffdq:enhancementInContext ?context } . " +
                "?context ffdq:hasInformationElement ?ie . " +
                "?ie ffdq:composedOf ?field " +
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

        String sparql = "PREFIX ffdq: <https://rs.tdwg.org/bdqffdq/terms> " +
                "PREFIX prov: <http://www.w3.org/ns/prov#> " +
                "SELECT DISTINCT ?dataResource WHERE { " +
                "?assertion prov:used ?dataResource " +
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
    public List<Assertion> findAssertionsForDataResource(DataResource dataResource, Class<? extends Assertion> cls) {
        String sparql = "PREFIX ffdq: <https://rs.tdwg.org/bdqffdq/terms> " +
                "PREFIX prov: <http://www.w3.org/ns/prov#> " +
                "SELECT ?assertion ?type WHERE { " +
                "?assertion prov:used <" + dataResource.getURI() + "> . " +
                "?assertion a ffdq:" + cls.getSimpleName() + " " +
                "}";


        Map<String, Assertion> assertions = findAll(cls, sparql, "assertion");

        return new ArrayList<>(assertions.values());
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
