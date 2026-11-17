/**
 * RdfWriter.java
 *
 * Copyright 2026 President and Fellows of Harvard College
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XSD;

/**
 * Writes model objects ({@link RdfMapped}) as RDF statements with RDF4J.
 *
 * Datatypes are explicit: {@link String} values are written as xsd:string literals, {@link URI} values as
 * IRIs, {@link LocalDate} as xsd:date, {@link Date}, {@link Instant}, {@link OffsetDateTime} and
 * {@link ZonedDateTime} as xsd:dateTime (in UTC for Date and Instant), {@link LocalDateTime} as xsd:dateTime
 * without a time zone, {@link Boolean} as xsd:boolean, and numbers with their XSD numeric types.
 *
 * Each object is written once, however often it is referred to.  Terms are given as CURIEs
 * (e.g. bdqffdq:hasSpecification) with the prefixes in {@link #PREFIXES}, or as full IRIs.
 */
public class RdfWriter {

    /** The namespace prefixes used in the CURIEs of the model classes' RDF mappings. */
    public static final Map<String, String> PREFIXES;
    static {
        Map<String, String> p = new HashMap<>(Namespace.nsPrefixes);
        p.put("rdf", RDF.NAMESPACE);
        p.put("rdfs", RDFS.NAMESPACE);
        p.put("xsd", XSD.NAMESPACE);
        p.put("prov", "http://www.w3.org/ns/prov#");
        p.put("dcterms", Namespace.DCTERMS);
        p.put("skos", Namespace.SKOS);
        p.put("bdqffdq", Namespace.BDQFFDQ);
        p.put("bdqdim", Namespace.BDQDIM);
        p.put("bdqcrit", Namespace.BDQCRIT);
        p.put("bdqenh", Namespace.BDQENH);
        p.put("bdqval", Namespace.BDQVAL);
        PREFIXES = Collections.unmodifiableMap(p);
    }

    private static final ValueFactory VF = SimpleValueFactory.getInstance();

    private final Model model;
    private final Set<Object> written = Collections.newSetFromMap(new IdentityHashMap<>());

    /** Write into a new, empty model. */
    public RdfWriter() {
        this(new LinkedHashModel());
    }

    /**
     * Write into a model.
     *
     * @param model the model to add statements to
     */
    public RdfWriter(Model model) {
        this.model = model;
    }

    /** @return the model the statements are written to */
    public Model getModel() {
        return model;
    }

    /**
     * Expand a CURIE with {@link #PREFIXES}, or take a full IRI as it is.
     *
     * @param curieOrIri e.g. bdqffdq:Validation, or https://rs.tdwg.org/bdqffdq/terms/Validation
     * @return the IRI
     */
    public static IRI iri(String curieOrIri) {
        int colon = curieOrIri.indexOf(':');
        if (colon > 0) {
            String ns = PREFIXES.get(curieOrIri.substring(0, colon));
            if (ns != null && !curieOrIri.startsWith(curieOrIri.substring(0, colon) + "://")) {
                return VF.createIRI(ns + curieOrIri.substring(colon + 1));
            }
        }
        return VF.createIRI(curieOrIri);
    }

