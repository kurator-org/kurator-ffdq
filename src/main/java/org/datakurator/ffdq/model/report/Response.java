
/**
 *  Response.java
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

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.DataQualityNeed;
import org.datakurator.ffdq.model.solutions.DataQualityMethod;
import org.datakurator.ffdq.rdf.FfdqApiRdf;
import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

/**
 * A bdqffdq:Response: the report from one execution of one Test on one bdqffdq:DataResource.
 *
 * <p>The RDF follows the BDQ standard (bdqffdq, its SHACL shapes, and the guides):
 * <ul>
 * <li>the Response has its bdqffdq:appliesTo DataResource, and the properties of its {@link Result}:
 * bdqffdq:hasResponseStatus, bdqffdq:hasResponseResult or bdqffdq:hasResponseResultValue,
 * bdqffdq:hasResponseComment, and bdqffdq:hasResponseQualifier (see {@link FfdqApiRdf});</li>
 * <li>a bdqffdq:Implementation bdqffdq:producesResponse the Response, bdqffdq:usesSpecification its
 * Specification, and is bdqffdq:implementedBy its Mechanism (one Implementation for each Specification
 * and Mechanism);</li>
 * <li>the Test is reached from the Specification through its Method (e.g. a bdqffdq:ValidationMethod
 * with bdqffdq:hasSpecification and bdqffdq:forValidation), which is written with the Response: the
 * Method given by {@link #setMethod(DataQualityMethod)}, or else one made from the Specification and
 * the Test;</li>
 * <li>for provenance (PROV-O, as the bdqffdq guide describes): the Response prov:wasGeneratedBy a
 * prov:Activity, which prov:used the Specification and the DataResource, prov:wasAssociatedWith the
 * Mechanism (a prov:SoftwareAgent), has prov:startedAtTime and prov:endedAtTime when known, and
 * prov:wasInformedBy the Activity of the AmendmentResponse whose proposals the Test was run on (for
 * Tests run after Amendments).</li>
 * </ul>
 *
 * <p>Reports written before 4.0.0 (with prov:hadPlan, prov:wasAttributedTo, prov:generated with a
 * bdqffdq:Result, bdqffdq:criterionInContext and the like) are also read.
 */
public abstract class Response implements RdfMapped {
    private Specification specification;
    private Mechanism mechanism;
    private Result result;
    private URI dataResource;
    private DataQualityMethod method;
    private Instant startedAtTime;
    private Instant endedAtTime;

    public URI getDataResource() {
        return dataResource;
    }

    public void setDataResource(URI dataResource) {
        this.dataResource = dataResource;
    }

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public Mechanism getMechanism() {
        return mechanism;
    }

