
/**
 *  DataQualityReport.java
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
package org.datakurator.ffdq.model.report;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

/**
 * A bdqffdq:DataQualityReport: a set of Responses (bdqffdq:containsResponse), e.g. those of one run
 * of a Mechanism over a data set.  Following the bdqffdq guide's discussion of PROV-O, it is also a
 * prov:Entity.
 */
public class DataQualityReport implements RdfMapped {

    private String id = "urn:uuid:" + UUID.randomUUID();
    private String label;
    private final List<Response> responses = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** @return a label for the report, or null */
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /** @return the Responses in this report */
    public List<Response> getResponses() {
        return responses;
    }

    /** @param response a Response to include in this report */
    public void addResponse(Response response) {
        if (response != null) {
            responses.add(response);
        }
    }

    // ---- RDF mapping (explicit RDF4J) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:DataQualityReport";

    /**
     * Write this object's own properties as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.value(s, "rdfs:label", getLabel());
        w.values(s, "bdqffdq:containsResponse", getResponses());
    }

    /**
     * Read this object's own properties from RDF.
     *
     * @param r the reader
     * @param s the subject
     */
    protected void readRdfProperties(RdfReader r, Resource s) {
        setLabel(r.string(s, "rdfs:label"));
        responses.addAll(r.readAll(s, "bdqffdq:containsResponse", Response::readRdf));
    }

    /** {@inheritDoc} */
    @Override
    public Resource writeRdf(RdfWriter w) {
        IRI s = RdfWriter.subject(getId(), null);
        if (w.begin(this)) {
            w.type(s, RDF_TYPE);
            w.type(s, "prov:Entity");
            writeRdfProperties(w, s);
        }
        return s;
    }

    /**
     * Read a DataQualityReport from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the DataQualityReport, the same instance each time for the same subject
     */
    public static DataQualityReport readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof DataQualityReport) { return (DataQualityReport) cached; }
        DataQualityReport o = r.remember(s, new DataQualityReport());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }
}
