/**  FieldContext.java
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
 * Context described in terms of the fields that were acted upon or consulted.
 *
 * @author lowery
 */
public class FieldContext {
    private List<String> fieldsActedUpon = new ArrayList<>();
    private List<String> fieldsConsulted = new ArrayList<>();

    public FieldContext(String... fieldsActedUpon) {
        setActedUpon(fieldsActedUpon);
    }

    public void setActedUpon(String... fieldsActedUpon) {
        this.fieldsActedUpon = Arrays.asList(fieldsActedUpon);
    }

    public void setConsulted(String... fieldsConsulted) {
        this.fieldsConsulted = Arrays.asList(fieldsConsulted);
    }

    public List<String> getConsulted() {
        return fieldsConsulted;
    }

    public List<String> getActedUpon() {
        return fieldsActedUpon;
    }

    /**
     * Get the context as a map of key value pair properties.
     *
     * @return properties map
     */
    public Map<String, String> getProperties() {
        Map<String, String> props = new HashMap<>();

        props.put("context.fieldsActedUpon", getActedUpon().toString());
        props.put("context.fieldsConsulted", getConsulted().toString());

        return props;
    }
}
