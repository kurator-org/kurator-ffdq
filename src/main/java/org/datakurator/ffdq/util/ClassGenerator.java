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
 */
package org.datakurator.ffdq.util;

import org.datakurator.ffdq.runner.AssertionTest;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ClassGenerator {
    private final static Logger logger = Logger.getLogger(ClassGenerator.class.getName());

    private String mechanismGuid;
    private String mechanismName;

    private String className;
    private String packageName;

    private Map<String, Integer> currGuids = new HashMap<>();
    private int testsAdded = 0;

    private StringBuilder sb;

    public ClassGenerator(String mechanismGuid, String mechanismName, String packageName, String className) {
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

        sb.append("/* NOTE: requires the ffdq-api dependecy in the maven pom.xml */\n\n");
        sb.append("package ").append(packageName).append(";\n\n");

        sb.append("import org.datakurator.ffdq.annotations.*;\n");
        sb.append("import org.datakurator.ffdq.api.DQResponse;\n");
        sb.append("import org.datakurator.ffdq.model.ResultState;\n");
        sb.append("import org.datakurator.ffdq.api.result.*;\n\n");

        sb.append("@Mechanism(\"").append(mechanismGuid).append("\")\n");
        sb.append("public class ").append(className).append(" {\n\n");
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
    }

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
            switch (test.getAssertionType().toUpperCase()) {
                case "MEASURE":
                    if (test.getDimension().equalsIgnoreCase("Completeness")) {
                        retType = "DQResponse<CompletenessValue>";
                    } else {
                        retType = "DQResponse<NumericalValue>";
                    }
                    break;
                case "VALIDATION":
                    retType = "DQResponse<ComplianceValue>";
                    break;
                case "AMENDMENT":
                    retType = "DQResponse<AmendmentValue>";
                    break;
            }

            // params
            Map<String, String> params = new HashMap<>();
            List<String> ie = test.getInformationElement();
            for (int i = 0; i < ie.size(); i++) {
                String term = ie.get(i);
                String param = term.split(":")[1];

                params.put(term, param);
            }

            // Javadoc comment first
            sb.append("    /**\n");
            sb.append("     * ").append(test.getDescription()).append("\n");
            sb.append("     *\n");
            sb.append("     * Provides: ").append(test.getLabel()).append("\n");
            sb.append("     *\n");

            for (String param : params.values()) {
                sb.append("     * @param ").append(param).append("\n");
            }

            sb.append("     * @return ").append(retType).append("\n");
            sb.append("     */\n");
            sb.append("    @Provides(\"").append(test.getGuid()).append("\")\n");
            sb.append("    public ").append(retType).append(" ").append(methodName).append("(");


            int cnt = 0;
            for (String term : params.keySet()) {
                sb.append("@ActedUpon(\"").append(term).append("\") String ").append(params.get(term));

                if (cnt < ie.size() - 1) {
                    sb.append(", ");
                }

                cnt++;
            }

            sb.append(") {\n");
            sb.append("        ").append(retType).append(" result = ").append("new ").append(retType).append("();\n\n");
            sb.append("        // ...\n\n");
            sb.append("        return result;\n");
            sb.append("    }\n\n");
        }
    }

    public void writeOut(OutputStream out) throws IOException {
        sb.append("}\n");
        out.write(sb.toString().getBytes());
        System.out.println();
        if (testsAdded > 0) {
            logger.info("Added new stub methods for " + testsAdded + " unimplemented tests.");
        } else {
            logger.info("DQ Class is up to date. No new tests added.");
        }

    }
}
