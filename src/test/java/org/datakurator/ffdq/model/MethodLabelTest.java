/**
 * MethodLabelTest.java
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
package org.datakurator.ffdq.model;

import static org.junit.Assert.assertEquals;

import org.datakurator.ffdq.model.context.Amendment;
import org.datakurator.ffdq.model.context.Issue;
import org.datakurator.ffdq.model.context.Measure;
import org.datakurator.ffdq.model.context.Validation;
import org.datakurator.ffdq.model.solutions.AmendmentMethod;
import org.datakurator.ffdq.model.solutions.IssueMethod;
import org.datakurator.ffdq.model.solutions.MeasurementMethod;
import org.datakurator.ffdq.model.solutions.ValidationMethod;
import org.junit.Test;

/**
 * The label of a Method is "{Method class}: {Test label} with Specification: {Specification label}", the
 * same for all four kinds of Method (e.g. "... with Specification: Specification for: VALIDATION_X").
 */
public class MethodLabelTest {

    private static Specification specification(String test) {
        Specification s = new Specification();
        s.setLabel("Specification for: " + test);
        return s;
    }

    @Test
    public void testLabelsOfAllMethodTypes() {
        Validation v = new Validation();
        v.setLabel("VALIDATION_X");
        assertEquals("ValidationMethod: VALIDATION_X with Specification: Specification for: VALIDATION_X",
                new ValidationMethod(specification("VALIDATION_X"), v).getLabel());
        Measure m = new Measure();
        m.setLabel("MEASURE_X");
        assertEquals("MeasurementMethod: MEASURE_X with Specification: Specification for: MEASURE_X",
                new MeasurementMethod(specification("MEASURE_X"), m).getLabel());
        Amendment a = new Amendment();
        a.setLabel("AMENDMENT_X");
        assertEquals("AmendmentMethod: AMENDMENT_X with Specification: Specification for: AMENDMENT_X",
                new AmendmentMethod(specification("AMENDMENT_X"), a).getLabel());
        Issue i = new Issue();
        i.setLabel("ISSUE_X");
        assertEquals("IssueMethod: ISSUE_X with Specification: Specification for: ISSUE_X",
                new IssueMethod(specification("ISSUE_X"), i).getLabel());
    }
}
