/** DwcOccurrence.java
 *
 * Copyright 2018 President and Fellows of Harvard College
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
package org.datakurator.dwcloud;

import org.datakurator.ffdq.model.DataResource;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DwcOccurrence implements DataResource {
    private static final String DWCLOUD_FILE = "https://raw.githubusercontent.com/tucotuco/DwCVocabs/master/kurator/darwin_cloud.txt";
    private static final String ID_TERM = "dwc:occurrenceID";

    private static final Vocabulary vocab = new Vocabulary(DWCLOUD_FILE, ID_TERM);

    private ModelBuilder builder;

    private Map<String, String> record;
    private Model model;

    private String uuid = "urn:uuid:" + UUID.randomUUID().toString();
    private URI subject;

    public DwcOccurrence() {
        this.builder = new ModelBuilder();

        builder.setNamespace("ffdq", "http://example.com/ffdq/");

        try {
            subject = new URI(uuid);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        builder.defaultGraph().add(subject.toString(), RDF.TYPE, "ffdq:DataResource");
    }

    public DwcOccurrence(Model model) {
        this.model = model;

        try {
            this.subject = new URI(((Resource) model.subjects().toArray()[0]).stringValue());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public DwcOccurrence(Map<String, String> record) {
        this.record = record;
        this.builder = new ModelBuilder();

        builder.setNamespace("ffdq", "http://example.com/ffdq/");
        
        try {
            subject = new URI(uuid);
        } catch (URISyntaxException e1) { throw new RuntimeException(e1); }

        builder.defaultGraph().add(subject.toString(), RDF.TYPE, "ffdq:DataResource");

        for (String term : record.keySet()) {
            URI predicate = vocab.getURI(term);
            String object = record.get(term);

            builder.defaultGraph().add(subject.toString(), predicate.toString(), object);
        }
    }

    public void put(URI term, String value) {
        builder.defaultGraph().add(subject.toString(), term.toString(), value);
    }

    public void setURI(URI uri) {
        this.subject = uri;
    }

    @Override
    public URI getURI() {
        return subject;
    }

    public String getRecordId() {
        return asMap().get(vocab.getIdTerm());
    }

    @Override
    public Map<String, String> asMap() {
        if (record == null && model != null) {
            record = new HashMap<>();

            for (Statement statement : model) {
                String term = statement.getPredicate().getLocalName();
                String value = statement.getObject().stringValue();

                record.put(term, value);
            }
        }

        return record;
    }

    @Override
    public Model asModel() {
        if (model == null) {
            model = builder.build();
        }

        return model;
    }
}
