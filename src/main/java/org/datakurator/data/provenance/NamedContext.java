/**  NamedContext.java
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

import java.util.*;

/**
 * Context defined by name contains information about which fields were affected.
 *
 * @author lowery
 */
public class NamedContext {
    private String name;
    private FieldContext fields;

    public NamedContext(String name, FieldContext fields) {
        this.name = name;
        this.fields = fields;
    }

    public NamedContext(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getFieldsConsulted() {
        if (fields != null) {
            return fields.getConsulted();
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public List<String> getFieldsActedUpon() {
        if (fields != null) {
            return fields.getActedUpon();
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public Map<String, String> getProperties() {
        if (fields != null) {
            return fields.getProperties();
        } else {
            return Collections.EMPTY_MAP;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamedContext that = (NamedContext) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
