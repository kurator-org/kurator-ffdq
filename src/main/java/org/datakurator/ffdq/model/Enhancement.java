/**  Enhancement.java
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
package org.datakurator.ffdq.model;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = http://rs.tdwg.org/bdqffdq/terms/",
        "bdqenh = http://rs.tdwg.org/bdqenh/terms/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:Enhancement")
public class Enhancement {
	
    private String id = "urn:uuid:" + UUID.randomUUID();
    private String label;

    public Enhancement() { }

    public Enhancement(String label) {
        this.label = label;
    }
    
    public Enhancement(String label, String id) { 
    	this.label= label;
    	this.id = id;
    }

    @RDFSubject(prefix = "bdqenh:")
    public String getId() {
    	if (id.startsWith("https://rs.tdwg.org/bdqenh/terms/")) { 
    		return id.replace("https://rs.tdwg.org/bdqenh/terms/", "");
    	} else { 
    		return id;
    	}
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("rdfs:label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public static Enhancement ASSUMEDDEFAULT = new Enhancement("AssumedDefault","https://rs.tdwg.org/bdqenh/terms/AssumedDefault");
    public static Enhancement CONVERTED = new Enhancement("Converted","https://rs.tdwg.org/bdqenh/terms/Converted");
    public static Enhancement FILLINFROM = new Enhancement("FillInFrom","https://rs.tdwg.org/bdqenh/terms/FillInFrom");
    public static Enhancement STANDARDIZED = new Enhancement("Standardized","https://rs.tdwg.org/bdqenh/terms/Standardized");
    public static Enhancement TRANSPOSED = new Enhancement("Transposed","https://rs.tdwg.org/bdqenh/terms/Transposed");

    public static Enhancement fromString(String value) {
        if (value.equalsIgnoreCase(ASSUMEDDEFAULT.getLabel())) return ASSUMEDDEFAULT;
        else if (value.equalsIgnoreCase(CONVERTED.getLabel())) return CONVERTED;
        else if (value.equalsIgnoreCase(FILLINFROM.getLabel())) return FILLINFROM;
        else if (value.equalsIgnoreCase(TRANSPOSED.getLabel())) return TRANSPOSED;
        else if (value.equalsIgnoreCase(STANDARDIZED.getLabel())) return STANDARDIZED;
        else throw new UnsupportedOperationException("Unable to find an bdqenh: term for bdqffdq:Criterion for value: [" + value + "]");
    }
    

}
