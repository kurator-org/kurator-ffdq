
/**
 *  Report.java
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

import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.Resource;


public class Report implements RdfMapped {

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:Report";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
    }

    /**
     * Read this object's own properties (and those of its superclasses) from RDF.
     *
     * @param r the reader
     * @param s the subject
     */
    protected void readRdfProperties(RdfReader r, Resource s) {
    }


    /** {@inheritDoc}  A Report has no identifier, and is written as a blank node. */
    @Override
    public Resource writeRdf(RdfWriter w) {
        Resource s = org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance().createBNode();
        w.type(s, RDF_TYPE);
        writeRdfProperties(w, s);
        return s;
    }

    /**
     * Read a Report from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the Report
     */
    public static Report readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof Report) { return (Report) cached; }
        Report o = r.remember(s, new Report());
        o.readRdfProperties(r, s);
        return o;
    }

}
