package org.datakurator.data.ffdq.util;

import org.apache.commons.cli.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.datakurator.data.ffdq.Namespace;
import org.datakurator.data.ffdq.model.needs.*;
import org.datakurator.data.ffdq.model.solutions.*;
import org.datakurator.data.ffdq.runner.AssertionTest;
import org.datakurator.data.ffdq.runner.Parameter;
import org.datakurator.data.ffdq.runner.RDFBeanFactory;
import org.datakurator.postprocess.model.Measure;
import org.datakurator.postprocess.model.Validation;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by lowery on 9/12/17.
 */
public class ParseTestsXLSX {
    private static String DWC_NS;

    private static String[] DWC_TERMS = { "institutionID", "collectionID", "datasetID", "institutionCode",
            "collectionCode", "datasetName", "ownerInstitutionCode", "basisOfRecord", "informationWithheld",
            "dataGeneralizations", "dynamicProperties", "occurrenceID", "catalogNumber", "recordNumber", "recordedBy",
            "individualCount", "organismQuantity", "organismQuantityType", "sex", "lifeStage", "reproductiveCondition",
            "behavior", "establishmentMeans", "occurrenceStatus", "preparations", "disposition", "associatedMedia",
            "associatedReferences", "associatedSequences", "associatedTaxa", "otherCatalogNumbers", "occurrenceRemarks",
            "organismID", "organismName", "organismScope", "associatedOccurrences", "associatedOrganisms",
            "previousIdentifications", "organismRemarks", "materialSampleID", "eventID", "parentEventID", "fieldNumber",
            "eventDate", "eventTime", "startDayOfYear", "endDayOfYear", "year", "month", "day", "verbatimEventDate",
            "habitat", "samplingProtocol", "sampleSizeValue", "sampleSizeUnit", "samplingEffort", "fieldNotes",
            "eventRemarks", "locationID", "higherGeographyID", "higherGeography", "continent", "waterBody",
            "islandGroup", "island", "country", "countryCode", "stateProvince", "county", "municipality",
            "locality", "verbatimLocality", "minimumElevationInMeters", "maximumElevationInMeters", "verbatimElevation",
            "minimumDepthInMeters", "maximumDepthInMeters", "verbatimDepth", "minimumDistanceAboveSurfaceInMeters",
            "maximumDistanceAboveSurfaceInMeters", "locationAccordingTo", "locationRemarks", "decimalLatitude",
            "decimalLongitude", "geodeticDatum", "coordinateUncertaintyInMeters", "coordinatePrecision",
            "pointRadiusSpatialFit", "verbatimCoordinates", "verbatimLatitude", "verbatimLongitude",
            "verbatimCoordinateSystem", "verbatimSRS", "footprintWKT", "footprintSRS", "footprintSpatialFit",
            "georeferencedBy", "georeferencedDate", "georeferenceProtocol", "georeferenceSources",
            "georeferenceVerificationStatus", "georeferenceRemarks", "geologicalContextID",
            "earliestEonOrLowestEonothem", "latestEonOrHighestEonothem", "earliestEraOrLowestErathem",
            "latestEraOrHighestErathem", "earliestPeriodOrLowestSystem", "latestPeriodOrHighestSystem",
            "earliestEpochOrLowestSeries", "latestEpochOrHighestSeries", "earliestAgeOrLowestStage",
            "latestAgeOrHighestStage", "lowestBiostratigraphicZone", "highestBiostratigraphicZone",
            "lithostratigraphicTerms", "group", "formation", "member", "bed", "identificationID",
            "identificationQualifier", "typeStatus", "identifiedBy", "dateIdentified", "identificationReferences",
            "identificationVerificationStatus", "identificationRemarks", "taxonID", "scientificNameID",
            "acceptedNameUsageID", "parentNameUsageID", "originalNameUsageID", "nameAccordingToID", "namePublishedInID",
            "taxonConceptID", "scientificName", "acceptedNameUsage", "parentNameUsage", "originalNameUsage",
            "nameAccordingTo", "namePublishedIn", "namePublishedInYear", "higherClassification", "kingdom", "phylum",
            "class", "order", "family", "genus", "subgenus", "specificEpithet", "infraspecificEpithet", "taxonRank",
            "verbatimTaxonRank", "scientificNameAuthorship", "vernacularName", "nomenclaturalCode", "taxonomicStatus",
            "nomenclaturalStatus", "taxonRemarks", "measurementID", "measurementType", "measurementValue",
            "measurementAccuracy", "measurementUnit", "measurementDeterminedBy", "measurementDeterminedDate",
            "measurementMethod", "measurementRemarks", "resourceRelationshipID", "resourceID", "relatedResourceID",
            "relationshipOfResource", "relationshipAccordingTo", "relationshipEstablishedDate", "relationshipRemarks" };

    private final RDFFormat format;
    private final OutputStream out;

