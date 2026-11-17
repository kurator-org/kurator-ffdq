
/**
 * BaseModel.java
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
package org.datakurator.ffdq.rdf;

import org.datakurator.ffdq.model.context.DataQualityNeed;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
public class BaseModel {
    private Repository repo;
    private RepositoryConnection conn;
    
    private final static Logger logger = Logger.getLogger(BaseModel.class.getName());

    /**
     * <p>Constructor for BaseModel.</p>
     */
    public BaseModel() {
        // Initialize an in-memory store
        repo = new SailRepository(new MemoryStore());

        conn = repo.getConnection();
    }

    /**
     * <p>load.</p>
     *
     * @param input a {@link java.io.InputStream} object.
     * @param format a {@link org.eclipse.rdf4j.rio.RDFFormat} object.
     * @throws java.io.IOException if any.
     * @throws RDFParseException 
     * @throws UnsupportedRDFormatException 
     */
    public void load(InputStream input, RDFFormat format) throws IOException, UnsupportedRDFormatException, RDFParseException {
        // Load RDF data from file
        Model model = Rio.parse(input, "", format);
        conn.add(model);
    }

    /**
     * <p>load.</p>
     *
     * @param model a {@link org.eclipse.rdf4j.model.Model} object.
     */
    public void load(Model model) {
        conn.add(model);
    }

    /**
     * <p>save.</p>
     *
     * @param obj a {@link java.lang.Object} object.
     */
    public void save(Object obj) {
        RdfWriter writer = new RdfWriter();
        if (obj instanceof RdfMapped) {
            ((RdfMapped) obj).writeRdf(writer);
        } else if (FfdqApiRdf.write(writer, obj) == null) {
            throw new RuntimeException("Not an RDF-mapped object: " + (obj == null ? "null" : obj.getClass().getName()));
        }
        conn.add(writer.getModel());
    }

    /**
     * A reader over all statements in the model.
     *
     * @return the reader
     */
    protected RdfReader reader() {
        Model model = new LinkedHashModel();
        conn.getStatements(null, null, null).forEach(model::add);
        return new RdfReader(model);
    }

    /**
     * The subject for an identifier: the identifier as an IRI, or for a bare UUID (as given in
     * Mechanism annotations) urn:uuid:{uuid}.
     *
     * @param reader the reader
     * @param guid the identifier
     * @return the subject, or null if neither is described in the model
     */
    private static Resource resolve(RdfReader reader, String guid) {
        if (guid == null) { return null; }
        if (guid.contains(":")) {
            IRI asIri = RdfWriter.iri(guid);
            if (reader.exists(asIri)) { return asIri; }
            return null;
        }
        IRI urn = RdfWriter.iri("urn:uuid:" + guid);
        return reader.exists(urn) ? urn : null;
    }

    /**
     * Read an object of a class from the model, with the class's readRdf(RdfReader, Resource) method.
     *
     * @param reader the reader
     * @param guid the identifier of the object
     * @param cls the class
     * @return the object, or null if there is no object of that class with that identifier
     */
    protected Object findOne(RdfReader reader, String guid, Class cls) {
        Resource r = resolve(reader, guid);
        if (r == null) {
            logger.log(Level.INFO, "Resource not found: Class: " + cls + " Guid: " + guid);
            return null;
        }
        Object result;
        try {
            Method readRdf = cls.getMethod("readRdf", RdfReader.class, Resource.class);
            result = readRdf.invoke(null, reader, r);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Could not read " + cls.getName() + " " + guid + ": " + e.getCause(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Not an RDF-mapped class: " + cls.getName(), e);
        }
        if (result == null || !cls.isInstance(result)) {
            logger.log(Level.WARNING, "Not a " + cls.getSimpleName() + ": " + r.stringValue());
            return null;
        }
        logger.log(Level.INFO, "Found: " + r.stringValue());
        return result;
    }

    /**
     * Read an object of a class from the model.
     *
     * @param guid the identifier of the object (an IRI, or a bare UUID for urn:uuid:{uuid})
     * @param cls the class
     * @return the object, or null if there is no object of that class with that identifier
     */
    public Object findOne(String guid, Class cls) {
        return findOne(reader(), guid, cls);
    }

    /**
     * Read the object of a class identified by the first solution of a SPARQL query.
     *
     * @param cls the class
     * @param sparql the query
     * @param field the query variable that identifies the object
     * @return the object, or null if the query has no solution
     */
    public Object findOne(Class cls, String sparql, String field) {
        TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate();
        logger.log(Level.INFO, field);
        logger.log(Level.INFO, sparql);
        if (!result.hasNext()) {
            return null;
        }
        BindingSet solution = result.next();
        String id = solution.getValue(field).stringValue();
        return findOne(id, cls);
    }

    /**
     * <p>findAll.</p>
     *
     * @param cls a {@link java.lang.Class} object.
     * @param sparql a {@link java.lang.String} object.
     * @param field a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public Map findAll(Class cls, String sparql, String field) {
        Map rdfBeans = new HashMap();
        RdfReader reader = reader();

        TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate();
        logger.log(Level.INFO, field);
        logger.log(Level.INFO, cls.getCanonicalName());
        logger.log(Level.INFO, sparql);

        while (result.hasNext()) {
            BindingSet solution = result.next();
            String id = solution.getValue(field).stringValue();
            
            logger.log(Level.INFO, "Looking for bean for: " + id);

            try { 
            	Object rdfBean = findOne(reader, id, cls);
            	rdfBeans.put(id, rdfBean);
            } catch (Exception e) { 
            	logger.log(Level.WARNING, e.getMessage());
            }
        }

        return rdfBeans;
    }

    /**
     * <p>findAll.</p> returning a map, finding objects by
     * one term in the sparql query and using a different term as 
     * the key for the returned map.
     *
     * @param cls a {@link java.lang.Class} object.
     * @param sparql a {@link java.lang.String} object.
     * @param field a {@link java.lang.String} object to find records by
     * @param keyForMap to use as they key of the reutrned map
     * @return a {@link java.util.Map} object.
     */
    public Map findAllMapBy(Class cls, String sparql, String field, String keyForMap) {
        Map<String, Object> rdfBeans = new HashMap<String, Object>();
        RdfReader reader = reader();

        TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate();
        logger.log(Level.INFO, field);
        logger.log(Level.INFO, cls.getCanonicalName());
        logger.log(Level.INFO, sparql);

        while (result.hasNext()) {
            BindingSet solution = result.next();
            String id = solution.getValue(field).stringValue();
            
            logger.log(Level.INFO, "Looking for bean for: " + id);

            try { 
            	Object rdfBean = findOne(reader, id, cls);
            	String key = solution.getValue(keyForMap).stringValue();
            	rdfBeans.put(key, rdfBean);
            } catch (Exception e) { 
            	logger.log(Level.WARNING, e.getMessage());
            }
        }

        return rdfBeans;
    }
    
    
    /**
     * Serialize the model to an output.
     *
     * @param format RDFFormat too write out
     * @param out OutputStream to write to
     * @param includeBindingClass if true, include rdfbeans:bindingClass assertions
     */
    public void write(RDFFormat format, OutputStream out, boolean includeBindingClass) {
        RDFWriter writer = Rio.createWriter(format, out);
        String query;
        if (includeBindingClass) {
            query = "PREFIX rdfbeans: <http://viceversatech.com/rdfbeans/2.0/> " + Namespace.getNamespacePrefixes() +
                    "CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o } ";
        } else {
            query = "PREFIX rdfbeans: <http://viceversatech.com/rdfbeans/2.0/> " + Namespace.getNamespacePrefixes() +
                    "CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o . MINUS { ?s rdfbeans:bindingClass ?o } } ";
        }
        try (RepositoryConnection conn = repo.getConnection()) {
            if (RDFFormat.RDFXML.equals(format)) {
                // For RDF/XML: collect all statements and sort by subject, predicate, then
                // object to guarantee that statements about the same subject are emitted in
                // a single contiguous <rdf:Description rdf:about="..."> block.
                StatementCollector collector = new StatementCollector();
                conn.prepareGraphQuery(QueryLanguage.SPARQL, query).evaluate(collector);
                List<Statement> statements = new ArrayList<>(collector.getStatements());
                statements.sort(Comparator.comparing((Statement s) -> s.getSubject().stringValue())
                        .thenComparing(s -> s.getPredicate().stringValue())
                        .thenComparing(s -> s.getObject().stringValue()));
                writer.startRDF();
                // Re-emit exactly the namespace prefix bindings that the SPARQL execution
                // produced (these mirror the PREFIX declarations in the query, including
                // rdf:, rdfs:, owl:, xsd: and any others that the engine supplies).
                for (Map.Entry<String, String> entry : collector.getNamespaces().entrySet()) {
                    writer.handleNamespace(entry.getKey(), entry.getValue());
                }
                for (Statement stmt : statements) {
                    writer.handleStatement(stmt);
                }
                writer.endRDF();
            } else {
                conn.prepareGraphQuery(QueryLanguage.SPARQL, query).evaluate(writer);
            }
        }
    }

    /**
     * <p>executeQuery.</p>
     *
     * @param sparql a {@link java.lang.String} object.
     * @param out a {@link java.io.OutputStream} object.
     */
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

    /**
     * <p>executeQuery.</p>
     *
     * @param sparql a {@link java.lang.String} object.
     * @return a {@link org.eclipse.rdf4j.query.TupleQueryResult} object.
     */
    public TupleQueryResult executeQuery(String sparql) {
         return conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate();
    }

    /**
     * <p>getResource.</p>
     *
     * @param subject a {@link java.lang.String} object.
     * @return a {@link org.eclipse.rdf4j.model.Model} object.
     */
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