    public void setMechanism(Mechanism mechanism) {
        this.mechanism = mechanism;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * @return the Method that relates the Specification to the Test, if given or read
     */
    public DataQualityMethod getMethod() {
        return method;
    }

    /**
     * @param method the Method that relates the Specification to the Test (e.g. the ValidationMethod
     * from the Test definitions); if not given, one is made from the Specification and the Test
     */
    public void setMethod(DataQualityMethod method) {
        this.method = method;
    }

    /** @return when the execution of the Test began, if known */
    public Instant getStartedAtTime() {
        return startedAtTime;
    }

    public void setStartedAtTime(Instant startedAtTime) {
        this.startedAtTime = startedAtTime;
    }

    /** @return when the execution of the Test ended, if known */
    public Instant getEndedAtTime() {
        return endedAtTime;
    }

    public void setEndedAtTime(Instant endedAtTime) {
        this.endedAtTime = endedAtTime;
    }

    // ---- what each kind of Response contributes ----

    /** @return the Test (Validation, Measure, Amendment, or Issue) this Response is about, or null */
    protected abstract DataQualityNeed dataQualityNeed();

    /** @param need the Test read from RDF */
    protected abstract void dataQualityNeedFromRdf(DataQualityNeed need);

    /**
     * @param specification the Specification
     * @param need the Test
     * @return a new Method of the kind for this Response relating them
     */
    protected abstract DataQualityMethod newMethod(Specification specification, DataQualityNeed need);

    /** @return the type of Method for this Response, e.g. bdqffdq:ValidationMethod */
    protected abstract String methodType();

    /**
     * @param method a Method of the type {@link #methodType()}
     * @return its Test
     */
    protected abstract DataQualityNeed needOf(DataQualityMethod method);

    /** @return the property that linked this Response to its Test before 4.0.0, e.g. bdqffdq:criterionInContext */
    protected abstract String oldTestProperty();

    /**
     * @param r the reader
     * @param s a Test
     * @return the Test read as the kind for this Response
     */
    protected abstract DataQualityNeed readNeed(RdfReader r, Resource s);

    /** @return the AmendmentResponse whose proposals this Test was run on, or null */
    protected AmendmentResponse informedBy() {
        return null;
    }

    /** @param amendmentResponse the AmendmentResponse read from RDF */
    protected void informedByFromRdf(AmendmentResponse amendmentResponse) {
    }

    // ---- RDF mapping (explicit RDF4J) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:Response";

    /**
     * The IRI of the Implementation of a Specification by a Mechanism: a name-based UUID, so that the
     * Responses of one Specification and Mechanism share one Implementation however they are saved.
     *
     * @param specification the Specification, or null
     * @param mechanism the Mechanism, or null
     * @return the IRI
     */
    static IRI implementationIri(Specification specification, Mechanism mechanism) {
        String key = "implementation|" + (specification == null ? "" : specification.getId()) + "|"
                + (mechanism == null ? "" : mechanism.getId());
        return RdfWriter.iri("urn:uuid:" + UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * @param response a Response's subject
     * @return the IRI of the prov:Activity that generated it (a name-based UUID)
     */
    static IRI activityIri(Resource response) {
        String key = "activity|" + response.stringValue();
        return RdfWriter.iri("urn:uuid:" + UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.value(s, "bdqffdq:appliesTo", getDataResource());
        FfdqApiRdf.writeResult(w, s, getResult());

        Specification spec = getSpecification();
        Mechanism mech = getMechanism();
        if (spec != null || mech != null) {
            IRI implementation = implementationIri(spec, mech);
            w.type(implementation, "bdqffdq:Implementation");
            w.getModel().add(implementation, RdfWriter.iri("bdqffdq:producesResponse"), s);
            w.value(implementation, "bdqffdq:usesSpecification", spec);
            w.value(implementation, "bdqffdq:implementedBy", mech);
        }

        DataQualityMethod m = getMethod();
        DataQualityNeed need = dataQualityNeed();
        if (m == null && spec != null && need != null) {
            m = newMethod(spec, need);
            Resource needSubject = (Resource) w.toValue(need);
            String key = "method|" + methodType() + "|" + spec.getId() + "|" + needSubject.stringValue();
            m.setId("urn:uuid:" + UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)));
        }
        if (m != null) {
            w.toValue(m);
        }

        IRI activity = activityIri(s);
        w.type(activity, "prov:Activity");
        w.getModel().add(s, RdfWriter.iri("prov:wasGeneratedBy"), activity);
        w.value(activity, "prov:used", spec);
        w.value(activity, "prov:used", getDataResource());
        if (mech != null) {
            Resource agent = (Resource) w.toValue(mech);
            w.type(agent, "prov:SoftwareAgent");
            w.getModel().add(activity, RdfWriter.iri("prov:wasAssociatedWith"), agent);
        }
        w.value(activity, "prov:startedAtTime", getStartedAtTime());
        w.value(activity, "prov:endedAtTime", getEndedAtTime());
        AmendmentResponse amendment = informedBy();
        if (amendment != null) {
            Resource amendmentSubject = amendment.writeRdf(w);
            w.getModel().add(activity, RdfWriter.iri("prov:wasInformedBy"), activityIri(amendmentSubject));
        }
    }

    /**
     * Read this object's own properties (and those of its superclasses) from RDF.
     *
     * @param r the reader
     * @param s the subject
     */
    protected void readRdfProperties(RdfReader r, Resource s) {
        setDataResource(r.uri(s, "bdqffdq:appliesTo"));

        Result read = FfdqApiRdf.readResult(r, s);
        setResult(read != null ? read : r.read(s, "prov:generated", Result::readRdf));

        Resource implementation = r.subject("bdqffdq:producesResponse", s);
        if (implementation != null) {
            setSpecification(r.read(implementation, "bdqffdq:usesSpecification", Specification::readRdf));
            setMechanism(r.read(implementation, "bdqffdq:implementedBy", Mechanism::readRdf));
        }
        if (getSpecification() == null) {
            setSpecification(r.read(s, "prov:hadPlan", Specification::readRdf));
        }
        if (getMechanism() == null) {
            setMechanism(r.read(s, "prov:wasAttributedTo", Mechanism::readRdf));
        }

        Value specIri = getSpecification() == null ? null : r.object(implementation == null ? s : implementation,
                implementation == null ? "prov:hadPlan" : "bdqffdq:usesSpecification");
        if (specIri != null) {
            for (Resource m : r.subjects("bdqffdq:hasSpecification", specIri)) {
                if (r.hasType(m, methodType())) {
                    setMethod(DataQualityMethod.readRdf(r, m));
                    break;
                }
            }
        }
        DataQualityNeed need = getMethod() == null ? null : needOf(getMethod());
        if (need == null) {
            need = r.read(s, oldTestProperty(), this::readNeed);
        }
        if (need != null) {
            dataQualityNeedFromRdf(need);
        }

        Value activity = r.object(s, "prov:wasGeneratedBy");
        if (activity instanceof Resource) {
            Date started = r.date((Resource) activity, "prov:startedAtTime");
            Date ended = r.date((Resource) activity, "prov:endedAtTime");
            setStartedAtTime(started == null ? null : started.toInstant());
            setEndedAtTime(ended == null ? null : ended.toInstant());
            Value informing = r.object((Resource) activity, "prov:wasInformedBy");
            Resource amendment = informing == null ? null : r.subject("prov:wasGeneratedBy", informing);
            if (amendment != null && r.hasType(amendment, AmendmentResponse.RDF_TYPE)) {
                informedByFromRdf(AmendmentResponse.readRdf(r, amendment));
            }
        }
        if (informedBy() == null) {
            AmendmentResponse old = r.read(s, "prov:wasInformedBy", AmendmentResponse::readRdf);
            if (old != null) {
                informedByFromRdf(old);
            }
        }
    }

    /**
     * Read a ValidationResponse, IssueResponse, MeasurementResponse, or AmendmentResponse, as its type.
     *
     * @param r the reader
     * @param s the subject
     * @return the Response, or null if the subject is none of these
     */
    public static Response readRdf(RdfReader r, Resource s) {
        if (r.hasType(s, ValidationResponse.RDF_TYPE)) { return ValidationResponse.readRdf(r, s); }
        if (r.hasType(s, IssueResponse.RDF_TYPE)) { return IssueResponse.readRdf(r, s); }
        if (r.hasType(s, MeasurementResponse.RDF_TYPE) || r.hasType(s, MeasurementResponse.OLD_RDF_TYPE)) { return MeasurementResponse.readRdf(r, s); }
        if (r.hasType(s, AmendmentResponse.RDF_TYPE)) { return AmendmentResponse.readRdf(r, s); }
        return null;
    }
}
