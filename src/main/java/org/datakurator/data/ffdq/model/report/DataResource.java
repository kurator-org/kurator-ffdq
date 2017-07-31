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
package org.datakurator.data.ffdq.model.report;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

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
        "dwc = http://rs.tdwg.org/dwc/terms/"
})
@RDFBean("ffdq:DataResource")
public class DataResource {

}
