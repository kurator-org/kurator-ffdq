/** ComplianceValue.java
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
package org.datakurator.ffdq.api.result;

import org.datakurator.ffdq.api.ResultValue;
import org.datakurator.ffdq.model.report.Entity;

public class ComplianceValue implements ResultValue {
    private final String value;

    private ComplianceValue(String value) {
        if (value.equalsIgnoreCase("COMPLIANT") || value.equalsIgnoreCase("NOT_COMPLIANT")) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Invalid value " + value + " for a validation result. Must be either " +
                    "\"COMPLIANT\" or \"NOT_COMPLIANT\".");
        }
    }

    @Override
    public String getObject() {
        return value;
    }

    @Override
    public Entity getEntity() {
        Entity entity = new Entity();
        entity.setValue(value);

        return entity;
    }

    public static ComplianceValue COMPLIANT = new ComplianceValue("COMPLIANT");
    public static ComplianceValue NOT_COMPLIANT = new ComplianceValue("NOT_COMPLIANT");
}
