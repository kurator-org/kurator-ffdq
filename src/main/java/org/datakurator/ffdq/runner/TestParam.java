/** TestParam.java
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
package org.datakurator.ffdq.runner;

import org.datakurator.ffdq.rdf.Namespace;

import java.lang.reflect.Parameter;
import java.net.URI;

/**
 * Created by lowery on 8/21/17.
 *
 * @author mole
 * @version $Id: $Id
 */
public class TestParam {
    private final String value;
    private URI namespace;
    private String term;

    private Parameter parameter;
    private int index;

    /**
     * <p>Constructor for TestParam.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @param index a int.
     * @param parameter a {@link java.lang.reflect.Parameter} object.
     */
    public TestParam(String value, int index, Parameter parameter) {
        this.parameter = parameter;
        this.index = index;
        this.value = value;

        if (value.contains(":")) {
            // Lookup namespace and resolve URI for the term
            this.namespace = Namespace.resolvePrefixedTerm(value);

            // Split string into namespace prefix and term name
            String[] str = value.split(":");
            this.term = str[1];
        } else {
            this.term = value;
        }
    }

    /**
     * <p>getURI.</p>
     *
     * @return a {@link java.net.URI} object.
     */
    public URI getURI() {
        return namespace.resolve(term);
    }

    /**
     * <p>Getter for the field <code>term</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTerm() {
        return term;
    }

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return parameter.getName();
    }

    /**
     * <p>Getter for the field <code>index</code>.</p>
     *
     * @return a int.
     */
    public int getIndex() {
        return index;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return term;
    }
}
