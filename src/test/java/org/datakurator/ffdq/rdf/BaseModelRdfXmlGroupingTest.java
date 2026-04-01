package org.datakurator.ffdq.rdf;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Regression test verifying that sorted RDF statements about the same subject
 * are grouped into a single {@code <rdf:Description rdf:about="...">} block
 * when written with the RDF/XML format.
 *
 * <p>This covers the fix in {@link BaseModel#write} that collects and sorts
 * all statements by (subject, predicate, object) before emitting RDF/XML, so
 * that each subject appears in exactly one contiguous block.
 */
public class BaseModelRdfXmlGroupingTest {

    private static final String SUBJECT = "https://rs.tdwg.org/bdqtest/terms/example-2025-03-07";

    @Test
    public void testSortedStatementsProduceSingleRdfDescriptionBlockPerSubject() throws Exception {
        ValueFactory vf = SimpleValueFactory.getInstance();

        // Three predicates for the same subject, added in unsorted order to
        // simulate the arbitrary order that a SPARQL CONSTRUCT result can
        // return statements.
        List<Statement> statements = new ArrayList<>();
        statements.add(vf.createStatement(
                vf.createIRI(SUBJECT),
                vf.createIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                vf.createIRI("https://rs.tdwg.org/bdqffdq/terms/Validation")));
        statements.add(vf.createStatement(
                vf.createIRI(SUBJECT),
                vf.createIRI("http://purl.org/dc/terms/description"),
                vf.createLiteral("A test description")));
        statements.add(vf.createStatement(
                vf.createIRI(SUBJECT),
                vf.createIRI("http://www.w3.org/2000/01/rdf-schema#label"),
                vf.createLiteral("Example test")));

        // Sort exactly as BaseModel.write does for RDF/XML.
        statements.sort(Comparator.comparing((Statement s) -> s.getSubject().stringValue())
                .thenComparing(s -> s.getPredicate().stringValue())
                .thenComparing(s -> s.getObject().stringValue()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, baos);
        writer.startRDF();
        for (Statement stmt : statements) {
            writer.handleStatement(stmt);
        }
        writer.endRDF();

        String xml = baos.toString("UTF-8");

        // The subject IRI must appear as rdf:about exactly once.
        String needle = "rdf:about=\"" + SUBJECT + "\"";
        int firstIdx = xml.indexOf(needle);
        assertTrue("Expected rdf:about for subject to appear in output", firstIdx >= 0);

        int secondIdx = xml.indexOf(needle, firstIdx + needle.length());
        assertEquals("All predicates for the same subject must be in a single rdf:Description block"
                + " (rdf:about appeared more than once)", -1, secondIdx);
    }

    @Test
    public void testSortedStatementsTwoSubjectsEachHaveOneBlock() throws Exception {
        ValueFactory vf = SimpleValueFactory.getInstance();

        String subjectA = "https://rs.tdwg.org/bdqtest/terms/aaa";
        String subjectB = "https://rs.tdwg.org/bdqtest/terms/bbb";
        String pred     = "http://www.w3.org/2000/01/rdf-schema#label";

        List<Statement> statements = new ArrayList<>();
        statements.add(vf.createStatement(vf.createIRI(subjectA), vf.createIRI(pred), vf.createLiteral("A label 1")));
        statements.add(vf.createStatement(vf.createIRI(subjectB), vf.createIRI(pred), vf.createLiteral("B label 1")));
        statements.add(vf.createStatement(vf.createIRI(subjectA), vf.createIRI(pred), vf.createLiteral("A label 2")));
        statements.add(vf.createStatement(vf.createIRI(subjectB), vf.createIRI(pred), vf.createLiteral("B label 2")));

        // Sort by subject → predicates of subjectA are contiguous, then subjectB.
        statements.sort(Comparator.comparing((Statement s) -> s.getSubject().stringValue())
                .thenComparing(s -> s.getPredicate().stringValue())
                .thenComparing(s -> s.getObject().stringValue()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, baos);
        writer.startRDF();
        for (Statement stmt : statements) {
            writer.handleStatement(stmt);
        }
        writer.endRDF();

        String xml = baos.toString("UTF-8");

        assertSingleBlock(xml, subjectA);
        assertSingleBlock(xml, subjectB);
    }

    private static void assertSingleBlock(String xml, String subjectIri) {
        String needle = "rdf:about=\"" + subjectIri + "\"";
        int first = xml.indexOf(needle);
        assertTrue("Expected rdf:about for <" + subjectIri + "> in output", first >= 0);
        int second = xml.indexOf(needle, first + needle.length());
        assertEquals("Subject <" + subjectIri + "> must appear in exactly one rdf:Description block",
                -1, second);
    }
}
