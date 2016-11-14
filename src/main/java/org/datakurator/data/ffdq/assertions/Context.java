/**  Context.java
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

package org.datakurator.data.ffdq.assertions;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes the context of an FFDQ assertion.
 *
 * @author lowery
 */
public class Context {
    private String name;
    private List<String> fieldsActedUpon = new ArrayList<>();
    private List<String> fieldsConsulted = new ArrayList<>();

    public Context() {} // default constructor for Jackson

    public Context(String name, List<String> fieldsActedUpon, List<String> fieldsConsulted) {
        this.name = name;
        this.fieldsActedUpon = fieldsActedUpon;
        this.fieldsConsulted = fieldsConsulted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFieldsActedUpon() {
        return fieldsActedUpon;
    }

    public void setFieldsActedUpon(List<String> fieldsActedUpon) {
        this.fieldsActedUpon = fieldsActedUpon;
    }

    public List<String> getFieldsConsulted() {
        return fieldsConsulted;
    }

    public void setFieldsConsulted(List<String> fieldsConsulted) {
        this.fieldsConsulted = fieldsConsulted;
    }
}
