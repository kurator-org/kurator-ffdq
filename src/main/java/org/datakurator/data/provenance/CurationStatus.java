/**  CurationStatus.java
 *
 * Copyright 2016 President and Fellows of Harvard College
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

package org.datakurator.data.provenance;

/**
 * Constants for marking the outcome of the application of a mechanism that
 * implements some specification.
 *
 */
public enum CurationStatus {

    /**
     * For Validations, indicates whether or not issues were found in the data per the specification.
     */
    COMPLIANT,
    NOT_COMPLIANT,


    /**
     * For Measures, indicates whether or not a measurement is considered complete.
     */
    COMPLETE,
    NOT_COMPLETE,

    /**
     * For Enhancements, indicates that a change to the data has been proposed.
     */
    CURATED,

    /**
     * For Enhancements, indicates that one or more terms which were blank in the input have been
     * filled in with some non-blank value in the output.
     */
    FILLED_IN,

    
    /**
     * For Validations or Measures, indicates that the data are internally inconsistent in
     * some way that makes the test result ambiguous, equivalent to the concept of 
     * SOLVE_WITH_MORE_DATA, in that some data in addition to that considered by the Context
     * is needed to resolve the ambiguity.  For a validation that explicitly tests for ambiguity,
     * use COMPLIANT or NOT_COMPLIANT as responses, AMBIGUOUS should be reserved for tests that
     * are asking other questions.  For example, given a value for a year of '82', and an 
     * assumption that the data probably represent dates in the range 1600-2000, a validation test
     * that checks if a year is specified should report NOT_COMPLIANT, while a validation test
     * that compares that year to a person's life span should report AMBIGUOUS, if one of the possible
     * years of 1682, or 1782, or 1882, or 1982 fall within the person's life span. 
     */
    AMBIGUOUS,

    /**
     * Some prerequisite inherent in the data for performing the tests or enhancements in the 
     * specification was not met, such as some required field was missing, or a value under 
     * test being out of range.  Thus it was not possible to validate, measure, or enhance
     * the provided data to the specification.
     */
    DATA_PREREQUISITES_NOT_MET,

    /**
     * Some prerequisite external to the provided data for performing the tests or enhancements
     * in the specification was not met.  For example, a webservice was down and unable to be 
     * consulted per the specification.
     */
    EXTERNAL_PREREQUISITES_NOT_MET,
}
