
/**
 * MeasurementMethod.java
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
package org.datakurator.ffdq.model.solutions;

import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

import java.util.List;

import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.Measure;

public class MeasurementMethod extends DataQualityMethod {
    private Measure cd;

    /**
     * <p>Constructor for MeasurementMethod.</p>
     */
    public MeasurementMethod() { }

    /**
     * <p>Constructor for MeasurementMethod.</p>
     *
     * @param specification a {@link org.datakurator.ffdq.model.Specification} object.
     * @param contextualizedDimension a {@link org.datakurator.ffdq.model.context.Measure} object.
     */
    public MeasurementMethod(Specification specification, Measure contextualizedDimension) {
        super.specification = specification;
        this.cd = contextualizedDimension;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return super.id;
    }
    
    /**
     * <p>getMeasure.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.context.Measure} object.
     */
    public Measure getMeasure() {
        return cd;
    }

    /**
     * <p>setContextualizedDimension.</p>
     *
     * @param cd a {@link org.datakurator.ffdq.model.context.Measure} object.
     */
    public void setContextualizedDimension(Measure cd) {
        this.cd = cd;
    }
    
    /**
     * <p>getLabel.</p>
     *
     * @return a generated label
     */
	public String getLabel() {
		StringBuilder labelBuilder = new StringBuilder();
		labelBuilder.append(this.getClass().getSimpleName()).append(": ");
		labelBuilder.append(cd.getLabel()).append(" with Specification ");
		labelBuilder.append(specification.getLabel());
		return labelBuilder.toString();
	}
    
    /**
     * Get the preferred label, currently, same as the rdfs;label.
     *
     * @return a skos:prefLabel
     */
    public String getPrefLabel() { 
    	return getLabel();
    }
    
	/**
	 * @return the historyNote
	 */
	public String getHistoryNote() {
		return super.historyNote;
	}
	
    /**
     * <p>Getter for the field <code>specification</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.Specification} object.
     */
    public Specification getSpecification() {
        return super.specification;
    }
    
	/**
	 * @return any notes
	 */
	public List<String> getNotes() {
		return super.notes;
	}

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:MeasurementMethod";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        super.writeRdfProperties(w, s);
        w.value(s, "bdqffdq:forMeasure", getMeasure());
        w.value(s, "rdfs:label", getLabel());
        w.value(s, "skos:prefLabel", getPrefLabel());
        w.value(s, "skos:historyNote", getHistoryNote());
        w.value(s, "bdqffdq:hasSpecification", getSpecification());
        w.values(s, "skos:note", getNotes());
    }

    /** {@inheritDoc} */
    @Override
    public Resource writeRdf(RdfWriter w) {
        IRI s = RdfWriter.subject(getId(), null);
        if (w.begin(this)) {
            w.type(s, RDF_TYPE, "bdqffdq:DataQualityMethod");
            writeRdfProperties(w, s);
        }
        return s;
    }

    /**
     * Read this object's own properties (and those of its superclasses) from RDF.
     *
     * @param r the reader
     * @param s the subject
     */
    protected void readRdfProperties(RdfReader r, Resource s) {
        super.readRdfProperties(r, s);
        this.cd = r.read(s, "bdqffdq:forMeasure", Measure::readRdf);
        // rdfs:label: computed by getLabel() from other properties; not read.
        // skos:prefLabel: computed by getPrefLabel() from other properties; not read.
        setHistoryNote(r.string(s, "skos:historyNote"));
        setSpecification(r.read(s, "bdqffdq:hasSpecification", Specification::readRdf));
        this.notes = new java.util.ArrayList<>(r.strings(s, "skos:note"));
    }

    /**
     * Read a MeasurementMethod from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the MeasurementMethod, the same instance each time for the same subject
     */
    public static MeasurementMethod readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof MeasurementMethod) { return (MeasurementMethod) cached; }
        MeasurementMethod o = r.remember(s, new MeasurementMethod());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
