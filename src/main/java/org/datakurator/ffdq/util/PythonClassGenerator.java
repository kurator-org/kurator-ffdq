/** PythonClassGenerator.java
 *
 * Copyright 2019 President and Fellows of Harvard College
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
 * 
 * 
 */
package org.datakurator.ffdq.util;

import org.datakurator.ffdq.runner.AssertionTest;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Generate experimental stub methods in python using analogs for java annotations that
 * could potentially be used to with python implementations in a manner similar to the
 * java kurator-ffdq and ffdq-api packages to associate metadata with tests and test
 * results.
 *
 * @author David Lowery
 * @author Paul J. Morris
 * @version $Id: $Id
 */
public class PythonClassGenerator {

/*

Example of code to be generated

class DwCTG2DQ:
    Mechanism = { "value":"3615123f-643c-4ff6-8b55-7ec3beabd274", "label":"Python: DwCTG2DQ" }
    
    def validationCountrycodeNotstandard(countryCode: {"dwc:countryCode":"ActedUpon"} ) -> DQResponse:
        '''
          #20 ValidationAssertion SingleRecord Conformance: countrycode notstandard
     
          Provides: VALIDATION_COUNTRYCODE_NOTSTANDARD
    
          param countryCode the provided dwc:countryCode to evaluate
          return DQResponse the response of type ComplianceValue  to return
        '''
        Provides = { "id":"0493bcfb-652e-4d17-815b-b0cce0742fbe", "label":"VALIDATION_COUNTRYCODE_NOTSTANDARD" }

        result = ComplianceValue();

        # TODO:  Implement specification
        # EXTERNAL_PREREQUISITES_NOT_MET if the specified source authority 
        # service was not available; COMPLIANT if dwc:countryCode 
        # is a valid ISO (ISO 3166-1-alpha-2 country codes) value 
        # or is EMPTY; otherwise NOT_COMPLIANT 
        # TODO: Parameters. This test is defined as parameterized.
        # bdq:sourceAuthority (default = https://restcountries.eu/#api-endpoints-list-of-codes)

        return result;

*/


    private final static Logger logger = Logger.getLogger(PythonClassGenerator.class.getName());

    private String mechanismGuid;
    private String mechanismName;

    private String className;
    private String packageName;

    private Map<String, Integer> currGuids = new HashMap<>();
    private int testsAdded = 0;

    private StringBuilder sb;

    /**
     * <p>Constructor for PythonClassGenerator.</p>
     *
     * @param mechanismGuid a {@link java.lang.String} object.
     * @param mechanismName a {@link java.lang.String} object.
     * @param packageName a {@link java.lang.String} object.
     * @param className a {@link java.lang.String} object.
     */
    public PythonClassGenerator(String mechanismGuid, String mechanismName, String packageName, String className) {
        this.mechanismGuid = mechanismGuid;
        this.mechanismName = mechanismName;

        this.packageName = packageName;
        this.className = className;
    }

    /**
     * Initialize for generation of a new DQ Class
     */
    public void init() {
        sb = new StringBuilder();

        sb.append("# NOTE: requires a library supporting the ffdq-api classes. \n\n");

        sb.append("class ").append(className).append(":\n");
        sb.append("\t").append("Mechanism = { \"value\":\"").append(mechanismGuid).append("\",\"label\":\"").append(mechanismName).append("\" }").append("\n");
        sb.append("\n");
    }

