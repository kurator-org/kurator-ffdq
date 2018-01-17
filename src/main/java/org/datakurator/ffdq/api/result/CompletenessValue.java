/** CompletenessValue.java
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

public class CompletenessValue implements ResultValue {
    private final String value;

    private CompletenessValue(String value) {
        if (value.equalsIgnoreCase("COMPLETE") || value.equalsIgnoreCase("NOT_COMPLETE")) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Invalid value " + value + " for a measurement result. Must be either " +
                    "\"COMPLETE\" or \"NOT_COMPLETE\".");
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

    public static CompletenessValue COMPLETE = new CompletenessValue("COMPLETE");
    public static CompletenessValue NOT_COMPLETE = new CompletenessValue("NOT_COMPLETE");
}
