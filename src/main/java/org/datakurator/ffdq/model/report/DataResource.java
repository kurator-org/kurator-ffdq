/**  DataResource.java
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
 */
package org.datakurator.ffdq.model.report;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Data Resource is an instance of data and the target to the DQ assessment and management.
 *
 * Data Resources have a property called “resource type.” Resource type, in the context of the conceptual framework,
 * can be "single record" or "multi-record (dataset)". This property is important because it affects the method for
 * measuring, validating and improving a Data Resource. For example, coordinate completeness of a single record could
 * be measured qualitatively by checking whether the latitude and longitude of the record are filled or not; whereas
 * the coordinate completeness of a dataset could be measured quantitatively, measuring the percentage of records in
 * the dataset which have the latitude and longitude fields filled. Both measurements are for coordinate completeness,
 * but they are measured in different ways due to the different resource type.
 *
 * Veiga AK, Saraiva AM, Chapman AD, Morris PJ, Gendreau C, Schigel D, et al. (2017) A conceptual framework for quality
 * assessment and management of biodiversity data. PLoS ONE 12(6): e0178731.
 *
 * @see <a href="https://doi.org/10.1371/journal.pone.0178731">https://doi.org/10.1371/journal.pone.0178731</a>
 *
 */

@RDFNamespaces({
        "dwc = http://rs.tdwg.org/dwc/terms/",
        "dcterms = http://purl.org/dc/terms/"
})
@RDFBean("dwc:Occurrence")
public class DataResource {
    private String guid = "urn:uuid:" + UUID.randomUUID().toString();

    private Map<String, String> record;

    public DataResource() {
        this.record = new HashMap<>();
    }

    public DataResource(Map<String, String> record) {
        this.record = record;
    }

    @RDFSubject
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @RDF("dcterms:identifier")
    public String getId() {
        return record.get("id");
    }

    public void setId(String id) {
        record.put("id", id);
    }

    @RDF("dcterms:type")
    public String getType() {
        return record.get("type");
    }

    public void setType(String type) {
        record.put("type", type);
    }

    @RDF("dcterms:modified")
    public String getModified() {
        return record.get("modified");
    }

    public void setModified(String modified) {
        record.put("modified", modified);
    }

    @RDF("dcterms:language")
    public String getLanguage() {
        return record.get("language");
    }

    public void setLanguage(String language) {
        record.put("language", language);
    }

    @RDF("dcterms:license")
    public String getLicense() {
        return record.get("license");
    }

    public void setLicense(String license) {
        record.put("license", license);
    }

    @RDF("dcterms:accessRights")
    public String getAccessRights() {
        return record.get("accessRights");
    }

    public void setAccessRights(String accessRights) {
        record.put("accessRights", accessRights);
    }
    @RDF("dcterms:references")
    public String getReferences() {
        return record.get("references");
    }

    public void setReferences(String references) {
        record.put("references", references);
    }

    @RDF("dwc:institutionID")
    public String getInstitutionID() {
        return record.get("institutionID");
    }

    public void setInstitutionID(String institutionID) {
        record.put("institutionID", institutionID);
    }

    @RDF("dwc:institutionCode")
    public String getInstitutionCode() {
        return record.get("institutionCode");
    }

    public void setInstitutionCode(String institutionCode) {
        record.put("institutionCode", institutionCode);
    }

    @RDF("dwc:collectionCode")
    public String getCollectionCode() {
        return record.get("collectionCode");
    }

    public void setCollectionCode(String collectionCode) {
        record.put("collectionCode", collectionCode);
    }

    @RDF("dwc:basisOfRecord")
    public String getBasisOfRecord() {
        return record.get("basisOfRecord");
    }

    public void setBasisOfRecord(String basisOfRecord) {
        record.put("basisOfRecord", basisOfRecord);
    }

    @RDF("dwc:informationWithheld")
    public String getInformationWithheld() {
        return record.get("informationWithheld");
    }

