package org.datakurator.ffdq.model;

import org.datakurator.ffdq.rdf.Namespace;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DwcOccurrence implements DataResource {
    private ModelBuilder builder;
    private Map<String, String> record;

    private String uuid = "urn:uuid:" + UUID.randomUUID().toString();;
    private URI subject;

    public DwcOccurrence(Map<String, String> record) {
        this.record = record;
        this.builder = new ModelBuilder();

        // TODO: Consolidate DwcOccurrence and FlatDarwinCore

        try { subject = new URI(uuid); } catch (URISyntaxException e1) { }

        for (String term : record.keySet()) {
            if (mapping.containsKey(term)) {
                URI predicate = mapping.get(term);
                String object = record.get(term);

                builder.defaultGraph().add(subject.toString(), predicate.toString(), object);
            }
        }
    }

    public void setURI(URI uri) {
        this.subject = uri;
    }

    @Override
    public URI getURI() {
        return subject;
    }

    @Override
    public Map<String, String> asMap() {
        return record;
    }

    @Override
    public Model asModel() {
        return builder.build();
    }

    private static Map<String, URI> mapping = new HashMap<>();

    static {
        try {
            URI DCTERMS = new URI(Namespace.DCTERMS);
            URI DWCIRI = new URI(Namespace.DWCIRI);
            URI DWC = new URI(Namespace.DWC);

            mapping.put("id", DCTERMS.resolve("identifier"));
            mapping.put("type", DCTERMS.resolve("type"));
            mapping.put("modified", DCTERMS.resolve("modified"));
            mapping.put("language", DCTERMS.resolve("language"));
            mapping.put("license", DCTERMS.resolve("license"));
            mapping.put("accessRights", DCTERMS.resolve("accessRights"));
            mapping.put("references", DCTERMS.resolve("references"));

            mapping.put("occurrenceID", DWCIRI.resolve("occurrenceID"));
            mapping.put("institutionID", DWCIRI.resolve("institutionID"));
            mapping.put("organismID", DWCIRI.resolve("organismID"));

            mapping.put("startDate", DWC.resolve("startDate"));
            mapping.put("endDate", DWC.resolve("endDate"));
            mapping.put("institutionCode", DWC.resolve("institutionCode"));
            mapping.put("collectionCode", DWC.resolve("collectionCode"));
            mapping.put("basisOfRecord", DWC.resolve("basisOfRecord"));
            mapping.put("informationWithheld", DWC.resolve("informationWithheld"));
            mapping.put("dynamicProperties", DWC.resolve("dynamicProperties"));
            mapping.put("catalogNumber", DWC.resolve("catalogNumber"));
            mapping.put("occurrenceRemarks", DWC.resolve("occurrenceRemarks"));
            mapping.put("recordNumber", DWC.resolve("recordNumber"));
            mapping.put("recordedBy", DWC.resolve("recordedBy"));
            mapping.put("individualCount", DWC.resolve("individualCount"));
            mapping.put("sex", DWC.resolve("sex"));
            mapping.put("lifeStage", DWC.resolve("lifeStage"));
            mapping.put("establishmentMeans", DWC.resolve("establishmentMeans"));
            mapping.put("preparations", DWC.resolve("preparations"));
            mapping.put("otherCatalogNumbers", DWC.resolve("otherCatalogNumbers"));
            mapping.put("associatedMedia", DWC.resolve("associatedMedia"));
            mapping.put("associatedSequences", DWC.resolve("associatedSequences"));
            mapping.put("associatedTaxa", DWC.resolve("associatedTaxa"));
            mapping.put("associatedOccurrences", DWC.resolve("associatedOccurrences"));
            mapping.put("previousIdentifications", DWC.resolve("previousIdentifications"));
            mapping.put("samplingProtocol", DWC.resolve("samplingProtocol"));
            mapping.put("eventDate", DWC.resolve("eventDate"));
            mapping.put("eventTime", DWC.resolve("eventTime"));
            mapping.put("endDayOfYear", DWC.resolve("endDayOfYear"));
            mapping.put("year", DWC.resolve("year"));
            mapping.put("month", DWC.resolve("month"));
            mapping.put("day", DWC.resolve("day"));
            mapping.put("verbatimEventDate", DWC.resolve("verbatimEventDate"));
            mapping.put("habitat", DWC.resolve("habitat"));
            mapping.put("fieldNumber", DWC.resolve("fieldNumber"));
            mapping.put("eventRemarks", DWC.resolve("eventRemarks"));
            mapping.put("higherGeography", DWC.resolve("higherGeography"));
            mapping.put("continent", DWC.resolve("continent"));
            mapping.put("waterBody", DWC.resolve("waterBody"));
            mapping.put("islandGroup", DWC.resolve("islandGroup"));
            mapping.put("island", DWC.resolve("island"));
            mapping.put("country", DWC.resolve("country"));
            mapping.put("stateProvince", DWC.resolve("stateProvince"));
            mapping.put("county", DWC.resolve("county"));
            mapping.put("locality", DWC.resolve("locality"));
            mapping.put("verbatimLocality", DWC.resolve("verbatimLocality"));
            mapping.put("minimumElevationInMeters", DWC.resolve("minimumElevationInMeters"));
            mapping.put("maximumElevationInMeters", DWC.resolve("maximumElevationInMeters"));
            mapping.put("minimumDepthInMeters", DWC.resolve("minimumDepthInMeters"));
            mapping.put("maximumDepthInMeters", DWC.resolve("maximumDepthInMeters"));
            mapping.put("locationAccordingTo", DWC.resolve("locationAccordingTo"));
            mapping.put("locationRemarks", DWC.resolve("locationRemarks"));
            mapping.put("verbatimCoordinates", DWC.resolve("verbatimCoordinates"));
            mapping.put("verbatimCoordinateSystem", DWC.resolve("verbatimCoordinateSystem"));
            mapping.put("decimalLatitude", DWC.resolve("decimalLatitude"));
            mapping.put("decimalLongitude", DWC.resolve("decimalLongitude"));
            mapping.put("geodeticDatum", DWC.resolve("geodeticDatum"));
            mapping.put("coordinateUncertaintyInMeters", DWC.resolve("coordinateUncertaintyInMeters"));
            mapping.put("georeferencedBy", DWC.resolve("georeferencedBy"));
            mapping.put("georeferencedDate", DWC.resolve("georeferencedDate"));
            mapping.put("georeferenceProtocol", DWC.resolve("georeferenceProtocol"));
            mapping.put("georeferenceSources", DWC.resolve("georeferenceSources"));
            mapping.put("georeferenceVerificationStatus", DWC.resolve("georeferenceVerificationStatus"));
            mapping.put("earliestEonOrLowestEonothem", DWC.resolve("earliestEonOrLowestEonothem"));
            mapping.put("earliestEraOrLowestErathem", DWC.resolve("earliestEraOrLowestErathem"));
            mapping.put("earliestPeriodOrLowestSystem", DWC.resolve("earliestPeriodOrLowestSystem"));
            mapping.put("earliestEpochOrLowestSeries", DWC.resolve("earliestEpochOrLowestSeries"));
            mapping.put("earliestAgeOrLowestStage", DWC.resolve("earliestAgeOrLowestStage"));
            mapping.put("formation", DWC.resolve("formation"));
            mapping.put("identifiedBy", DWC.resolve("identifiedBy"));
            mapping.put("dateIdentified", DWC.resolve("dateIdentified"));
            mapping.put("identificationReferences", DWC.resolve("identificationReferences"));
            mapping.put("identificationRemarks", DWC.resolve("identificationRemarks"));
            mapping.put("identificationQualifier", DWC.resolve("identificationQualifier"));
            mapping.put("identificationVerificationStatus", DWC.resolve("identificationVerificationStatus"));
            mapping.put("typeStatus", DWC.resolve("typeStatus"));
            mapping.put("scientificName", DWC.resolve("scientificName"));
            mapping.put("higherClassification", DWC.resolve("higherClassification"));
            mapping.put("kingdom", DWC.resolve("kingdom"));
            mapping.put("phylum", DWC.resolve("phylum"));
            mapping.put("class", DWC.resolve("class"));
            mapping.put("order", DWC.resolve("order"));
            mapping.put("family", DWC.resolve("family"));
            mapping.put("genus", DWC.resolve("genus"));
            mapping.put("specificEpithet", DWC.resolve("specificEpithet"));
            mapping.put("infraspecificEpithet", DWC.resolve("infraspecificEpithet"));
            mapping.put("taxonRank", DWC.resolve("taxonRank"));
            mapping.put("nomenclaturalCode", DWC.resolve("nomenclaturalCode"));

        } catch (Exception e) {

        }
    }
}
