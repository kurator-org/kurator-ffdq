/**
 * RdfReader.java
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

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XSD;

/**
 * Reads model objects from RDF statements with RDF4J: the counterpart of {@link RdfWriter}.
 *
 * Each object is read once: reading the same subject again (e.g. a Specification shared by several
 * Methods, or a reference back to an object being read) returns the same instance.
 */
public class RdfReader {

    private final Model model;
    private final Map<Resource, Object> cache = new HashMap<>();

    /**
     * Read from a model.
     *
     * @param model the statements to read
     */
    public RdfReader(Model model) {
        this.model = model;
    }

    /** @return the model being read */
    public Model getModel() {
        return model;
    }

    /**
     * The object already read for a subject, if any.
     *
     * @param subject the subject
     * @return the object, or null
     */
    public Object cached(Resource subject) {
        return cache.get(subject);
    }

    /**
     * Record the object read for a subject, before reading its properties, so that references
     * back to it return the same object.
     *
     * @param subject the subject
     * @param object the object
     * @param <T> its type
     * @return the object
     */
    public <T> T remember(Resource subject, T object) {
        cache.put(subject, object);
        return object;
    }

    /**
     * Does a subject have a type?
     *
     * @param subject the subject
     * @param type the type, as a CURIE or IRI
     * @return true if subject rdf:type type is in the model
     */
    public boolean hasType(Resource subject, String type) {
        return model.contains(subject, RDF.TYPE, RdfWriter.iri(type));
    }

    /**
     * Is a subject described in the model (does it appear as a subject of any statement)?
     *
     * @param subject the subject
     * @return true if it has statements
     */
    public boolean exists(Resource subject) {
        return model.contains(subject, null, null);
    }

    /**
     * The values of a property, sorted by their string forms (so that reading is deterministic).
     *
     * @param subject the subject
     * @param predicate the predicate, as a CURIE or IRI
     * @return the values
     */
    public List<Value> objects(Resource subject, String predicate) {
        TreeSet<String> order = new TreeSet<>();
        Map<String, Value> byString = new HashMap<>();
        for (Statement s : model.filter(subject, RdfWriter.iri(predicate), null)) {
            String key = s.getObject().stringValue() + "\u0000" + s.getObject().toString();
            order.add(key);
            byString.put(key, s.getObject());
        }
        List<Value> result = new ArrayList<>();
        for (String key : order) { result.add(byString.get(key)); }
        return result;
    }

    /**
     * The first value of a property (in the order of {@link #objects(Resource, String)}).
     *
     * @param subject the subject
     * @param predicate the predicate
     * @return the value, or null
     */
    public Value object(Resource subject, String predicate) {
        List<Value> values = objects(subject, predicate);
        return values.isEmpty() ? null : values.get(0);
    }

    /** @return the string value of a property (a literal's lexical form, or an IRI), or null */
    public String string(Resource subject, String predicate) {
        Value v = object(subject, predicate);
        return v == null ? null : v.stringValue();
    }

    /** @return the string values of a property */
    public List<String> strings(Resource subject, String predicate) {
        List<String> result = new ArrayList<>();
        for (Value v : objects(subject, predicate)) { result.add(v.stringValue()); }
        return result;
    }

    /** @return the value of a property as a URI, or null */
    public URI uri(Resource subject, String predicate) {
        String s = string(subject, predicate);
        return s == null ? null : URI.create(s);
    }

    /** @return the values of a property as URIs */
    public List<URI> uris(Resource subject, String predicate) {
        List<URI> result = new ArrayList<>();
        for (String s : strings(subject, predicate)) { result.add(URI.create(s)); }
        return result;
    }

    /**
     * The value of a property as a date: an xsd:date (or the date part of an xsd:dateTime, or of a
     * plain literal beginning with a date).
     *
     * @param subject the subject
     * @param predicate the predicate
     * @return the date, or null
     */
    public LocalDate localDate(Resource subject, String predicate) {
        Value v = object(subject, predicate);
        if (!(v instanceof Literal)) { return null; }
        String s = v.stringValue().trim();
        if (s.length() >= 10) { s = s.substring(0, 10); }
        try {
            return LocalDate.parse(s);
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * The value of a property as a Date: an xsd:dateTime (or the start of an xsd:date, in UTC).
     *
     * @param subject the subject
     * @param predicate the predicate
     * @return the date, or null
     */
    public Date date(Resource subject, String predicate) {
        Value v = object(subject, predicate);
        if (!(v instanceof Literal)) { return null; }
        Literal l = (Literal) v;
        try {
            if (XSD.DATE.equals(l.getDatatype())) {
                return Date.from(LocalDate.parse(l.getLabel()).atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
            }
            return Date.from(OffsetDateTime.parse(l.getLabel()).toInstant());
        } catch (RuntimeException e) {
            try {
                return Date.from(Instant.parse(l.getLabel()));
            } catch (RuntimeException e2) {
                return null;
            }
        }
    }

    /** @return the value of a property as a Boolean, or null */
    public Boolean bool(Resource subject, String predicate) {
        Value v = object(subject, predicate);
        if (!(v instanceof Literal)) { return null; }
        return Boolean.valueOf(v.stringValue().trim());
    }

    /**
     * Read the object that is the value of a property.
     *
     * @param subject the subject
     * @param predicate the predicate
     * @param readFunction the static readRdf method of the object's class
     * @param <T> the object's class
     * @return the object, or null if the property has no resource value
     */
    public <T> T read(Resource subject, String predicate, BiFunction<RdfReader, Resource, T> readFunction) {
        Value v = object(subject, predicate);
        return v instanceof Resource ? readFunction.apply(this, (Resource) v) : null;
    }

    /**
     * Read the objects that are the values of a property.
     *
     * @param subject the subject
     * @param predicate the predicate
     * @param readFunction the static readRdf method of the objects' class
     * @param <T> the objects' class
     * @return the objects (those not readable, i.e. for which readFunction returns null, are left out)
     */
    public <T> List<T> readAll(Resource subject, String predicate, BiFunction<RdfReader, Resource, T> readFunction) {
        List<T> result = new ArrayList<>();
        for (Value v : objects(subject, predicate)) {
            if (v instanceof Resource) {
                T t = readFunction.apply(this, (Resource) v);
                if (t != null) { result.add(t); }
            }
        }
        return result;
    }

    /**
     * The types of a subject.
     *
     * @param subject the subject
     * @return the type IRIs
     */
    public Set<IRI> types(Resource subject) {
        Set<IRI> result = new java.util.LinkedHashSet<>();
        for (Value v : model.filter(subject, RDF.TYPE, null).objects()) {
            if (v instanceof IRI) { result.add((IRI) v); }
        }
        return result;
    }

    /**
     * The local identifier of a subject for a class whose subjects are a namespace followed by an
     * identifier (e.g. bdqdim:Completeness gives Completeness).
     *
     * @param subject the subject
     * @param namespace the namespace IRI or prefix
     * @return the identifier, or the whole IRI if it is not in the namespace
     */
    public static String localId(Resource subject, String namespace) {
        String ns = RdfWriter.subject("", namespace).stringValue();
        String s = subject.stringValue();
        return s.startsWith(ns) ? s.substring(ns.length()) : s;
    }

    /** @return an Optional of the value of a property */
    public Optional<Value> optional(Resource subject, String predicate) {
        return Optional.ofNullable(object(subject, predicate));
    }
}