    public void setInformationWithheld(String informationWithheld) {
        record.put("informationWithheld", informationWithheld);
    }

    @RDF("dwc:dynamicProperties")
    public String getDynamicProperties() {
        return record.get("dynamicProperties");
    }

    public void setDynamicProperties(String dynamicProperties) {
        record.put("dynamicProperties", dynamicProperties);
    }

    @RDF("dwc:occurrenceID")
    public String getOccurrenceID() {
        return record.get("occurrenceID");
    }

    public void setOccurrenceID(String occurrenceID) {
        record.put("occurrenceID", occurrenceID);
    }

    @RDF("dwc:catalogNumber")
    public String getCatalogNumber() {
        return record.get("catalogNumber");
    }

    public void setCatalogNumber(String catalogNumber) {
        record.put("catalogNumber", catalogNumber);
    }

    @RDF("dwc:occurrenceRemarks")
    public String getOccurrenceRemarks() {
        return record.get("occurrenceRemarks");
    }

    public void setOccurrenceRemarks(String occurrenceRemarks) {
        record.put("occurrenceRemarks", occurrenceRemarks);
    }

    @RDF("dwc:recordNumber")
    public String getRecordNumber() {
        return record.get("recordNumber");
    }

    public void setRecordNumber(String recordNumber) {
        record.put("recordNumber", recordNumber);
    }

    @RDF("dwc:recordedBy")
    public String getRecordedBy() {
        return record.get("recordedBy");
    }

    public void setRecordedBy(String recordedBy) {
        record.put("recordedBy", recordedBy);
    }

    @RDF("dwc:individualCount")
    public String getIndividualCount() {
        return record.get("individualCount");
    }

    public void setIndividualCount(String individualCount) {
        record.put("individualCount", individualCount);
    }

    @RDF("dwc:sex")
    public String getSex() {
        return record.get("sex");
    }

    public void setSex(String sex) {
        record.put("sex", sex);
    }

    @RDF("dwc:lifeStage")
    public String getLifeStage() {
        return record.get("lifeStage");
    }

    public void setLifeStage(String lifeStage) {
        record.put("lifeStage", lifeStage);
    }

    @RDF("dwc:establishmentMeans")
    public String getEstablishmentMeans() {
        return record.get("establishmentMeans");
    }

    public void setEstablishmentMeans(String establishmentMeans) {
        record.put("establishmentMeans", establishmentMeans);
    }

    @RDF("dwc:preparations")
    public String getPreparations() {
        return record.get("preparations");
    }

    public void setPreparations(String preparations) {
        record.put("preparations", preparations);
    }

    @RDF("dwc:otherCatalogNumbers")
    public String getOtherCatalogNumbers() {
        return record.get("otherCatalogNumbers");
    }

    public void setOtherCatalogNumbers(String otherCatalogNumbers) {
        record.put("otherCatalogNumbers", otherCatalogNumbers);
    }

    @RDF("dwc:associatedMedia")
    public String getAssociatedMedia() {
        return record.get("associatedMedia");
    }

    public void setAssociatedMedia(String associatedMedia) {
        record.put("associatedMedia", associatedMedia);
    }

    @RDF("dwc:associatedSequences")
    public String getAssociatedSequences() {
        return record.get("associatedSequences");
    }

    public void setAssociatedSequences(String associatedSequences) {
        record.put("associatedSequences", associatedSequences);
    }

    @RDF("dwc:associatedTaxa")
    public String getAssociatedTaxa() {
        return record.get("associatedTaxa");
    }

    public void setAssociatedTaxa(String associatedTaxa) {
        record.put("associatedTaxa", associatedTaxa);
    }

    @RDF("dwc:organismID")
    public String getOrganismID() {
        return record.get("organismID");
    }

    public void setOrganismID(String organismID) {
        record.put("organismID", organismID);
    }

    @RDF("dwc:associatedOccurrences")
    public String getAssociatedOccurrences() {
        return record.get("associatedOccurrences");
    }

