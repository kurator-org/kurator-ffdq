package org.datakurator.ffdq.rdf;

import org.datakurator.ffdq.model.Criterion;
import org.datakurator.ffdq.model.InformationElement;
import org.datakurator.ffdq.model.ResourceType;
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.ContextualizedCriterion;
import org.datakurator.ffdq.model.solutions.AmendmentMethod;
import org.datakurator.ffdq.model.solutions.AssertionMethod;
import org.datakurator.ffdq.model.solutions.MeasurementMethod;
import org.datakurator.ffdq.model.solutions.ValidationMethod;
import org.datakurator.ffdq.runner.AssertionTest;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lowery on 11/14/17.
 */
public class FFDQModel extends BaseModel {

    public Map<String, Specification> findSpecificationsForMechanism(String mechanismGuid) {
        Set<String> guids = new HashSet<>();

        String sparql = "PREFIX ffdq: <http://example.com/ffdq/> " +
                "SELECT ?specification " +
                "WHERE { ?implementation ffdq:implementedBy <" + mechanismGuid + "> . " +
                "?implementation ffdq:hasSpecification ?specification }";

        return (Map<String, Specification>) findAll(Specification.class, sparql, "specification");
    }

    public AssertionMethod findMethodForSpecification(String testGuid) {
        String sparql = "PREFIX ffdq: <http://example.com/ffdq/> " +
                "SELECT ?method WHERE { " +
                "{ ?method a ffdq:MeasurementMethod } UNION " +
                "{ ?method a ffdq:ValidationMethod } UNION " +
                "{ ?method a ffdq:AmendmentMethod } . " +
                "?method ffdq:hasSpecification <" + testGuid + "> }";

        return (AssertionMethod) findOne(AssertionMethod.class, sparql, "method");
    }
}
