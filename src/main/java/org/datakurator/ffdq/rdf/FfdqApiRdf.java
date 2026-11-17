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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.datakurator.ffdq.model.Entity;
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
 * warning is logged.  An Entity is a prov:Entity with the subject {id} and a prov:value.
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
     * The RDF value of an ffdq-api object (a ResultState or an Entity), written to w.
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
        if (value instanceof Entity) {
            Entity entity = (Entity) value;
            IRI s = RdfWriter.subject(entity.getId(), null);
            if (w.begin(entity)) {
                w.type(s, ENTITY);
                w.value(s, "prov:value", entity.getValue());
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

    /**
     * Read an Entity, with its prov:value as a String (or, for a typed literal, its lexical form).
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
        if (v instanceof Literal) {
            entity.setValue(v.stringValue());
        } else if (v != null) {
            entity.setValue(v.stringValue());
        }
        return entity;
    }
}