    public void setAssociatedOccurrences(String associatedOccurrences) {
        record.put("associatedOccurrences", associatedOccurrences);
    }

    @RDF("dwc:previousIdentifications")
    public String getPreviousIdentifications() {
        return record.get("previousIdentifications");
    }

    public void setPreviousIdentifications(String previousIdentifications) {
        record.put("previousIdentifications", previousIdentifications);
    }

    @RDF("dwc:samplingProtocol")
    public String getSamplingProtocol() {
        return record.get("samplingProtocol");
    }

    public void setSamplingProtocol(String samplingProtocol) {
        record.put("samplingProtocol", samplingProtocol);
    }

    @RDF("dwc:eventDate")
    public String getEventDate() {
        return record.get("eventDate");
    }

    public void setEventDate(String eventDate) {
        record.put("eventDate", eventDate);
    }

    @RDF("dwc:eventTime")
    public String getEventTime() {
        return record.get("eventTime");
    }

    public void setEventTime(String eventTime) {
        record.put("eventTime", eventTime);
    }

    @RDF("dwc:endDayOfYear")
    public String getEndDayOfYear() {
        return record.get("endDayOfYear");
    }

    public void setEndDayOfYear(String endDayOfYear) {
        record.put("endDayOfYear", endDayOfYear);
    }

    @RDF("dwc:year")
    public String getYear() {
        return record.get("year");
    }

    public void setYear(String year) {
        record.put("year", year);
    }

    @RDF("dwc:month")
    public String getMonth() {
        return record.get("month");
    }

    public void setMonth(String month) {
        record.put("month", month);
    }

    @RDF("dwc:day")
    public String getDay() {
        return record.get("day");
    }

    public void setDay(String day) {
        record.put("day", day);
    }

    @RDF("dwc:verbatimEventDate")
    public String getVerbatimEventDate() {
        return record.get("verbatimEventDate");
    }

    public void setVerbatimEventDate(String verbatimEventDate) {
        record.put("verbatimEventDate", verbatimEventDate);
    }

    @RDF("dwc:habitat")
    public String getHabitat() {
        return record.get("habitat");
    }

    public void setHabitat(String habitat) {
        record.put("habitat", habitat);
    }

    @RDF("dwc:fieldNumber")
    public String getFieldNumber() {
        return record.get("fieldNumber");
    }

    public void setFieldNumber(String fieldNumber) {
        record.put("fieldNumber", fieldNumber);
    }

    @RDF("dwc:eventRemarks")
    public String getEventRemarks() {
        return record.get("eventRemarks");
    }

    public void setEventRemarks(String eventRemarks) {
        record.put("eventRemarks", eventRemarks);
    }

    @RDF("dwc:higherGeography")
    public String getHigherGeography() {
        return record.get("higherGeography");
    }

    public void setHigherGeography(String higherGeography) {
        record.put("higherGeography", higherGeography);
    }

    @RDF("dwc:continent")
    public String getContinent() {
        return record.get("continent");
    }

    public void setContinent(String continent) {
        record.put("continent", continent);
    }

    @RDF("dwc:waterBody")
    public String getWaterBody() {
        return record.get("waterBody");
    }

    public void setWaterBody(String waterBody) {
        record.put("waterBody", waterBody);
    }

    @RDF("dwc:islandGroup")
    public String getIslandGroup() {
        return record.get("islandGroup");
    }

    public void setIslandGroup(String islandGroup) {
        record.put("islandGroup", islandGroup);
    }

    @RDF("dwc:island")
    public String getIsland() {
        return record.get("island");
    }

    public void setIsland(String island) {
        record.put("island", island);
    }

    @RDF("dwc:country")
    public String getCountry() {
        return record.get("country");
    }

    public void setCountry(String country) {
        record.put("country", country);
    }

    @RDF("dwc:stateProvince")
    public String getStateProvince() {
        return record.get("stateProvince");
    }

    public void setStateProvince(String stateProvince) {
        record.put("stateProvince", stateProvince);
    }

