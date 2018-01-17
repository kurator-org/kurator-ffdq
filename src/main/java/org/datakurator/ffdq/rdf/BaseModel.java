/** BaseModel.java
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

import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class BaseModel {
    private Repository repo;
    private RepositoryConnection conn;
    private RDFBeanManager manager;

    public BaseModel() {
        // Initialize an in-memory store and the rdf bean manager
        repo = new SailRepository(new MemoryStore());
        repo.initialize();

        conn = repo.getConnection();
        manager = new RDFBeanManager(conn);
    }

    public void load(InputStream input, RDFFormat format) throws IOException {
        // Load RDF data from file
        Model model = Rio.parse(input, "", format);
        conn.add(model);
    }

    public void load(Model model) {
        conn.add(model);
    }

    public void save(Object obj) {
        try {
            manager.add(obj);
        } catch (RDFBeanException e) {
            throw new RuntimeException("Could not process the rdf bean instance.", e);
        }
    }

    public Object findOne(String guid, Class cls) {
        try {
            Resource r = manager.getResource(guid, cls);
            return manager.get(r);
        } catch (RDFBeanException e) {
            throw new RuntimeException("Could not fetch the rdf bean instance.", e);
        }
    }

    public Object findOne(Class cls, String sparql, String field) {
        try {
            TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate();

            BindingSet solution = result.next();
            String id = solution.getValue(field).stringValue();

            Resource r = manager.getResource(id, cls);
            return manager.get(r);
        } catch (RDFBeanException e) {
            throw new RuntimeException("Could not fetch the rdf bean instance.", e);
        }
    }

    public Map findAll(Class cls, String sparql, String field) {
        Map rdfBeans = new HashMap();

        TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate();

        while (result.hasNext()) {
            BindingSet solution = result.next();
            String id = solution.getValue(field).stringValue();

            Object rdfBean = findOne(id, cls);
            rdfBeans.put(id, rdfBean);
        }

        return rdfBeans;
    }

    public void write(RDFFormat format, OutputStream out) {
        RDFWriter writer = Rio.createWriter(format, out);
        try (RepositoryConnection conn = repo.getConnection()) {
            conn.prepareGraphQuery(QueryLanguage.SPARQL,
                    "PREFIX rdfbeans: <http://viceversatech.com/rdfbeans/2.0/> " +
                            "CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o } ").evaluate(writer);

            // conn.prepareGraphQuery(QueryLanguage.SPARQL,
            //         "PREFIX rdfbeans: <http://viceversatech.com/rdfbeans/2.0/> " +
            //            "CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o . MINUS { ?s rdfbeans:bindingClass ?o } } ").evaluate(writer);
        }
    }

    public void executeQuery(String sparql, OutputStream out) {
        if (sparql.contains("CONSTRUCT")) {

            // Sparql CONSTRUCT
            RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, out);
            conn.prepareGraphQuery(QueryLanguage.SPARQL, sparql).evaluate(writer);

        } else if (sparql.contains("SELECT")) {

            // Sparql SELECT
            TupleQueryResultHandler handler = new SPARQLResultsTSVWriter(out);
            conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate(handler);

        }
    }

    public TupleQueryResult executeQuery(String sparql) {
         return conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate();
    }

    public Model getResource(String subject) {
        Model model = new LinkedHashModel();

        try (RepositoryConnection conn = repo.getConnection()) {
            conn.prepareGraphQuery(QueryLanguage.SPARQL,
                    "PREFIX rdfbeans: <http://viceversatech.com/rdfbeans/2.0/> " +
                            "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o . FILTER( ?s = <" + subject + "> ) } ").evaluate(new StatementCollector(model));
        }

        return model;
    }
}
