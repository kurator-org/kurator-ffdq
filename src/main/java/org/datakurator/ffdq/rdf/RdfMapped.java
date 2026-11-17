/**
 * RdfMapped.java
 *
 * Copyright 2026 President and Fellows of Harvard College
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
package org.datakurator.ffdq.rdf;

import org.eclipse.rdf4j.model.Resource;

/**
 * A model object that writes itself as RDF.  Each implementing class states explicitly, with RDF4J,
 * the type, subject IRI, and properties it writes ({@link #writeRdf(RdfWriter)}), and provides a static
 * {@code readRdf(RdfReader, Resource)} method that reads an instance back.
 */
public interface RdfMapped {

    /**
     * Write this object, and the objects it refers to, as RDF.
     *
     * @param writer the writer to add the statements to
     * @return the subject that identifies this object
     */
    Resource writeRdf(RdfWriter writer);
}
