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

import org.datakurator.ffdq.model.Entity;
import org.datakurator.ffdq.model.ResultState;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

/**
 * RDF mapping of the ffdq-api model classes used in kurator-ffdq's model ({@link ResultState} and
 * {@link Entity}), which ffdq-api declares with RDFBeans annotations.  The RDF written is that of those
 * annotations: a ResultState is a {@value #RESPONSE_STATUS} with the subject {@value #RESULT_STATE_NS}{id}
 * and an rdfs:label; an Entity is a prov:Entity with the subject {id} and a prov:value.
 */
public final class FfdqApiRdf {

    /** The type of a ResultState, in the namespace ffdq-api declares for bdqffdq. */
    public static final String RESPONSE_STATUS = "http://rs.tdwg.org/bdqffdq/terms/ResponseStatus";
    /** The namespace of ResultState subjects in ffdq-api. */
    public static final String RESULT_STATE_NS = "http://example.com/ffdq/rs/";
    /** The type of an Entity. */
    public static final String ENTITY = "http://www.w3.org/ns/prov#Entity";

    private FfdqApiRdf() { }

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
            IRI s = RdfWriter.subject(state.getId(), RESULT_STATE_NS);
            if (w.begin(state)) {
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
     * Read a ResultState, with the subject's identifier and its label.
     *
     * @param r the reader
     * @param s the subject
     * @return the ResultState
     */
    public static ResultState readResultState(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof ResultState) { return (ResultState) cached; }
        String id = RdfReader.localId(s, RESULT_STATE_NS);
        String label = r.string(s, "rdfs:label");
        ResultState state = new ResultState();
        state.setId(id);
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
