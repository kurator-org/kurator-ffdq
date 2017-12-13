# kurator-ffdq

A library that provides support for using the FFDQ framework for making data quality assertions.

[![DOI](https://zenodo.org/badge/72672241.svg)](https://zenodo.org/badge/latestdoi/72672241)

# Include using maven

Available from maven central.

    <dependency>
        <groupId>org.datakurator</groupId>
        <artifactId>kurator-ffdq</artifactId>
        <version>1.0</version>
    </dependency>

# Building

    mvn package

# QueryUtil

A command-line utility is provided at `target/kurator-ffdq-1.0.4-jar-with-dependencies.jar` for running sparql queries 
on sample rdf data. Example sparql query files can be found in the `competencyquestions/sparql` directory relative to the
project root and example turtle and jsonld files can be found in `competencyquestions/rdf`.

To view competency questions and example rdf see the readme at: https://github.com/kurator-org/kurator-ffdq/blob/master/competencyquestions/README.md

Run the jar via the following and provide the utility with the options specified below or run with no options to see usage.

`java -jar kurator-ffdq-1.0.4.jar -t ../competencyquestions/rdf/example.jsonld -q ../competencyquestions/sparql/results.sparql -o results.tsv`

# Test spreadsheet utility

This utility provides authors of actors a way to convert the spreadsheet of standardized tests into FFDQ RDF and/or Java classes containing stub methods for implementing tests.

The utility takes a csv file with each row representing a single test and a properties file with metadata about the mechanism as inputs. Examples for the Date Validator can be found at `data/DwCEventDQ.csv` and `data/DwCEventDQ.properties`.

## Configuration

The properties file must contain a guid that uniquely identifies and a human readable name for the mechanism implementing the tests. In order to use class generation, a Java package and class name must be specified for the implementation.

    ffdq.mechanism.guid=b844059f-87cf-4c31-b4d7-9a52003eef84
    ffdq.mechanism.name=Kurator: Date Validator - DwCEventDQ
    ffdq.mechanism.javaPackage=org.filteredpush.qc.date
    ffdq.mechanism.javaClass=DwCEventDQ

## Test CSV Format

The csv file containing the test metadata defines the following metadata :

* **GUID** - The test guid
* **Label** - Human readable name of the test
* **Description** - Describes the test conditions (pass/fail). This value is used to define the Criterion for a Validation or the Enhancement for an Amendment. 
* **Specification** - Technical description of expected behavior when running the test
* **Type** - The assertion type. Must be one of the values `Measure`, `Validation` or `Amendment`
* **Resource Type** - Either `SingleRecord` or `MultiRecord`
* **Dimension** - Defines the data quality dimension of a test for Measures, can be one of `Value`, `Vocab Match`, `Completeness`, `Accuracy`, `Precision` or `Uniqueness`
* **Information Element** - Term or list of terms from a controlled vocabulary that a test acts upon. Must contain the namespace prefix (e.g. "dwc:eventDate, dwc:verbatimEventDate")
* **Source** - Source of the tests
* **Example Implementation** - Link to the source code on GitHub, SourceForge, etc

## Running

To run the utility use the `test-util.sh` shell script with the following required options:

* **config <arg>** - Properties file defining the mechanism to use
* **in <arg>** - Input CSV file containing list of tests
* **out <arg>** - Output file for the rdf representation of the tests

The default format is turtle but this can be changes via the following option:

* **format <arg>** - Output format (RDFXML, TURTLE, JSON-LD)

By default the utility only generates the rdf. In order to generate a new Java class or append new tests to an existing one, you can also specify the following options:
 
* **generateClass** - Generate a new Java class with stub methods for each test
* **appendClass** - Append to an existing Java class stub methods for new tests
* **srcDir <arg>** - The Java sources root directory (e.g. src/main/java)

For example, to run the utility on the example data provided in this project use the following command:

    ./test-util.sh -config data/DwCEventDQ.properties -in data/DwCEventDQ.csv -out data/DwCEventDQ.ttl -srcDir event_date_qc/src/main/java -appendClass

# Test Runner

After generating FFDQ rdf from the spreadsheet of tests and implementing methods tied to test GUIDs in the DQClass, the test runner utility can be used to produce rdf containing FFDQ report concepts for describing the results.

Using the options below, run the utility from the directory containing the jar files(s) that include the annotated DQ Classes (e.g. event_date_qc-1.0.4-SNAPSHOT.jar)

* **cls <arg>** - Fully qualified name of Java class on the classpath to run tests from
* **rdf <arg>** - Input file containing the rdf representation of the tests
* **in <arg>** - Input occurrence tsv data file
* **out <arg>** - Output file for the rdf representation of the dq report
* **format <arg>** - Input/output rdf format (RDFXML, TURTLE, JSON-LD)

For example, run from the command line via:

    cd ~/event_date_qc/target
    ~/kurator-ffdq/test-runner.sh -cls org.filteredpush.qc.date.DwCEventDQ -rdf ../conf/DwCEventDQ.ttl -in ~/Downloads/occurrence.txt -out dq-report.ttl

Using the query utility mentioned above along with the postprocess.sparql query, you can create a tsv query result for previewing the report:

    ~/kurator-ffdq/query-util.sh -q ~/kurator-ffdq/competencyquestions/sparql/postprocess.sparql -t dq-report.ttl -o result.tsv
    libreoffice result.tsv

# Annotated DQ Class

The higher level use of the framework makes use of Java annotations defined in the org.datakurator.ffdq.annotations package.

To use the annotations, in the project that defines the methods and classes corresponding to a set of assertion tests, add the dependency via maven to your pom.xml file

        <dependency>
            <groupId>org.datakurator</groupId>
            <artifactId>kurator-ffdq</artifactId>
            <version>1.0.4-SNAPSHOT</version>
        </dependency>
        
Provided is a class level annotation that defines an FFDQ Mechanism that implements the test (methods). Example usage of the @DQClass annotation applied to a class:

    @DQClass("urn:uuid:b844059f-87cf-4c31-b4d7-9a52003eef84")
    public class DwCEventDQ {
        // ...     
    }

The mechanism above has a guid value property that uniquely identifies the mechanism. This is tied to metadata in RDF about the mechanism.

Next is the method level annotation that maps Java code to FFDQ concepts by associating a method implementing a test with the specification GUID.

	@DQProvides("urn:uuid:da63f836-1fc6-4e96-a612-fa76678cfd6a")
    public static DQResponse<ComplianceValue> eventDateConsistentWithVerbatim(...) {
        // ...
    }
    
Lastly, the method parameter level annotation is provided for defining how the parameters (the fields acted upon or fields consulted) map to information elements in ffdq defined in terms of a controlled vocabulary such as DWC. The value for this annotation must contain the namespace prefix (e.g. dwc:eventDate).

    public static EventDQValidation eventDateConsistentWithVerbatim(
    		@DQParam("dwc:eventDate") String eventDate,
			@DQParam("dwc:verbatimEventDate") String verbatimEventDate) {
			    // ...
			}
    
Depending of the type of assertion, the generic return type DQResponse can be parameterized with the following. Examples usage of each parameterized type below.

For Measures, depending on the dimension, use `DQResponse<NumericalValue>`: 

    DQResponse<NumericalValue> result = new DQResponse<>();
    long seconds = DateUtils.measureDurationSeconds(eventDate);
    result.setValue(new NumericalValue(seconds));
    result.setResultState(ResultState.RUN_HAS_RESULT);

or for the dimension of Completeness use `DQResponse<CompletenessValue>`:

    DQResponse<CompletenessValue> result = new DQResponse<>();
    result.setValue(CompletenessValue.COMPLETE);
    result.addComment("Value provided for eventDate.");
    result.setResultState(ResultState.RUN_HAS_RESULT);
    
For Validations use `DQResponse<ComplianceValue>`:

    DQResponse<ComplianceValue> result = new DQResponse<>();
    result.setValue(ComplianceValue.COMPLIANT);
    result.addComment("Provided value for day '" + day + "' is an integer in the range 1 to 31.");
    result.setResultState(ResultState.RUN_HAS_RESULT);

For Amendments use `DQResponse<AmendmentValue>` and add changed values to an instance of AmendmentValue:

    DQResponse<AmendmentValue> result = new DQResponse<>();
    AmendmentValue extractedValues = new AmendmentValue();
	extractedValues.addResult("dwc:eventDate", DateUtils.createEventDateFromStartEnd(startDate, endDate));
    result.setValue(extractedValues);
    result.setResultState(ResultState.CHANGED);

# Lower level API


# Maintainer deployment: 

To deploy a snapshot to the snapshotRepository:

    mvn clean deploy

To deploy a new release to maven central, set the version in pom.xml to a non-snapshot version, then deploy with the release profile (which adds package signing and deployment to release staging:

    mvn clean deploy -P release

After this, you will need to login to the sonatype oss repository hosting nexus instance, find the staged release in the staging repositories, and perform the release.  It should be possible (haven't verified this yet) to perform the release from the command line instead by running:

    mvn nexus-staging:release -P release

# Acknowledgements

Certain Java classes dedicated to fundamental concepts in the Fitness For Use framework have Java comments that credit a paper by Veiga et al available at: http://journals.plos.org/plosone/article?id=10.1371/journal.pone.0178731

An example is: https://github.com/kurator-org/kurator-ffdq/blob/master/src/main/java/org/datakurator/data/ffdq/model/report/DataResource.java