    /**
     * The subject IRI of an object: its identifier, or a namespace followed by its identifier
     * (unless the identifier already begins with the namespace).
     *
     * @param id the identifier, e.g. urn:uuid:..., or a local name when namespace is given
     * @param namespace the namespace IRI or prefix (e.g. https://rs.tdwg.org/bdqdim/terms/ or bdqdim:), or null
     * @return the subject IRI
     */
    public static IRI subject(String id, String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return iri(id);
        }
        String ns = namespace.endsWith(":") && PREFIXES.containsKey(namespace.substring(0, namespace.length() - 1))
                ? PREFIXES.get(namespace.substring(0, namespace.length() - 1)) : namespace;
        // an identifier already in the namespace (e.g. a mechanism given as urn:uuid:{uuid}) is not prefixed again
        return VF.createIRI(id.startsWith(ns) ? id : ns + id);
    }

    /**
     * Begin writing an object: true the first time the object is seen, false if it has already been written.
     *
     * @param object the object
     * @return true if the object is to be written now
     */
    public boolean begin(Object object) {
        return written.add(object);
    }

    /**
     * Write the type of a subject, and, for a class with superclasses, that it is a subclass of them
     * (e.g. bdqffdq:Validation rdfs:subClassOf bdqffdq:DataQualityNeed).
     *
     * @param subject the subject
     * @param type the type
     * @param superTypes the superclasses of the type, if any
     */
    public void type(Resource subject, String type, String... superTypes) {
        IRI t = iri(type);
        model.add(subject, RDF.TYPE, t);
        for (String superType : superTypes) {
            model.add(t, RDFS.SUBCLASSOF, iri(superType));
        }
    }

    /**
     * Write a value (if not null): an RDF value, a model object, a URI, a string, a date, a boolean, or a number.
     *
     * @param subject the subject
     * @param predicate the predicate, as a CURIE or IRI
     * @param value the value, or null to write nothing
     */
    public void value(Resource subject, String predicate, Object value) {
        Value v = toValue(value);
        if (v != null) {
            model.add(subject, iri(predicate), v);
        }
    }

    /**
     * Write each value of a collection (if not null), as {@link #value(Resource, String, Object)}.
     *
     * @param subject the subject
     * @param predicate the predicate
     * @param values the values, or null
     */
    public void values(Resource subject, String predicate, Collection<?> values) {
        if (values != null) {
            for (Object value : values) {
                value(subject, predicate, value);
            }
        }
    }

    /**
     * The RDF value of a Java value; a model object is written and its subject returned.
     *
     * @param value the value
     * @return the RDF value, or null for null
     */
    public Value toValue(Object value) {
        if (value == null) { return null; }
        if (value instanceof Value) { return (Value) value; }
        if (value instanceof RdfMapped) { return ((RdfMapped) value).writeRdf(this); }
        Resource ffdqApi = FfdqApiRdf.write(this, value);
        if (ffdqApi != null) { return ffdqApi; }
        if (value instanceof URI) { return VF.createIRI(value.toString()); }
        if (value instanceof String) { return VF.createLiteral((String) value, XSD.STRING); }
        return literal(value);
    }

    /**
     * A typed literal for a date, time, boolean, or number.
     *
     * @param value the value
     * @return the literal
     */
    public static Literal literal(Object value) {
        if (value instanceof LocalDate) {
            return VF.createLiteral(((LocalDate) value).format(DateTimeFormatter.ISO_LOCAL_DATE), XSD.DATE);
        }
        if (value instanceof Date) {
            return VF.createLiteral(DateTimeFormatter.ISO_INSTANT.format(((Date) value).toInstant()), XSD.DATETIME);
        }
        if (value instanceof Instant) {
            return VF.createLiteral(DateTimeFormatter.ISO_INSTANT.format((Instant) value), XSD.DATETIME);
        }
        if (value instanceof OffsetDateTime) {
            return VF.createLiteral(((OffsetDateTime) value).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), XSD.DATETIME);
        }
        if (value instanceof ZonedDateTime) {
            return VF.createLiteral(((ZonedDateTime) value).toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), XSD.DATETIME);
        }
        if (value instanceof LocalDateTime) {
            return VF.createLiteral(((LocalDateTime) value).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), XSD.DATETIME);
        }
        if (value instanceof Boolean) { return VF.createLiteral((Boolean) value); }
        if (value instanceof Integer) { return VF.createLiteral((Integer) value); }
        if (value instanceof Long) { return VF.createLiteral((Long) value); }
        if (value instanceof Short) { return VF.createLiteral((Short) value); }
        if (value instanceof Byte) { return VF.createLiteral((Byte) value); }
        if (value instanceof Double) { return VF.createLiteral((Double) value); }
        if (value instanceof Float) { return VF.createLiteral((Float) value); }
        if (value instanceof BigDecimal) { return VF.createLiteral((BigDecimal) value); }
        if (value instanceof BigInteger) { return VF.createLiteral((BigInteger) value); }
        return VF.createLiteral(value.toString(), XSD.STRING);
    }
}
