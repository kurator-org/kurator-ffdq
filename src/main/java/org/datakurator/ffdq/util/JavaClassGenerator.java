/** ClassGenerator.java
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
 * 
 * @author David Lowery
 * @author Paul J. Morris
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

public class JavaClassGenerator {
    private final static Logger logger = Logger.getLogger(JavaClassGenerator.class.getName());

    private String mechanismGuid;
    private String mechanismName;

    private String className;
    private String packageName;

    private Map<String, Integer> currGuids = new HashMap<>();
    private Map<String, Integer> currVersions = new HashMap<>();
    private int testsAdded = 0;

    private StringBuilder outputCodeSB;

    public JavaClassGenerator(String mechanismGuid, String mechanismName, String packageName, String className) {
        this.mechanismGuid = mechanismGuid;
        this.mechanismName = mechanismName;

        this.packageName = packageName;
        this.className = className;
    }

    /**
     * Initialize for generation of a new DQ Class
     */
    public void init() {
        outputCodeSB = new StringBuilder();

        outputCodeSB.append("/* NOTE: requires the ffdq-api dependecy in the maven pom.xml */\n\n");
        outputCodeSB.append("package ").append(packageName).append(";\n\n");

        outputCodeSB.append("import org.datakurator.ffdq.annotations.*;\n");
        outputCodeSB.append("import org.datakurator.ffdq.api.DQResponse;\n");
        outputCodeSB.append("import org.datakurator.ffdq.model.ResultState;\n");
        outputCodeSB.append("import org.datakurator.ffdq.api.result.*;\n\n");

        outputCodeSB.append("@Mechanism(value=\"").append(mechanismGuid).append("\",label=\"").append(mechanismName).append("\")\n");
        outputCodeSB.append("public class ").append(className).append(" {\n\n");
    }

    /**
     * Initialize for appending tests to an existing DQ Class or checking versions  
     *
     * @param javaSrc java source file to append to or check
     */
    public void init(InputStream javaSrc, boolean logVersions) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(javaSrc));

        String line = "";
        int lineNum = 0, classEnd = 0;

        List<String> lines = new LinkedList<>();

        try {
            while ((line = reader.readLine()) != null) {
                if (line.contains("@Provides(")) {
                    String guid = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
                    currGuids.put(guid, lineNum + 1);
                }
                if (line.contains("@ProvidesVersion(")) {
                    String guid = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
                    currVersions.put(guid, lineNum + 1);
                    if (logVersions) { 
                    	logger.info(guid);
                    }
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

        outputCodeSB = new StringBuilder();
        for (String str : lines) {
            outputCodeSB.append(str).append("\n");
        }
    }

    public void checkTest(AssertionTest test) {
        if (!currGuids.isEmpty() && currGuids.containsKey(test.getGuid())) {
            logger.info("Found existing implementation for \"" + test.getLabel() + "\" with guid \"" + test.getGuid() + "\" on line: " + currGuids.get(test.getGuid()));
            if (!currVersions.isEmpty()) { 
                if (currVersions.containsKey(test.getProvidesVersion())) {
                	logger.info("Current implementation for \"" + test.getLabel() + "\" with version \"" + test.getProvidesVersion() + "\" on line: " + currVersions.get(test.getProvidesVersion()));
                } else { 
                	logger.info("Non-current implementation for \"" + test.getLabel() + "\" version \"" + test.getProvidesVersion() + "\" ");
                	System.out.println("Non-current implementation for \"" + test.getLabel() + "\" current version is \"" + test.getProvidesVersion() + "\" see line: " + currGuids.get(test.getGuid()));
                }
            }
        }
    } 
    
    public void addTest(AssertionTest test) {
        if (!currGuids.isEmpty() && currGuids.containsKey(test.getGuid())) {
            logger.info("Found existing implementation for \"" + test.getLabel() + "\" with guid \"" + test.getGuid() + "\" on line: " +
                    currGuids.get(test.getGuid()));
            if (!currVersions.isEmpty()) { 
            	if (currVersions.containsKey(test.getProvidesVersion())) {
            		logger.info("Current implementation for \"" + test.getLabel() + "\" with version \"" + test.getProvidesVersion() + "\" on line: " + currVersions.get(test.getProvidesVersion()));
            	} else {
            		logger.info("Current implementation for \"" + test.getLabel() + "\" with version \"" + test.getProvidesVersion() + "\" on line: " + currVersions.get(test.getProvidesVersion()));
            		outputCodeSB.append("// TODO: Implementation of ").append(test.getLabel());
            		outputCodeSB.append(" is not up to date with current version: ").append(test.getProvidesVersion());
            		outputCodeSB.append(" see line: ").append( currGuids.get(test.getGuid()) );
            		outputCodeSB.append("\n");
            	}
            }
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
            StringBuilder descriptorAnnotation = new StringBuilder();
            switch (test.getAssertionType().toUpperCase()) {
                case "MEASURE":
                    if (test.getDimension().equalsIgnoreCase("Completeness")) {
                        retType = "DQResponse<CompletenessValue>";
                        retTypeJavaDoc = "DQResponse the response of type CompletenessValue ";
                    } else {
                        retType = "DQResponse<NumericalValue>";
                        retTypeJavaDoc = "DQResponse the response of type NumericalValue ";
                    }
                    descriptorAnnotation.append("@Measure(label=\"").append(test.getLabel()).append("\", description=\"");
                    descriptorAnnotation.append(test.getDescription()).append("\"");
                    descriptorAnnotation.append(", dimension= Dimension." + test.getDimension().toUpperCase());
                    descriptorAnnotation.append(")");
                    break;
                case "VALIDATION":
                    retType = "DQResponse<ComplianceValue>";
                    retTypeJavaDoc = "DQResponse the response of type ComplianceValue ";
                    descriptorAnnotation.append("@Validation(label=\"").append(test.getLabel()).append("\", description=\"");
                    descriptorAnnotation.append(test.getDescription()).append("\")");
                    break;
                case "AMENDMENT":
                    retType = "DQResponse<AmendmentValue>";
                    retTypeJavaDoc = "DQResponse the response of type AmendmentValue";
                    descriptorAnnotation.append("@Amendment(label=\"").append(test.getLabel()).append("\", description=\"");
                    descriptorAnnotation.append(test.getDescription()).append("\")");
                    break;
                case "ISSUE":
                    retType = "DQResponse<IssueValue>";
                    retTypeJavaDoc = "DQResponse the response of type IssueValue";
                    descriptorAnnotation.append("@Issue(label=\"").append(test.getLabel()).append("\", description=\"");
                    descriptorAnnotation.append(test.getDescription()).append("\")");
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

            // Javadoc comment first
            outputCodeSB.append("    /**\n");
            outputCodeSB.append("     * ").append(test.getDescription()).append("\n");
            outputCodeSB.append("     *\n");
            outputCodeSB.append("     * Provides: ").append(test.getLabel()).append("\n");
            outputCodeSB.append("     * Version: ").append(test.getVersion()).append("\n");
            outputCodeSB.append("     *\n");

            for (String key : params.keySet()) {
            	String param = params.get(key);
                outputCodeSB.append("     * @param ").append(param).append(" the provided ").append(key).append(" to evaluate\n");
            }
            
            if (retTypeJavaDoc!=null) { 
                outputCodeSB.append("     * @return ").append(retTypeJavaDoc).append(" to return\n");
            }
            outputCodeSB.append("     */\n");
            outputCodeSB.append("    ").append(descriptorAnnotation).append("\n");
            outputCodeSB.append("    @Provides(\"").append(test.getGuid()).append("\")\n");
            outputCodeSB.append("    @ProvidesVersion(\"").append(test.getProvidesVersion()).append("\")\n");
            outputCodeSB.append("    @Specification(\"").append(test.getSpecification().replace('"', '\'') ).append("\")\n");
            outputCodeSB.append("    public ").append(retType).append(" ").append(methodName).append("(");


            int cnt = 0;
            for (String term : params.keySet()) {
                outputCodeSB.append("@ActedUpon(\"").append(term).append("\") String ").append(params.get(term));

                if (cnt < ie.size() - 1) {
                    outputCodeSB.append(", ");
                }

                cnt++;
            }

            List<String> specificationWords = java.util.Arrays.asList(test.getSpecification().split("\\s+"));
            
            outputCodeSB.append(") {\n");
            outputCodeSB.append("        ").append(retType).append(" result = ").append("new ").append(retType).append("();\n\n");
            outputCodeSB.append("        //TODO:  Implement specification").append("\n");
            // Split the specification into words on whitespace, then print specification in lines with
            // the last word boundary being before character 55 in the string (plus an indent and comment chars).
            Iterator<String> i = specificationWords.iterator();
            StringBuffer specificationLine = new StringBuffer();
            while (i.hasNext()) { 
            	specificationLine.append(i.next()).append(" ");
            	if (specificationLine.length()>55) { 
            	     outputCodeSB.append("        // ").append(specificationLine.toString()).append("\n");
            	     specificationLine = new StringBuffer();
            	}
            }
            outputCodeSB.append("        // ").append(specificationLine.toString()).append("\n");
            outputCodeSB.append("\n");
            // Test Parameters change the behavior of the test.
            if (test.getTestParameters()!=null && test.getTestParameters().size()>0) { 
            	StringBuilder testParamCommentLines = new StringBuilder();
            	Iterator<String> ipar = test.getTestParameters().iterator();
            	while (ipar.hasNext()) { 
            		String testParam = ipar.next();
            		if (testParam!=null && testParam.trim().length()>0) { 
            			testParamCommentLines.append("        // ").append(testParam).append("\n");
            		}
            	}
            	if (testParamCommentLines.length()>0) { 
            		outputCodeSB.append("        //TODO: Parameters. This test is defined as parameterized.").append("\n");
            		outputCodeSB.append(testParamCommentLines);
            		outputCodeSB.append("\n");
            	}
            }
            outputCodeSB.append("        return result;\n");
            outputCodeSB.append("    }\n\n");
        }
    }

    public void writeOut(OutputStream out) throws IOException {
        outputCodeSB.append("}\n");
        out.write(outputCodeSB.toString().getBytes());
        System.out.println();
        if (testsAdded > 0) {
            logger.info("Added new stub methods for " + testsAdded + " unimplemented tests.");
        } else {
            logger.info("DQ Class is up to date. No new tests added.");
        }

    }
}
