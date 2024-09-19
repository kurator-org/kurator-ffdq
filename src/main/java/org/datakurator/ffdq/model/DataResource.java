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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * @author mole
 * @version $Id: $Id
 */
public class DataResource {
    private Vocabulary vocab;

    private ModelBuilder builder = new ModelBuilder().setNamespace("bdqffdq", "https://rs.tdwg.org/bdqffdq/terms/");
    private ValueFactory valueFactory = SimpleValueFactory.getInstance();

    private Model model;
    private IRI subject;
    
    private final static Logger logger = Logger.getLogger(DataResource.class.getName());

    /**
     * <p>Constructor for DataResource.</p>
     *
     * @param vocab a {@link org.datakurator.dwcloud.Vocabulary} object.
     */
    public DataResource(Vocabulary vocab) {
        // Create data resource subject iri from generated uuid
        String uuid = "urn:uuid:" + UUID.randomUUID().toString();

        // Set an rdf type of ffdq:DataResource
        subject = valueFactory.createIRI(uuid);
        builder.defaultGraph().add(subject, RDF.TYPE, "bdqffdq:DataResource");

        this.vocab = vocab;
    }

    /**
     * <p>Constructor for DataResource.</p>
     *
     * @param vocab a {@link org.datakurator.dwcloud.Vocabulary} object.
     * @param model a {@link org.eclipse.rdf4j.model.Model} object.
     */
    public DataResource(Vocabulary vocab, Model model) {
        List<Resource> resources = new ArrayList<>();
        for (Object obj : model.subjects().toArray()) {
            if (obj instanceof Resource) {
                resources.add((Resource) obj);
            }
        }

        // Expects a model that contains only the data resource
        if (resources.size() != 1) {
            throw new IllegalArgumentException("Model must contain exactly one resource, " + resources.size() +
                    " found.");
        }

        // Data resource subject is the uri of the first resource in the model
        this.subject = valueFactory.createIRI(resources.get(0).stringValue());

        this.vocab = vocab;
        this.model = model;
    }

    /**
     * <p>Constructor for DataResource.</p>
     *
     * @param vocab a {@link org.datakurator.dwcloud.Vocabulary} object.
     * @param record a {@link java.util.Map} object.
     */
    public DataResource(Vocabulary vocab, Map<String, String> record) {
        this(null, vocab, record);
    }

    /**
     * <p>Constructor for DataResource.</p>
     *
     * @param uuid a {@link java.lang.String} object.
     * @param vocab a {@link org.datakurator.dwcloud.Vocabulary} object.
     * @param record a {@link java.util.Map} object.
     */
    public DataResource(String uuid, Vocabulary vocab, Map<String, String> record) {
        if (uuid == null) {
            // Create data resource subject iri from generated uuid
            uuid = "urn:uuid:" + UUID.randomUUID().toString();
        }

        // Set an rdf type of ffdq:DataResource
        subject = valueFactory.createIRI(uuid);
        builder.defaultGraph().add(subject, RDF.TYPE, "bdqffdq:DataResource");

        // Add triples from the key value pairs in the map
        for (String term : record.keySet()) {

        	try { 
            URI uri = vocab.getURI(term);  // Lookup the term uri via dwcloud vocab

            IRI predicate = valueFactory.createIRI(uri.toString());
            String object = record.get(term);

            builder.defaultGraph().add(subject, predicate, object);

        	} catch (Exception e) {
        		logger.log(Level.SEVERE, e.getMessage());
        	}
        }

        this.vocab = vocab;
        this.model = builder.build();
    }

    /**
     * <p>get.</p>
     *
     * @param term a {@link java.net.URI} object.
     * @return a {@link java.lang.String} object.
     */
    public String get(URI term) {
        // Resolve term and create predicate iri
        IRI predicate = valueFactory.createIRI(term.toString());

        // Get the first value from the model
        Set<Value> values = model.filter(subject, predicate, null).objects();
        String value = ((Value) values.toArray()[0]).stringValue();

        return value;
    }

    /**
     * <p>get.</p>
     *
     * @param term a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String get(String term) {
        // Resolve term and create predicate iri
        URI uri = vocab.getURI(term);
        IRI predicate = valueFactory.createIRI(uri.toString());

        // Get the first value from the model
        Set<Value> values = model.filter(subject, predicate, null).objects();
        String value = ((Value) values.toArray()[0]).stringValue();

        return value;
    }

    /**
     * <p>getURI.</p>
     *
     * @return a {@link java.net.URI} object.
     */
    public URI getURI() {
        try {
            return new URI(subject.stringValue());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Subject is not a valid uri", e);
        }
    }

    /**
     * <p>getRecordId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRecordId() {
        // Use the id term set in vocab to find the id
        return get(vocab.getIdTerm());
    }

    /**
     * <p>asMap.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> asMap() {
        Map<String, String> record = new HashMap<>();

        // Iterate over model statements and convert to map
        for (Statement statement : model) {
            String term = statement.getPredicate().getLocalName();
            String value = statement.getObject().stringValue();

            if (!term.equals("type")) {
                record.put(term, value);
            }
        }

        return record;
    }

    /**
     * <p>asModel.</p>
     *
     * @return a {@link org.eclipse.rdf4j.model.Model} object.
     */
    public Model asModel() {
        return model;
    }
}