    @RDF("dwc:county")
    public String getCounty() {
        return record.get("county");
    }

    public void setCounty(String county) {
        record.put("county", county);
    }

    @RDF("dwc:locality")
    public String getLocality() {
        return record.get("locality");
    }

    public void setLocality(String locality) {
        record.put("locality", locality);
    }

    @RDF("dwc:verbatimLocality")
    public String getVerbatimLocality() {
        return record.get("verbatimLocality");
    }

    public void setVerbatimLocality(String verbatimLocality) {
        record.put("verbatimLocality", verbatimLocality);
    }

    @RDF("dwc:minimumElevationInMeters")
    public String getMinimumElevationInMeters() {
        return record.get("minimumElevationInMeters");
    }

    public void setMinimumElevationInMeters(String minimumElevationInMeters) {
        record.put("minimumElevationInMeters", minimumElevationInMeters);
    }

    @RDF("dwc:maximumElevationInMeters")
    public String getMaximumElevationInMeters() {
        return record.get("maximumElevationInMeters");
    }

    public void setMaximumElevationInMeters(String maximumElevationInMeters) {
        record.put("maximumElevationInMeters", maximumElevationInMeters);
    }

    @RDF("dwc:minimumDepthInMeters")
    public String getMinimumDepthInMeters() {
        return record.get("minimumDepthInMeters");
    }

    public void setMinimumDepthInMeters(String minimumDepthInMeters) {
        record.put("minimumDepthInMeters", minimumDepthInMeters);
    }

    @RDF("dwc:maximumDepthInMeters")
    public String getMaximumDepthInMeters() {
        return record.get("maximumDepthInMeters");
    }

    public void setMaximumDepthInMeters(String maximumDepthInMeters) {
        record.put("maximumDepthInMeters", maximumDepthInMeters);
    }

    @RDF("dwc:locationAccordingTo")
    public String getLocationAccordingTo() {
        return record.get("locationAccordingTo");
    }

    public void setLocationAccordingTo(String locationAccordingTo) {
        record.put("locationAccordingTo", locationAccordingTo);
    }

    @RDF("dwc:locationRemarks")
    public String getLocationRemarks() {
        return record.get("locationRemarks");
    }

    public void setLocationRemarks(String locationRemarks) {
        record.put("locationRemarks", locationRemarks);
    }

    @RDF("dwc:verbatimCoordinates")
    public String getVerbatimCoordinates() {
        return record.get("verbatimCoordinates");
    }

    public void setVerbatimCoordinates(String verbatimCoordinates) {
        record.put("verbatimCoordinates", verbatimCoordinates);
    }

    @RDF("dwc:verbatimCoordinateSystem")
    public String getVerbatimCoordinateSystem() {
        return record.get("verbatimCoordinateSystem");
    }

    public void setVerbatimCoordinateSystem(String verbatimCoordinateSystem) {
        record.put("verbatimCoordinateSystem", verbatimCoordinateSystem);
    }

    @RDF("dwc:decimalLatitude")
    public String getDecimalLatitude() {
        return record.get("decimalLatitude");
    }

    public void setDecimalLatitude(String decimalLatitude) {
        record.put("decimalLatitude", decimalLatitude);
    }

    @RDF("dwc:decimalLongitude")
    public String getDecimalLongitude() {
        return record.get("decimalLongitude");
    }

    public void setDecimalLongitude(String decimalLongitude) {
        record.put("decimalLongitude", decimalLongitude);
    }

    @RDF("dwc:geodeticDatum")
    public String getGeodeticDatum() {
        return record.get("geodeticDatum");
    }

    public void setGeodeticDatum(String geodeticDatum) {
        record.put("geodeticDatum", geodeticDatum);
    }

    @RDF("dwc:coordinateUncertaintyInMeters")
    public String getCoordinateUncertaintyInMeters() {
        return record.get("coordinateUncertaintyInMeters");
    }

