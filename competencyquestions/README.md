# Competency questions

This project contains a set of competency questions framed as sparql queries. These queries can be run on the provided 
example rdf output.

## Sparql queries

- [guids.sparql](https://github.com/kurator-org/kurator-ffdq//blob/master/competencyquestions/sparql/guids.sparql) - Given a UseCase find guids for each assertion test's Mechanism and Specification and list valuable
 InformationElements acted upon by that test.
- [mechanism.sparql](https://github.com/kurator-org/kurator-ffdq//blob/master/competencyquestions/sparql/mechanism.sparql) - Given a use case find the mechanisms used
- [results.sparql](https://github.com/kurator-org/kurator-ffdq//blob/master/competencyquestions/sparql/results.sparql) - Given a use case find the validation result status and valuable information elements
- [tests.sparql](https://github.com/kurator-org/kurator-ffdq//blob/master/competencyquestions/sparql/tests.sparql) - List each test result by use case, test id, specification, record id, result status and comment

## Example RDF

- [example.ttl](https://github.com/kurator-org/kurator-ffdq//blob/master/competencyquestions/rdf/example.ttl) - Simple example of a validation result for use case "check internal consistency of dates"
- [example.jsonld](https://github.com/kurator-org/kurator-ffdq//blob/master/competencyquestions/rdf/example.jsonld) - JSONLD serialization of the example above
