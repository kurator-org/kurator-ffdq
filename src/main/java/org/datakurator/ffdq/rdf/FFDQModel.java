package org.datakurator.ffdq.rdf;

import org.datakurator.ffdq.model.Criterion;
import org.datakurator.ffdq.model.InformationElement;
import org.datakurator.ffdq.model.ResourceType;
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.ContextualizedCriterion;
import org.datakurator.ffdq.model.solutions.AmendmentMethod;
import org.datakurator.ffdq.model.solutions.MeasurementMethod;
import org.datakurator.ffdq.model.solutions.ValidationMethod;
import org.datakurator.ffdq.runner.AssertionTest;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.List;

/**
 * Created by lowery on 11/14/17.
 */
public class FFDQModel extends BaseModel {

    public List<ValidationMethod> findValidationMethods(String mechanismGuid) {

        String sparql = "PREFIX ffdq: <http://example.com/ffdq/> " +
                "SELECT ?method " +
                "WHERE { ?implementation ffdq:implementedBy <" + mechanismGuid + "> . " +
                "?implementation ffdq:hasSpecification ?specification . " +
                "?method a ffdq:ValidationMethod . " +
                "?method ffdq:hasSpecification ?specification }";

        return (List<ValidationMethod>) findAll(ValidationMethod.class, sparql, "method");
    }

    public List<MeasurementMethod> findMeasurementMethods(String mechanismGuid) {

        String sparql = "PREFIX ffdq: <http://example.com/ffdq/> " +
                "SELECT ?method " +
                "WHERE { ?implementation ffdq:implementedBy <" + mechanismGuid + "> . " +
                "?implementation ffdq:hasSpecification ?specification . " +
                "?method a ffdq:MeasurementMethod . " +
                "?method ffdq:hasSpecification ?specification }";

        return (List<MeasurementMethod>) findAll(MeasurementMethod.class, sparql, "method");
    }

    public List<AmendmentMethod> findAmendmentMethods(String mechanismGuid) {

        String sparql = "PREFIX ffdq: <http://example.com/ffdq/> " +
                "SELECT ?method " +
                "WHERE { ?implementation ffdq:implementedBy <" + mechanismGuid + "> . " +
                "?implementation ffdq:hasSpecification ?specification . " +
                "?method a ffdq:AmendmentMethod . " +
                "?method ffdq:hasSpecification ?specification }";

        return (List<AmendmentMethod>) findAll(AmendmentMethod.class, sparql, "method");
    }
}
