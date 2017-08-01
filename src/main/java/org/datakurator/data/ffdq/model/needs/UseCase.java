/**  UseCase.java
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
package org.datakurator.data.ffdq.model.needs;

import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.datakurator.data.ffdq.model.ValidationPolicy;
import org.datakurator.data.ffdq.model.solutions.*;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("ffdq:UseCase")
public class UseCase {
    private UUID uuid;
    private String label;

    public UseCase() {
        this.uuid = UUID.randomUUID();
    }

    @RDFSubject()
    public String getId() {
        return "urn:uuid:" + uuid.toString();
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @RDF("rdfs:label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
