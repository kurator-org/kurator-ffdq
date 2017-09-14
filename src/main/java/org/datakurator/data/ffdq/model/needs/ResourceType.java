/**  ResourceType.java
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
package org.datakurator.data.ffdq.model.needs;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.UUID;

@RDFNamespaces({
        "rt = http://example.com/ffdq/rt/"
})
@RDFBean("ffdq:ResourceType")
public class ResourceType {
    public static final ResourceType SINGLE_RECORD = new ResourceType("SingleRecord");
    public static final ResourceType MULTI_RECORD = new ResourceType("MultiRecord");

    private String id = "urn:uuid:" + UUID.randomUUID();
    private String label;

    @RDFSubject(prefix = "rt:")
    public String getId() {
        return label;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResourceType(String label) {
        this.label = label;
    }

}