    private RDFWriter writer;

    private Repository repo;
    private RepositoryConnection conn;
    private RDFBeanManager manager;

    public ParseTestsXLSX(RDFFormat format, OutputStream out) {
        this.format = format;
        this.out = out;

        this.writer = Rio.createWriter(format, out);
        // Initialize an in-memory store and the rdf bean manager
        repo = new SailRepository(new MemoryStore());
        repo.initialize();

        conn = repo.getConnection();
        manager = new RDFBeanManager(conn);
    }

    public static void main(String[] args) throws ParseException, IOException {
        Options options = new Options();

        options.addOption("i", "in", true, "XLSX file that contains the \"Tests-current\" sheet.");
        options.addOption("o", "out", true, "Output file for the rdf representation of the tests.");
        options.addOption("f", "format", true, "Output format (RDFXML, TURTLE, JSON-LD)");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("i") && cmd.hasOption("o")) {

            // Get option values
            String input = cmd.getOptionValue("i");
            String output = cmd.getOptionValue("o");

            // Default output format is turtle
            RDFFormat format = RDFFormat.TURTLE;

            if (cmd.hasOption("f")) {
                String value = cmd.getOptionValue("f");

                switch (value) {
                    case "RDFXML":
                        format = RDFFormat.RDFXML;
                        break;
                    case "TURTLE":
                        format = RDFFormat.TURTLE;
                        break;
                    case "JSON-LD":
                        format = RDFFormat.JSONLD;
                        break;
                }
            }

            ParseTestsXLSX testsToRDF = new ParseTestsXLSX(format, new FileOutputStream(output));
            testsToRDF.parseXlsx(new FileInputStream(new File(input)));

        } else {

            // Print usage
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ffdq", options);
        }
    }

    private void parseXlsx(InputStream input) throws IOException {
        int LABEL_IDX = -1, GUID_IDX = -1, DESCRIPTION_IDX = -1, SPECIFICATION_IDX = -1,
                RESOURCE_TYPE_IDX = -1, ASSERTION_TYPE_IDX = -1, INFORMATION_ELEMENTS_IDX = -1,
                DIMENSION_IDX = -1;

        RDFBeanFactory beanFactory = new RDFBeanFactory();

        Workbook workbook = new XSSFWorkbook(input);
        Sheet sheet = workbook.getSheet("Tests-current");

        // Header cell iterator
        Iterator<Cell> header = sheet.getRow(0).cellIterator();

        // Find column numbers from the headers relevant to ffdq
        for (int i = 0; header.hasNext(); i++) {
            Cell cell = header.next();
            String value = cell.getStringCellValue();
            if (value.equals("Label")) {
                LABEL_IDX = i;
            } else if (value.equals("GUID")) {
                GUID_IDX = i;
            } else if (value.equals("Description (test - PASS)")) {
                DESCRIPTION_IDX = i;
            } else if (value.equals("Specification (Technical Description)")) {
                SPECIFICATION_IDX = i;
            } else if (value.equals("Resource Type")) {
                RESOURCE_TYPE_IDX = i;
            } else if (value.equals("Output Type")) {
                ASSERTION_TYPE_IDX = i;
            } else if (value.equals("Information Element")) {
                INFORMATION_ELEMENTS_IDX = i;
            } else if (value.equals("DQ Dimension")) {
                DIMENSION_IDX = i;
            }
        }

        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row

        while (rows.hasNext()) {
            Row row = rows.next();

            Cell labelCell = row.getCell(LABEL_IDX);
            Cell guidCell = row.getCell(GUID_IDX);

            if (labelCell != null && guidCell != null) {
                String label = labelCell.getStringCellValue().trim();
                String guid = "urn:uuid:" + guidCell.getStringCellValue().trim();
                String description = row.getCell(DESCRIPTION_IDX).getStringCellValue().trim();
                String specification = row.getCell(SPECIFICATION_IDX).getStringCellValue().trim();
                String resourceType = row.getCell(RESOURCE_TYPE_IDX).getStringCellValue().trim();
                String assertionType = row.getCell(ASSERTION_TYPE_IDX).getStringCellValue().trim();
                String informationElements = row.getCell(INFORMATION_ELEMENTS_IDX).getStringCellValue().trim();
                String dimension = row.getCell(DIMENSION_IDX).getStringCellValue().trim();

                boolean errors = false;
                if (!label.isEmpty() && label != null) {

                    if (guid.isEmpty() || guid == null) {
                        errors = true;
                        System.err.println("ERROR: Test " + label + " does not have an associated guid");
                    }

                    try {
                        if (assertionType.equalsIgnoreCase("Measure")) {
                            createMeasure(guid, specification, label, dimension, resourceType,
                                    informationElements);
                        } else if (assertionType.equalsIgnoreCase("Validation")) {
                            createValidation(guid, specification, label, description, resourceType,
                                    informationElements);
                        } else if (assertionType.equalsIgnoreCase("Amendment")) {
                            createAmendment(guid, specification, label, description, resourceType,
                                    informationElements);
                        } else {
                            errors = true;
                            System.err.println("ERROR: Test " + label + " has invalid assertion type " + assertionType);
                        }
                    } catch (RDFBeanException e) {
                        errors = true;
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public void writeComment(String comment) {
        StringBuilder sb = new StringBuilder();
        int length = comment.length()+4;

        for (int i = 0; i < length; i++) {
            sb.append("#");
        }

        sb.append("\n# " + comment + " #\n");

        for (int i = 0; i < length; i++) {
            sb.append("#");
        }

        try {
            out.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }

        writer.handleComment(sb.toString());
    }

    public void writeResource(Resource resource) {
        try (RepositoryConnection conn = repo.getConnection()) {
            GraphQueryResult graphResult = conn.prepareGraphQuery(QueryLanguage.SPARQL,
                    "PREFIX rdfbeans: <http://viceversatech.com/rdfbeans/2.0/> CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o . MINUS { ?s rdfbeans:bindingClass ?o } } ").evaluate();
            Model resultModel = QueryResults.asModel(graphResult).filter(resource, null, null);
            Rio.write(resultModel, writer);
        }
    }

    private InformationElement parseInformationElements(String informationElements) {
        try {
            URI uri = new URI(Namespace.DWC);
            List<URI> uris = new ArrayList<>();

            if (informationElements.equalsIgnoreCase("All Darwin Core Terms")) {
                for (String term : DWC_TERMS) {
                    uris.add(uri.resolve(term));
                }
            } else {
                StringTokenizer tokenizer = new StringTokenizer(informationElements, ",");

                while (tokenizer.hasMoreTokens()) {
                    String term = tokenizer.nextToken().trim();
                    uris.add(uri.resolve(term));
                }
            }

            return new InformationElement(uris);
    } catch (Exception e) {
        throw new RuntimeException("Could not construct dwc namespace uri for information elements \"" + informationElements + "\"", e);
    }

    }

    private void createMeasure(String guid, String specification, String label, String dimension, String resourceType,
                               String informationElements) throws RDFBeanException {

        writeComment(label);

        Dimension d = new Dimension(dimension);
        ResourceType rt = new ResourceType(resourceType);

        InformationElement ie = parseInformationElements(informationElements);

        ContextualizedDimension cd = new ContextualizedDimension();
        cd.setDimension(d);
        cd.setResourceType(rt);
        cd.setInformationElements(ie);

        Specification s = new Specification(guid, specification);

        MeasurementMethod measurementMethod = new MeasurementMethod();
        measurementMethod.setContextualizedDimension(cd);
        measurementMethod.setSpecification(s);

        writeResource(manager.add(s));
        writeResource(manager.add(d));
        writeResource(manager.add(rt));
        writeResource(manager.add(ie));
        writeResource(manager.add(cd));
        writeResource(manager.add(measurementMethod));
    }

    private void createValidation(String guid, String specification, String label, String criterion, String resourceType,
                               String informationElements) throws RDFBeanException {

        writeComment(label);

        Criterion c = new Criterion(criterion);
        ResourceType rt = new ResourceType(resourceType);

        InformationElement ie = parseInformationElements(informationElements);

        ContextualizedCriterion cc = new ContextualizedCriterion();
        cc.setCriterion(c);
        cc.setResourceType(rt);
        cc.setInformationElements(ie);

        Specification s = new Specification(guid, specification);

        ValidationMethod validationMethod = new ValidationMethod();
        validationMethod.setContextualizedCriterion(cc);
        validationMethod.setSpecification(s);

        writeResource(manager.add(s));
        writeResource(manager.add(c));
        writeResource(manager.add(rt));
        writeResource(manager.add(ie));
        writeResource(manager.add(cc));
        writeResource(manager.add(validationMethod));
    }

    private void createAmendment(String guid, String specification, String label, String enhancement, String resourceType,
                                  String informationElements) throws RDFBeanException {

        writeComment(label);

        Enhancement e = new Enhancement(enhancement);
        ResourceType rt = new ResourceType(resourceType);

        InformationElement ie = parseInformationElements(informationElements);

        ContextualizedEnhancement ce = new ContextualizedEnhancement();
        ce.setEnhancement(e);
        ce.setResourceType(rt);
        ce.setInformationElements(ie);

        Specification s = new Specification(guid, specification);

        AmendmentMethod amendmentMethod = new AmendmentMethod();
        amendmentMethod.setContextualizedEnhancement(ce);
        amendmentMethod.setSpecification(s);

        writeResource(manager.add(s));
        writeResource(manager.add(e));
        writeResource(manager.add(rt));
        writeResource(manager.add(ie));
        writeResource(manager.add(ce));
        writeResource(manager.add(amendmentMethod));
    }

}
