/**  DataResource.java
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
package org.datakurator.ffdq.model;

import org.datakurator.dwcloud.Vocabulary;
import org.datakurator.ffdq.rdf.Namespace;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Data Resource is an instance of data and the target to the DQ assessment and management.
 *
 * Data Resources have a property called “resource type.” Resource type, in the context of the conceptual framework,
 * can be "single record" or "multi-record (dataset)". This property is important because it affects the method for
 * measuring, validating and improving a Data Resource. For example, coordinate completeness of a single record could
 * be measured qualitatively by checking whether the latitude and longitude of the record are filled or not; whereas
 * the coordinate completeness of a dataset could be measured quantitatively, measuring the percentage of records in
 * the dataset which have the latitude and longitude fields filled. Both measurements are for coordinate completeness,
 * but they are measured in different ways due to the different resource type.
 *
 * Veiga AK, Saraiva AM, Chapman AD, Morris PJ, Gendreau C, Schigel D, et al. (2017) A conceptual framework for quality
 * assessment and management of biodiversity data. PLoS ONE 12(6): e0178731.
 *
 * @see <a href="https://doi.org/10.1371/journal.pone.0178731">https://doi.org/10.1371/journal.pone.0178731</a>
 *
 */
public class DataResource {
    private Vocabulary vocab;

    private ModelBuilder builder = new ModelBuilder().setNamespace("ffdq", "http://example.com/ffdq/");
    private ValueFactory valueFactory = SimpleValueFactory.getInstance();

    private Model model;
    private IRI subject;

    public DataResource(Vocabulary vocab) {
        // Create data resource subject iri from generated uuid
        String uuid = "urn:uuid:" + UUID.randomUUID().toString();

        // Set an rdf type of ffdq:DataResource
        subject = valueFactory.createIRI(uuid);
        builder.defaultGraph().add(subject, RDF.TYPE, "ffdq:DataResource");

        this.vocab = vocab;
    }

    public DataResource(Vocabulary vocab, Model model) {
        Resource[] resources = (Resource[]) model.subjects().toArray();

        // Expects a model that contains only the data resource
        if (resources.length != 1) {
            throw new IllegalArgumentException("Model must contain exactly one resource, " + resources.length +
                    " found.");
        }

        // Data resource subject is the uri of the first resource in the model
        this.subject = valueFactory.createIRI(resources[0].stringValue());

        this.vocab = vocab;
        this.model = model;
    }

    public DataResource(Vocabulary vocab, Map<String, String> record) {
        // Create data resource subject iri from generated uuid
        String uuid = "urn:uuid:" + UUID.randomUUID().toString();

        // Set an rdf type of ffdq:DataResource
        subject = valueFactory.createIRI(uuid);
        builder.defaultGraph().add(subject, RDF.TYPE, "ffdq:DataResource");

        // Add triples from the key value pairs in the map
        for (String term : record.keySet()) {

            URI uri = vocab.getURI(term);  // Lookup the term uri via dwcloud vocab

            IRI predicate = valueFactory.createIRI(uri.toString());
            String object = record.get(term);

            builder.defaultGraph().add(subject, predicate, object);

        }

        this.vocab = vocab;
    }

    public String get(String term) {
        // Resolve term and create predicate iri
        URI uri = Namespace.resolvePrefixedTerm(term);
        IRI predicate = valueFactory.createIRI(uri.toString());

        // Get the first value from the model
        Set<Value> values = model.filter(subject, predicate, null).objects();
        String value = values.toArray()[0].toString();

        return value;
    }

    public void put(URI term, String value) {
        IRI predicate = valueFactory.createIRI(term.toString());
        builder.defaultGraph().add(subject, predicate, value);
    }

    public URI getURI() {
        try {
            return new URI(subject.stringValue());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Subject is not a valid uri", e);
        }
    }

    public String getRecordId() {
        // Use the id term set in vocab to find the id
        return get(vocab.getIdTerm());
    }

    public Map<String, String> asMap() {
        Map<String, String> record = new HashMap<>();

        // Iterate over model statements and convert to map
        for (Statement statement : model) {
            String term = statement.getPredicate().getLocalName();
            String value = statement.getObject().stringValue();

            record.put(term, value);
        }

        return record;
    }

    public Model asModel() {
        return model;
    }
}