    /**
     * Initialize for appending tests to an existing DQ Class
     *
     * @param javaSrc java source file to append to
     */
    public void init(InputStream javaSrc) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(javaSrc));

        String line = "";
        int lineNum = 0, classEnd = 0;

        List<String> lines = new LinkedList<>();

        /* 
        TODO: Implement for Python 
        try {
            while ((line = reader.readLine()) != null) {
                if (line.contains("@Provides(")) {
                    String guid = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
                    currGuids.put(guid, lineNum + 1);
                }

                if (line.contains("}")) {
                    classEnd = lineNum;
                }

                lines.add(line);
                lineNum++;
            }

            lines.remove(classEnd);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load Java source file", e);
        }

        sb = new StringBuilder();
        for (String str : lines) {
            sb.append(str).append("\n");
        }
        */
    }

    /**
     * <p>addTest.</p>
     *
     * @param test a {@link org.datakurator.ffdq.runner.AssertionTest} object.
     */
    public void addTest(AssertionTest test) {
        if (!currGuids.isEmpty() && currGuids.containsKey(test.getGuid())) {
            logger.info("Found existing implementation for \"" + test.getLabel() + "\" with guid \"" + test.getGuid() + "\" on line: " +
                    currGuids.get(test.getGuid()));
        } else {
            testsAdded++;

            // method name
            String[] label = test.getLabel().split("[^a-zA-Z\\d:]");
            String methodName = "";

            for (int i = 0; i < label.length; i++) {
                if (i == 0) {
                    methodName += label[i].toLowerCase();
                } else {
                    methodName += label[i].substring(0, 1).toUpperCase() + label[i].substring(1).toLowerCase();
                }
            }


            // return type
            String retType = "void";
            String retTypeJavaDoc = null;   // Javadoc @return throws error when given generics.
            switch (test.getAssertionType().toUpperCase()) {
                case "MEASURE":
                    if (test.getDimension().equalsIgnoreCase("Completeness")) {
                        // retType = "DQResponse<CompletenessValue>";
                        retType = "CompletenessValue";
                        retTypeJavaDoc = "DQResponse the response of type CompletenessValue ";
                    } else {
                        // retType = "DQResponse<NumericalValue>";
                        retType = "NumericalValue";
                        retTypeJavaDoc = "DQResponse the response of type NumericalValue ";
                    }
                    break;
                case "VALIDATION":
                    // retType = "DQResponse<ComplianceValue>";
                    retType = "ComplianceValue";
                    retTypeJavaDoc = "DQResponse the response of type ComplianceValue ";
                    break;
                case "AMENDMENT":
                    //retType = "DQResponse<AmendmentValue>";
                    retType = "AmendmentValue";
                    retTypeJavaDoc = "DQResponse the response of type AmendmentValue";
                    break;
            }

            // params  (these are the information elements as test inputs).
            Map<String, String> params = new HashMap<>();
            List<String> ie = test.getInformationElement();
            for (int i = 0; i < ie.size(); i++) {
                String term = ie.get(i);
                String param = term.split(":")[1];

                params.put(term, param);
            }

            // method declaration
            // def validationCountrycodeNotstandard(countryCode: {"dwc:countryCode":"ActedUpon"} ) -> DQResponse:
            sb.append("\t").append("def ").append(methodName).append("(");
            int cnt = 0;
            for (String term : params.keySet()) {
            	String pterm = params.get(term);
            	if (pterm.equals("class")) { pterm = "p_class"; }  // class is reserved word in python 
            	if (pterm.equals("*")) { pterm = "dict_allDarwinCoreTerms"; }  // * is not an allowed word python 
                sb.append(pterm).append(": {\"").append(term).append("\":\"ActedUpon\"").append("} ");

                if (cnt < ie.size() - 1) {
                    sb.append(", ");
                }

                cnt++;
            }
            sb.append(")->").append(retType).append(":").append("\n");

            // pydoc comment right after method declaration
            sb.append("\t").append("\t").append("'''\n");
            sb.append("\t").append("\t").append("").append(test.getDescription()).append("\n");
            sb.append("\t").append("\t").append("\n");
            sb.append("\t").append("\t").append("Provides: ").append(test.getLabel()).append(" \n");
            sb.append("\t").append("\t").append("Version: ").append(test.getVersion()).append(" \n");
            sb.append("\t").append("\t").append("\n");
            for (String key : params.keySet()) {
            	String param = params.get(key);
                sb.append("\t").append("\t").append("param ").append(param).append(" the provided ").append(key).append(" to evaluate\n");
            }
            
            if (retTypeJavaDoc!=null) { 
                sb.append("\t").append("\t").append("@return ").append(retTypeJavaDoc).append(" to return\n");
            }
            sb.append("\n");

            sb.append("\t").append("\t").append("'''\n");
            sb.append("\n");

            // Provides = "0493bcfb-652e-4d17-815b-b0cce0742fbe"
            // ProvidesVersion = "0493bcfb-652e-4d17-815b-b0cce0742fbe/yyyy-mm-dd"
            sb.append("\t").append("\t").append("Provides = { \"id\":\"").append(test.getGuid()).append("\", \"version\":\"").append(test.getGuidTDWGNamespace()).append("/").append(test.getVersion()).append("\"").append(", \"label\":\"").append(test.getGuid()).append("\" }\n");
            
            // result = ComplianceValue();
            sb.append("\t").append("\t").append("result = ").append(retType).append("()").append("\n\n");

            List<String> specificationWords = java.util.Arrays.asList(test.getSpecification().split("\\s+"));
            List<String> defaultsWords = java.util.Arrays.asList(test.getAuthoritiesDefaults().split("\\s+"));
            sb.append("\t").append("\t").append("#TODO:  Implement specification").append("\n");
            // Split the specification into words on whitespace, then print specification in lines with
            // the last word boundary being before character 55 in the string (plus an indent and comment chars).
            Iterator<String> i = specificationWords.iterator();
            StringBuffer specificationLine = new StringBuffer();
            while (i.hasNext()) { 
            	specificationLine.append(i.next()).append(" ");
            	if (specificationLine.length()>55) { 
            	     sb.append("\t").append("\t").append("# ").append(specificationLine.toString()).append("\n");
            	     specificationLine = new StringBuffer();
            	}
            }
            sb.append("\t").append("\t").append("#").append(specificationLine.toString()).append("\n");
            Iterator<String> iw = defaultsWords.iterator();
            StringBuffer defaultsLine = new StringBuffer();
            while (i.hasNext()) { 
            	defaultsLine.append(i.next()).append(" ");
            	if (defaultsLine.length()>55) { 
            	     sb.append("\t").append("\t").append("# ").append(defaultsLine.toString()).append("\n");
            	     defaultsLine = new StringBuffer();
            	}
            }
            sb.append("\t").append("\t").append("#").append(defaultsLine.toString()).append("\n");
            sb.append("\n");
            // Test Parameters change the behavior of the test.
            if (test.getTestParameters()!=null && test.getTestParameters().size()>0) { 
            	StringBuilder testParamCommentLines = new StringBuilder();
            	Iterator<String> ipar = test.getTestParameters().iterator();
            	while (ipar.hasNext()) { 
            		String testParam = ipar.next();
            		if (testParam!=null && testParam.trim().length()>0) { 
            			testParamCommentLines.append("\t").append("\t").append("# ").append(testParam).append("\n");
            		}
            	}
            	if (testParamCommentLines.length()>0) { 
            		sb.append("\t").append("\t").append("#TODO: Parameters. This test is defined as parameterized.").append("\n");
            		sb.append(testParamCommentLines);
            		sb.append("\n");
            	}
            }
            sb.append("\t").append("\t").append("return result;\n");
            sb.append("\n\n");
        }
    }

    /**
     * <p>writeOut.</p>
     *
     * @param out a {@link java.io.OutputStream} object.
     * @throws java.io.IOException if any.
     */
    public void writeOut(OutputStream out) throws IOException {
        sb.append("\n");
        out.write(sb.toString().getBytes());
        System.out.println();
        if (testsAdded > 0) {
            logger.info("Added new stub methods for " + testsAdded + " unimplemented tests.");
        } else {
            logger.info("DQ Class is up to date. No new tests added.");
        }

    }
}
