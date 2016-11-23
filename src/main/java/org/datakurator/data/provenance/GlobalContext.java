/**  GlobalContext.java
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

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates information about the global context of changes made to a record. This level of context is intended to
 * be used to describe metadata about changes made at the actor or dataset level.
 *
 * @author lowery
 */
public class GlobalContext {
    private final String actorClass;
    private final String actorName;

    public GlobalContext(String actorClass, String actorName) {
        this.actorClass = actorClass;
        this.actorName = actorName;
    }

    public String getActorClass() {
        return actorClass;
    }

    public String getActorName() {
        return actorName;
    }

    /**
     * Get the context as a map of key value pair properties.
     *
     * @return properties map
     */
    public Map<String, String> getProperties() {
        Map<String, String> props = new HashMap<>();

        props.put("actor.name", getActorName());
        props.put("actor.class", getActorClass());

        return props;
    }
}