    public void setCoordinateUncertaintyInMeters(String coordinateUncertaintyInMeters) {
        record.put("coordinateUncertaintyInMeters", coordinateUncertaintyInMeters);
    }

    @RDF("dwc:georeferencedBy")
    public String getGeoreferencedBy() {
        return record.get("georeferencedBy");
    }

    public void setGeoreferencedBy(String georeferencedBy) {
        record.put("georeferencedBy", georeferencedBy);
    }

    @RDF("dwc:georeferencedDate")
    public String getGeoreferencedDate() {
        return record.get("georeferencedDate");
    }

    public void setGeoreferencedDate(String georeferencedDate) {
        record.put("georeferencedDate", georeferencedDate);
    }

    @RDF("dwc:georeferenceProtocol")
    public String getGeoreferenceProtocol() {
        return record.get("georeferenceProtocol");
    }

    public void setGeoreferenceProtocol(String georeferenceProtocol) {
        record.put("georeferenceProtocol", georeferenceProtocol);
    }

    @RDF("dwc:georeferenceSources")
    public String getGeoreferenceSources() {
        return record.get("georeferenceSources");
    }

    public void setGeoreferenceSources(String georeferenceSources) {
        record.put("georeferenceSources", georeferenceSources);
    }

    @RDF("dwc:georeferenceVerificationStatus")
    public String getGeoreferenceVerificationStatus() {
        return record.get("georeferenceVerificationStatus");
    }

    public void setGeoreferenceVerificationStatus(String georeferenceVerificationStatus) {
        record.put("georeferenceVerificationStatus", georeferenceVerificationStatus);
    }

    @RDF("dwc:earliestEonOrLowestEonothem")
    public String getEarliestEonOrLowestEonothem() {
        return record.get("earliestEonOrLowestEonothem");
    }

    public void setEarliestEonOrLowestEonothem(String earliestEonOrLowestEonothem) {
        record.put("earliestEonOrLowestEonothem", earliestEonOrLowestEonothem);
    }

    @RDF("dwc:earliestEraOrLowestErathem")
    public String getEarliestEraOrLowestErathem() {
        return record.get("earliestEraOrLowestErathem");
    }

    public void setEarliestEraOrLowestErathem(String earliestEraOrLowestErathem) {
        record.put("earliestEraOrLowestErathem", earliestEraOrLowestErathem);
    }

    @RDF("dwc:earliestPeriodOrLowestSystem")
    public String getEarliestPeriodOrLowestSystem() {
        return record.get("earliestPeriodOrLowestSystem");
    }

    public void setEarliestPeriodOrLowestSystem(String earliestPeriodOrLowestSystem) {
        record.put("earliestPeriodOrLowestSystem", earliestPeriodOrLowestSystem);
    }

    @RDF("dwc:earliestEpochOrLowestSeries")
    public String getEarliestEpochOrLowestSeries() {
        return record.get("earliestEpochOrLowestSeries");
    }

    public void setEarliestEpochOrLowestSeries(String earliestEpochOrLowestSeries) {
        record.put("earliestEpochOrLowestSeries", earliestEpochOrLowestSeries);
    }

    @RDF("dwc:earliestAgeOrLowestStage")
    public String getEarliestAgeOrLowestStage() {
        return record.get("earliestAgeOrLowestStage");
    }

    public void setEarliestAgeOrLowestStage(String earliestAgeOrLowestStage) {
        record.put("earliestAgeOrLowestStage", earliestAgeOrLowestStage);
    }

    @RDF("dwc:formation")
    public String getFormation() {
        return record.get("formation");
    }

    public void setFormation(String formation) {
        record.put("formation", formation);
    }

    @RDF("dwc:identifiedBy")
    public String getIdentifiedBy() {
        return record.get("identifiedBy");
    }

    public void setIdentifiedBy(String identifiedBy) {
        record.put("identifiedBy", identifiedBy);
    }

    @RDF("dwc:dateIdentified")
    public String getDateIdentified() {
        return record.get("dateIdentified");
    }

