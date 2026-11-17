/**
 * FfdqApiRdf.java
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.datakurator.ffdq.model.Entity;
import org.datakurator.ffdq.model.ResponseQualifier;
import org.datakurator.ffdq.model.report.Result;
import org.datakurator.ffdq.model.ResultState;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

/**
 * RDF mapping of the ffdq-api model classes used in kurator-ffdq's model ({@link ResultState} and
 * {@link Entity}).
 *
 * A ResultState is written as the bdqffdq:ResponseStatus term of the BDQ standard with the same
 * label, e.g. RUN_HAS_RESULT as https://rs.tdwg.org/bdqffdq/terms/RUN_HAS_RESULT, typed
 * {@value #RESPONSE_STATUS} and with its label as rdfs:label.  A ResultState that is not a
 * bdqffdq:ResponseStatus term (e.g. NOT_RUN, which should not reach a report) is written under
 * {@value #NON_STANDARD_RESULT_STATE_NS}{id}, so that it cannot be mistaken for a BDQ term, and a
 * warning is logged.
 *
 * A {@link Result} (the Response.status, Response.result, Response.comment, and Response.qualifiers
 * of a Test) is written as properties of its Response, as the BDQ standard has them, by
 * {@link #writeResult(RdfWriter, Resource, Result)}:
 * <ul>
 * <li>the status as bdqffdq:hasResponseStatus;</li>
 * <li>a result that is a bdqffdq:ResponseResult term (COMPLIANT, NOT_COMPLIANT, COMPLETE, NOT_COMPLETE,
 * IS_ISSUE, POTENTIAL_ISSUE, NOT_ISSUE) as bdqffdq:hasResponseResult with that term (typed
 * bdqffdq:ResponseResult, with its label); any other result as
 * the literal bdqffdq:hasResponseResultValue: a map (the proposed changes of an Amendment) as JSON
 * (rdf:JSON), and an empty map not at all, a number as xsd:integer or xsd:decimal, and anything else as xsd:string;</li>
 * <li>the comment as bdqffdq:hasResponseComment;</li>
 * <li>each qualifier as bdqffdq:hasResponseQualifier, a bdqffdq:ResponseQualifier with an rdfs:label,
 * rdf:value, and rdfs:comment (a proof of concept: the standard prescribes no structure for it); an
 * ambiguous Result without an AMBIGUOUS qualifier gets one.</li>
 * </ul>
 * An Entity read from a report written before 4.0.0 is a prov:Entity with the subject {id} and a prov:value.
 */
public final class FfdqApiRdf {

    private static final Logger logger = Logger.getLogger(FfdqApiRdf.class.getName());

