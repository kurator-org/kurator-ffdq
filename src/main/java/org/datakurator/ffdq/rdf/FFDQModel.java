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
import org.datakurator.ffdq.model.report.Assertion;
import org.datakurator.ffdq.model.solutions.AssertionMethod;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by lowery on 11/14/17.
 */
public class FFDQModel extends BaseModel {
    private final Vocabulary vocab;

    public FFDQModel() {
        super();

        this.vocab = Vocabulary.defaultInstance();
    }

    public Map<String, Specification> findSpecificationsForMechanism(String mechanismGuid) {
        Set<String> guids = new HashSet<>();

        String sparql = "PREFIX ffdq: <http://rs.tdwg.org/ffdq#> " +
                "SELECT ?specification " +
                "WHERE { ?implementation ffdq:implementedBy <" + mechanismGuid + "> . " +
                "?implementation ffdq:hasSpecification ?specification }";

        return (Map<String, Specification>) findAll(Specification.class, sparql, "specification");
    }

    public AssertionMethod findMethodForSpecification(String testGuid) {
        String sparql = "PREFIX ffdq: <http://rs.tdwg.org/ffdq#> " +
                "SELECT ?method WHERE { " +
                "{ ?method a ffdq:MeasurementMethod } UNION " +
                "{ ?method a ffdq:ValidationMethod } UNION " +
                "{ ?method a ffdq:AmendmentMethod } . " +
                "?method ffdq:hasSpecification <" + testGuid + "> }";

        return (AssertionMethod) findOne(AssertionMethod.class, sparql, "method");
    }

    public List<DataResource> findDataResources() {
        List<DataResource> dataResources = new ArrayList<>();

        String sparql = "PREFIX ffdq: <http://rs.tdwg.org/ffdq#> " +
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

    public List<String> findFieldsByAssertionType(Class<? extends Assertion> cls) {
        List<String> fields = new ArrayList<>();

        String sparql = "PREFIX ffdq: <http://rs.tdwg.org/ffdq#> " +
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

    public List<URI> listDataResourcesByURI() {
        List<URI> dataResources = new ArrayList<>();

        String sparql = "PREFIX ffdq: <http://rs.tdwg.org/ffdq#> " +
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

    public List<Assertion> findAssertionsForDataResource(DataResource dataResource, Class<? extends Assertion> cls) {
        String sparql = "PREFIX ffdq: <http://rs.tdwg.org/ffdq#> " +
                "PREFIX prov: <http://www.w3.org/ns/prov#> " +
                "SELECT ?assertion ?type WHERE { " +
                "?assertion prov:used <" + dataResource.getURI() + "> . " +
                "?assertion a ffdq:" + cls.getSimpleName() + " " +
                "}";


        Map<String, Assertion> assertions = findAll(cls, sparql, "assertion");

        return new ArrayList<>(assertions.values());
    }

    public DataResource findDataResource(URI uri) {
        Model model = getResource(uri.toString());

        if (model.subjects().size() == 0) {
            return null;
        }

        DataResource dataResource = new DataResource(vocab, model);

        return dataResource;
    }

    public Vocabulary getVocab() {
        return vocab;
    }
}
