/** AmendmentValue.java
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
package org.datakurator.ffdq.model.report.result;

import org.datakurator.dwcloud.DwcOccurrence;
import org.datakurator.ffdq.model.DataResource;
import org.datakurator.ffdq.model.report.ResultValue;
import org.datakurator.ffdq.rdf.Namespace;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

public class AmendmentValue extends ResultValue {
    private String id = "urn:uuid" + UUID.randomUUID();

    private int score = 1; // TODO: For ranking of alternatives

    private DwcOccurrence record;

    public AmendmentValue() {
        record = new DwcOccurrence();
        setValue(record.getURI());
    }

    public AmendmentValue(Map<String, String> value) {
        this.record = new DwcOccurrence(value);
        setValue(record.getURI());
    }

    public DataResource getDataResource() {
        return record;
    }

    public String get(String key) {
        return record.asMap().get(key);
    }

    public int size() {
        return record.asMap().size();
    }

    /**
     * Add a Darwin Core term and its value to the result.
     *
     * @param key the darwin core term for which a value is being provided
     * @param value the value provided for the key.
     */
    public void addResult(String key, String value) {
        // Check that the term is valid and contains the prefix
        URI uri = Namespace.resolvePrefixedTerm(key);

        this.record.put(uri, value);
    }
}
