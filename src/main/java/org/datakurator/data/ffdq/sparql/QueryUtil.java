package org.datakurator.data.ffdq.sparql;

import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.datakurator.data.ffdq.model.ValidationPolicy;
import org.datakurator.data.ffdq.model.needs.Criterion;
import org.datakurator.data.ffdq.model.needs.InformationElement;
import org.datakurator.data.ffdq.model.needs.ResourceType;
import org.datakurator.data.ffdq.model.needs.UseCase;
import org.datakurator.data.ffdq.model.solutions.*;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Example sparql queries on the ffdq rdf model
 */
public class QueryUtil {

    public static void main(String [] args) throws URISyntaxException {

        // Construct RdfBeans
        List<InformationElement> vie = new ArrayList<>();
        vie.add(new InformationElement("http://rs.tdwg.org/dwc/terms/eventDate"));
        vie.add(new InformationElement("http://rs.tdwg.org/dwc/terms/verbatimEventDate"));
        vie.add(new InformationElement("http://rs.tdwg.org/dwc/terms/year"));
        vie.add(new InformationElement("http://rs.tdwg.org/dwc/terms/month"));
        vie.add(new InformationElement("http://rs.tdwg.org/dwc/terms/day"));

        UseCase useCase = new UseCase();
        useCase.setUuid(UUID.fromString("dd78b90c-640f-4b9c-bece-564e525a43e0"));

        useCase.setLabel("Check for internal consistency of dates");
        useCase.setResourceType(ResourceType.SINGLE_RECORD);
        useCase.setInformationElements(vie);

        Criterion criterion = new Criterion("Value for day must be consistent with the provided month and year.");

        List<InformationElement> ie = new ArrayList<>();
        ie.add(new InformationElement("http://rs.tdwg.org/dwc/terms/day"));

        ContextualizedCriterion cc = new ContextualizedCriterion();
        cc.setCriterion(criterion);
        cc.setInformationElements(ie);
        cc.setResourceType(ResourceType.SINGLE_RECORD);

        ValidationPolicy policy = new ValidationPolicy();
        policy.setCriterionInContext(cc);
        policy.setUseCase(useCase);

        Mechanism mechanism = new Mechanism();
        mechanism.setLabel("Kurator: Date Validator - DwCEventDQ");

        Specification specification = new Specification();
        specification.setLabel("Compliant if dwc:day is an integer in the range 1 to 31 inclusive, not compliant otherwise. " +
                "Internal prerequisites not met if day is empty or an integer cannot be parsed from day.");

        List<Mechanism> mechanisms = new ArrayList<>();
        mechanisms.add(mechanism);

        Implementation implementation = new Implementation();
        implementation.setSpecification(specification);
        implementation.setImplementedBy(mechanisms);

        List<Specification> specifications = new ArrayList<>();
        specifications.add(specification);

        ValidationMethod method = new ValidationMethod();
        method.setSpecifications(specifications);
        method.setContextualizedCriterion(cc);

        // Initialize an in-memory store and run SPARQL query
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();

        try (RepositoryConnection conn = repo.getConnection()) {

            RDFBeanManager manager = new RDFBeanManager(conn);
            manager.add(useCase);
            manager.add(cc);
            manager.add(policy);
            manager.add(implementation);
            manager.add(method);

            RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, System.out);

            conn.prepareGraphQuery(QueryLanguage.SPARQL,
                    "CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o } ").evaluate(writer);

            System.out.println();

            conn.prepareTupleQuery(QueryLanguage.SPARQL,
                    "PREFIX ffdq: <http://example.com/ffdq/> " +
                            "PREFIX dwc: <http://rs.tdwg.org/dwc/terms/> " +
                            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +

                            "SELECT ?useCase ?specification ?mechanism ?policy " +

                            "WHERE { " +

                            // Find criterion in context from the validation
                            // policy for a given use case
                            "?policy a ffdq:ValidationPolicy ." +
                            "?policy ffdq:coversUseCase ?uc ." +
                            "?policy ffdq:criterionInContext ?cc ." +
                            "?uc rdfs:label ?useCase ." +

                            // Find the specification from the validation method
                            // referencing the criterion in context
                            "?vm a ffdq:ValidationMethod ." +
                            "?vm ffdq:hasContextualizedCriterion ?cc ." +
                            "?vm ffdq:hasSpecification ?s ." +
                            "?s rdfs:label ?specification ." +

                            // Find the mechanism from the implementation
                            // for the specification
                            "?i a ffdq:Implementation ." +
                            "?i ffdq:hasSpecification ?s ." +
                            "?i ffdq:implementedBy ?m ." +
                            "?m rdfs:label ?mechanism . " +

                            // Filter by a specific use case
                            "FILTER( ?uc = <urn:uuid:dd78b90c-640f-4b9c-bece-564e525a43e0> )" +

                            "} "
            ).evaluate(new SPARQLResultsTSVWriter(System.out));
        } catch (RDFBeanException e) {
            e.printStackTrace();
        }
    }
}
