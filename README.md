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

A command-line utility is provided at `target/kurator-ffdq-1.0.3-jar-with-dependencies.jar` for running sparql queries 
on sample rdf data. Example sparql query files can be found in the `competencyquestions/sparql` directory relative to the
project root and example turtle and jsonld files can be found in `competencyquestions/rdf`.

To view competency questions and example rdf see the readme at: https://github.com/kurator-org/kurator-ffdq/blob/master/competencyquestions/README.md

Run the jar via the following and provide the utility with the options specified below or run with no options to see usage.

`java -jar kurator-ffdq-1.0.3.jar -t ../competencyquestions/rdf/example.jsonld -q ../competencyquestions/sparql/results.sparql -o results.tsv`

# Using 

Classes are provided to represent Measures, Validations, and Improvements, to relate them to criteria in Context,
to group these assertions into data quality reports, which are composed of pre-enhancement, enhancement, and 
post enhancement stages.

Here is an example of the use of these classes taken from FP-KurationServices DateValidator:

    public static BaseRecord validateEventConsistencyWithContext(String recordId, String eventDate, String year, String month,
                                                                 String day, String startDayOfYear, String endDayOfYear,
                                                                 String eventTime, String verbatimEventDate) {

       FFDQRecord record = new FFDQRecord();

       // store the values initialy encountered in the data.
       Map<String, String> initialValues = new HashMap<>();
       initialValues.put("eventDate", eventDate);
       initialValues.put("year", year);
       initialValues.put("month", month);
       initialValues.put("day", day);
       initialValues.put("startDayOfYear", startDayOfYear);
       initialValues.put("endDayOfYear", endDayOfYear);
       initialValues.put("eventTime", eventTime);
       initialValues.put("verbatimEventDate", verbatimEventDate);
       record.setInitialValues(initialValues);   

       GlobalContext globalContext = new GlobalContext(DateValidator.class.getSimpleName(), DateValidator.getActorName());
       record.setGlobalContext(globalContext);

       // Start pre enhancement stage
       record.startStage(CurationStage.PRE_ENHANCEMENT);

       // call some methods that perform measures and validations
       checkContainsEventTime(record);   // see below 
       checkEventDateCompleteness(record);
       checkDurationInSeconds(record);

       validateConsistencyWithAtomicParts(record);
       validateVerbatimEventDate(record);
       validateConsistencyWithEventTime(record);

        // Start enhancement stage
        record.startStage(CurationStage.ENHANCEMENT);

        if (DateUtils.isEmpty(record.getEventDate())) {
            fillInFromAtomicParts(record);
        }

        // Start post enhancement stage
        record.startStage(CurationStage.POST_ENHANCEMENT);

        checkContainsEventTime(record);
        checkEventDateCompleteness(record);
        checkDurationInSeconds(record);

        validateConsistencyWithAtomicParts(record);
        validateVerbatimEventDate(record);
        validateConsistencyWithEventTime(record);

        return record;
    }

    /** 
     * This is a measure, it measures to see if an event date contains a time.
     *
    private static void checkContainsEventTime(DateFragment record) {
        FieldContext fields = new FieldContext();
        fields.setActedUpon("eventDate");
        fields.setConsulted("eventTime");

        NamedContext eventDateContainsEventTime = new NamedContext("eventDateContainsEventTime", fields);
        Measure m = record.assertMeasure(eventDateContainsEventTime);

        if (!DateUtils.isEmpty(record.getEventDate())) {
            if (DateUtils.containsTime(record.getEventDate())) {
                // Measure.complete() makes the assertion COMPLETE with a comment.
                m.complete("dwc:eventDate contains eventTime");
            } else {
                // Measure.incomplete() makes the assertion NOT_COMPLETE with a comment.
                m.incomplete("dwc:eventDate does not contain eventTime");
            }
        } else {
            // Measure.prereqUnmet() makes the assertion DATA_PREREQUISITES_NOT_MET with a comment.
            m.prereqUnmet("dwc:eventDate does not contain a value.");
        }
    }

A data quality report can then be serialized with the DQReportBuilder

     InputStream config = DateValidatorTest.class.getResourceAsStream("/ffdq-assertions.json");
     DQReportBuilder builder = new DQReportBuilder(config);
     DQReport report = builder.createReport(testResult);
     StringWriter writer = new StringWriter();
     report.write(writer);

A configuration definition (from FP-KurationServices src/main/resources/ffdq-assertions.json) for
the measure checkContainsEventTime() above, is:

    {
      "context": {
        "name": "eventDateContainsEventTime"
      },

      "dimension": "Completeness",
      "specification": "Check that ${context.fieldsActedUpon} matches an ISO date that contains a time",
      "mechanism": "Kurator: ${actor.class} - ${actor.name}"
    },


Conversion of the data quality report to a human readable form would then require subsequent processing.


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