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
     * Indicates whether or not issues were found in the data per the specification.
     */
    COMPLIANT,
    NOT_COMPLIANT,


    /**
     * Indicates whether or not a measurement is considered complete.
     */
    COMPLETE,
    NOT_COMPLETE,

    /**
     * Indicates that a change to the data has been proposed.
     */
    CURATED,

    /**
     * Indicates that one or more terms which were blank in the input have been
     * filled in with some non-blank value in the output.
     */
    FILLED_IN,


    /**
     * Indicates that it was possible to perform the tests of the specification on the
     * data, but that it was not possible to validate the provided data to the specification.
     */
    DATA_PREREQUISITES_NOT_MET,
    DATA_PREREQUISITES_AMBIGUOUS,

    /**
     * Some prerequisite for performing the tests in the specification was not met.  This could
     * be internal to the data (some required field was missing), or external (a webservice
     * was down and unable to be consulted).
     */
    EXTERNAL_PREREQUISITES_NOT_MET,
}