    public void setDateIdentified(String dateIdentified) {
        record.put("dateIdentified", dateIdentified);
    }

    @RDF("dwc:identificationReferences")
    public String getIdentificationReferences() {
        return record.get("identificationReferences");
    }

    public void setIdentificationReferences(String identificationReferences) {
        record.put("identificationReferences", identificationReferences);
    }

    @RDF("dwc:identificationRemarks")
    public String getIdentificationRemarks() {
        return record.get("identificationRemarks");
    }

    public void setIdentificationRemarks(String identificationRemarks) {
        record.put("identificationRemarks", identificationRemarks);
    }

    @RDF("dwc:identificationQualifier")
    public String getIdentificationQualifier() {
        return record.get("identificationQualifier");
    }

    public void setIdentificationQualifier(String identificationQualifier) {
        record.put("identificationQualifier", identificationQualifier);
    }

    @RDF("dwc:identificationVerificationStatus")
    public String getIdentificationVerificationStatus() {
        return record.get("identificationVerificationStatus");
    }

    public void setIdentificationVerificationStatus(String identificationVerificationStatus) {
        record.put("identificationVerificationStatus", identificationVerificationStatus);
    }

    @RDF("dwc:typeStatus")
    public String getTypeStatus() {
        return record.get("typeStatus");
    }

    public void setTypeStatus(String typeStatus) {
        record.put("typeStatus", typeStatus);
    }

    @RDF("dwc:scientificName")
    public String getScientificName() {
        return record.get("scientificName");
    }

    public void setScientificName(String scientificName) {
        record.put("scientificName", scientificName);
    }

    @RDF("dwc:higherClassification")
    public String getHigherClassification() {
        return record.get("higherClassification");
    }

    public void setHigherClassification(String higherClassification) {
        record.put("higherClassification", higherClassification);
    }

    @RDF("dwc:kingdom")
    public String getKingdom() {
        return record.get("kingdom");
    }

    public void setKingdom(String kingdom) {
        record.put("kingdom", kingdom);
    }

    @RDF("dwc:phylum")
    public String getPhylum() {
        return record.get("phylum");
    }

    public void setPhylum(String phylum) {
        record.put("phylum", phylum);
    }

    @RDF("dwc:class")
    public String getDwcClass() {
        return record.get("class");
    }

    public void setDwcClass(String dwcClass) {
        record.put("class", dwcClass);
    }

    @RDF("dwc:order")
    public String getOrder() {
        return record.get("order");
    }

    public void setOrder(String order) {
        record.put("order", order);
    }

    @RDF("dwc:family")
    public String getFamily() {
        return record.get("family");
    }

    public void setFamily(String family) {
        record.put("family", family);
    }

    @RDF("dwc:genus")
    public String getGenus() {
        return record.get("genus");
    }

    public void setGenus(String genus) {
        record.put("genus", genus);
    }

    @RDF("dwc:specificEpithet")
    public String getSpecificEpithet() {
        return record.get("specificEpithet");
    }

    public void setSpecificEpithet(String specificEpithet) {
        record.put("specificEpithet", specificEpithet);
    }

    @RDF("dwc:infraspecificEpithet")
    public String getInfraspecificEpithet() {
        return record.get("infraspecificEpithet");
    }

    public void setInfraspecificEpithet(String infraspecificEpithet) {
        record.put("infraspecificEpithet", infraspecificEpithet);
    }

    @RDF("dwc:taxonRank")
    public String getTaxonRank() {
        return record.get("taxonRank");
    }

    public void setTaxonRank(String taxonRank) {
        record.put("taxonRank", taxonRank);
    }

    @RDF("dwc:nomenclaturalCode")
    public String getNomenclaturalCode() {
        return record.get("nomenclaturalCode");
    }

    public void setNomenclaturalCode(String nomenclaturalCode) {
        record.put("nomenclaturalCode", nomenclaturalCode);
    }
}