    /** The type of a ResultState: the bdqffdq:ResponseStatus class. */
    public static final String RESPONSE_STATUS = Namespace.BDQFFDQ + "ResponseStatus";
    /** The namespace of the bdqffdq:ResponseStatus terms. */
    public static final String RESPONSE_STATUS_NS = Namespace.BDQFFDQ;
    /** The labels of the bdqffdq:ResponseStatus terms of the BDQ standard. */
    public static final Set<String> RESPONSE_STATUS_TERMS = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            "RUN_HAS_RESULT", "AMENDED", "NOT_AMENDED", "FILLED_IN",
            "INTERNAL_PREREQUISITES_NOT_MET", "EXTERNAL_PREREQUISITES_NOT_MET")));
    /** The namespace of ResultStates that are not bdqffdq:ResponseStatus terms (a placeholder, not a BDQ namespace). */
    public static final String NON_STANDARD_RESULT_STATE_NS = "http://example.com/ffdq/rs/";
    /** The type of an Entity. */
    public static final String ENTITY = "http://www.w3.org/ns/prov#Entity";

    private FfdqApiRdf() { }

    /**
     * The IRI of a ResultState: its bdqffdq:ResponseStatus term, or, for a state that is not one,
     * {@value #NON_STANDARD_RESULT_STATE_NS}{id}.
     *
     * @param state the ResultState
     * @return the IRI
     */
    public static IRI resultStateIri(ResultState state) {
        String label = state.getLabel();
        if (label != null && RESPONSE_STATUS_TERMS.contains(label)) {
            return RdfWriter.subject(label, RESPONSE_STATUS_NS);
        }
        String id = state.getId() != null ? state.getId() : (label == null ? "" : label.toLowerCase());
        return RdfWriter.subject(id, NON_STANDARD_RESULT_STATE_NS);
    }

    /**
     * The RDF value of an ffdq-api ResultState, written to w.  (An Entity is not written as a
     * resource: its value is the Response.result, see {@link #writeResult(RdfWriter, Resource, Result)}.)
     *
     * @param w the writer
     * @param value the object
     * @return its subject, or null for null
     */
    public static Resource write(RdfWriter w, Object value) {
        if (value instanceof ResultState) {
            ResultState state = (ResultState) value;
            IRI s = resultStateIri(state);
            if (w.begin(state)) {
                if (!s.stringValue().startsWith(RESPONSE_STATUS_NS)) {
                    logger.warning("ResultState " + state.getLabel() + " is not a bdqffdq:ResponseStatus term; written as " + s);
                }
                w.type(s, RESPONSE_STATUS);
                w.value(s, "rdfs:label", state.getLabel());
            }
            return s;
        }
        return null;
    }

    /**
     * Read a ResultState: its label is the rdfs:label (or the local name of the IRI), and its id
     * the label in lower case, as ffdq-api gives them.  Reads the bdqffdq:ResponseStatus terms, and
     * also the http://example.com/ffdq/rs/{id} IRIs that earlier versions wrote for every state.
     *
     * @param r the reader
     * @param s the subject
     * @return the ResultState
     */
    public static ResultState readResultState(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof ResultState) { return (ResultState) cached; }
        String label = r.string(s, "rdfs:label");
        String iri = s.stringValue();
        String localName = iri.substring(Math.max(iri.lastIndexOf('/'), iri.lastIndexOf('#')) + 1);
        if (label == null) {
            label = localName.toUpperCase();
        }
        ResultState state = new ResultState();
        state.setId(iri.startsWith(NON_STANDARD_RESULT_STATE_NS) ? localName : label.toLowerCase());
        state.setLabel(label);
        return r.remember(s, state);
    }

    /** The labels of the bdqffdq:ResponseResult terms of the BDQ standard. */
    public static final Set<String> RESPONSE_RESULT_TERMS = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            "COMPLIANT", "NOT_COMPLIANT", "COMPLETE", "NOT_COMPLETE", "IS_ISSUE", "POTENTIAL_ISSUE", "NOT_ISSUE")));
    /** The datatype of JSON literals. */
    public static final String RDF_JSON = "http://www.w3.org/1999/02/22-rdf-syntax-ns#JSON";

    private static final ObjectMapper JSON = new ObjectMapper();

    /**
     * Write a Result as properties of its Response (see the class description).
     *
     * @param w the writer
     * @param response the Response's subject
     * @param result the Result, or null to write nothing
     */
    public static void writeResult(RdfWriter w, Resource response, Result result) {
        if (result == null) { return; }
        w.value(response, "bdqffdq:hasResponseStatus", result.getState());
        Entity entity = result.getEntity();
        Object value = entity == null ? null : entity.getValue();
        if (value instanceof Map && ((Map<?, ?>) value).isEmpty()) {
            // an empty proposal proposes nothing: no Response.result (e.g. for NOT_AMENDED)
            value = null;
        }
        if (value != null) {
            String label = value instanceof String ? ((String) value).trim() : null;
            if (label != null && RESPONSE_RESULT_TERMS.contains(label)) {
                IRI term = RdfWriter.iri(Namespace.BDQFFDQ + label);
                w.getModel().add(response, RdfWriter.iri("bdqffdq:hasResponseResult"), term);
                // as for statuses, the term's type and label are included, so that a report is
                // self-contained (and conforms to the bdqffdq shapes without the vocabulary loaded)
                w.type(term, "bdqffdq:ResponseResult");
                w.value(term, "rdfs:label", label);
            } else {
                w.getModel().add(response, RdfWriter.iri("bdqffdq:hasResponseResultValue"), resultValueLiteral(value));
            }
        }
        w.value(response, "bdqffdq:hasResponseComment", result.getComment());
        List<ResponseQualifier> qualifiers = new ArrayList<>(result.getQualifiers());
        if (Boolean.TRUE.equals(result.isAmbiguous())
                && qualifiers.stream().noneMatch(q -> ResponseQualifier.AMBIGUOUS.equals(q.getLabel()))) {
            qualifiers.add(new ResponseQualifier(ResponseQualifier.AMBIGUOUS, null, null));
        }
        for (ResponseQualifier qualifier : qualifiers) {
            String key = response.stringValue() + "|" + qualifier.getLabel() + "|" + qualifier.getValue() + "|" + qualifier.getComment();
            IRI q = RdfWriter.iri("urn:uuid:" + UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)));
            w.getModel().add(response, RdfWriter.iri("bdqffdq:hasResponseQualifier"), q);
            w.type(q, "bdqffdq:ResponseQualifier");
            w.value(q, "rdfs:label", qualifier.getLabel());
            w.value(q, "rdf:value", qualifier.getValue());
            w.value(q, "rdfs:comment", qualifier.getComment());
        }
    }

    /**
     * The literal of a Response.result that is not a bdqffdq:ResponseResult term.
     *
     * @param value the value
     * @return a JSON literal for a map, an xsd:integer or xsd:decimal literal for a number (or a string
     * that is one), otherwise an xsd:string literal
     */
    static Literal resultValueLiteral(Object value) {
        if (value instanceof Map) {
            try {
                return RdfWriter.typedLiteral(JSON.writeValueAsString(new TreeMap<>((Map<?, ?>) value)), RDF_JSON);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Could not write the result as JSON: " + value, e);
            }
        }
        String text = value.toString().trim();
        if (value instanceof Number || text.matches("[-+]?[0-9]+(\\.[0-9]+)?")) {
            try {
                if (text.matches("[-+]?[0-9]+")) {
                    return RdfWriter.typedLiteral(new BigInteger(text).toString(), "xsd:integer");
                }
                return RdfWriter.typedLiteral(new BigDecimal(text).toPlainString(), "xsd:decimal");
            } catch (NumberFormatException e) {
                // e.g. a Double in exponent form: written as a string below
            }
        }
        return RdfWriter.typedLiteral(value.toString(), "xsd:string");
    }

    /**
     * Read the Result of a Response from its bdqffdq properties (see the class description).
     *
     * @param r the reader
     * @param response the Response's subject
     * @return the Result, or null if the Response has none of those properties
     */
    public static Result readResult(RdfReader r, Resource response) {
        ResultState state = r.read(response, "bdqffdq:hasResponseStatus", FfdqApiRdf::readResultState);
        Value result = r.object(response, "bdqffdq:hasResponseResult");
        Value resultValue = r.object(response, "bdqffdq:hasResponseResultValue");
        String comment = r.string(response, "bdqffdq:hasResponseComment");
        List<Value> qualifierValues = r.objects(response, "bdqffdq:hasResponseQualifier");
        if (state == null && result == null && resultValue == null && comment == null && qualifierValues.isEmpty()) {
            return null;
        }
        Result o = new Result();
        o.setState(state);
        o.setComment(comment);
        Object value = null;
        if (result instanceof IRI) {
            value = ((IRI) result).getLocalName();
        } else if (resultValue instanceof Literal) {
            Literal l = (Literal) resultValue;
            value = l.getLabel();
            if (RDF_JSON.equals(l.getDatatype().stringValue())) {
                try {
                    value = JSON.readValue(l.getLabel(), new TypeReference<LinkedHashMap<String, String>>() { });
                } catch (JsonProcessingException e) {
                    value = l.getLabel();
                }
            }
        }
        if (value != null) {
            Entity entity = new Entity();
            entity.setValue(value);
            o.setEntity(entity);
        }
        List<ResponseQualifier> qualifiers = new ArrayList<>();
        for (Value q : qualifierValues) {
            if (q instanceof Resource) {
                Resource qr = (Resource) q;
                qualifiers.add(new ResponseQualifier(r.string(qr, "rdfs:label"), r.string(qr, "rdf:value"), r.string(qr, "rdfs:comment")));
            }
        }
        o.setQualifiers(qualifiers);
        o.setAmbiguous(qualifiers.stream().anyMatch(q -> ResponseQualifier.AMBIGUOUS.equals(q.getLabel())));
        return o;
    }

    /**
     * Read an Entity of a report written before 4.0.0, with its prov:value as a String (the lexical
     * form of a literal) or, for an IRI (the amended values of an Amendment), a URI.
     *
     * @param r the reader
     * @param s the subject
     * @return the Entity
     */
    public static Entity readEntity(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof Entity) { return (Entity) cached; }
        Entity entity = r.remember(s, new Entity());
        entity.setId(s.stringValue());
        Value v = r.object(s, "prov:value");
        if (v instanceof IRI) {
            entity.setValue(java.net.URI.create(v.stringValue()));
        } else if (v != null) {
            entity.setValue(v.stringValue());
        }
        return entity;
    }
}
