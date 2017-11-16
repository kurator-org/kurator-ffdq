package org.datakurator.ffdq.util;

import org.datakurator.ffdq.runner.AssertionTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 11/16/17.
 */
public class ClassGenerator {
    private final String mechanismGuid;
    private final String mechanismName;

    private final String className;
    private final String packageName;

    private StringBuilder sb;

    public ClassGenerator(String mechanismGuid, String mechanismName, String packageName, String className) {
        this.mechanismGuid = mechanismGuid;
        this.mechanismName = mechanismName;

        this.packageName = packageName;
        this.className = className;

        sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");

        sb.append("import org.datakurator.ffdq.annotations.*;\n\n");

        sb.append("@DQClass(\"").append(mechanismGuid).append("\")\n");
        sb.append("public class ").append(className).append(" {\n\n");
    }

    public void addTest(AssertionTest test) {

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
                retType = "EventDQMeasurement";
                break;
            case "VALIDATION":
                retType = "EventDQValidation";
                break;
            case "AMENDMENT":
                retType = "EventDQAmendment";
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
        sb.append("    @DQProvides(\"").append(test.getGuid()).append("\")\n");
        sb.append("    public ").append(retType).append(" ").append(methodName).append("(");


        int cnt = 0;
        for (String term : params.keySet()) {
            sb.append("@DQParam(\"").append(term).append("\") String ").append(params.get(term));

            if (cnt < ie.size()-1) {
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

    public void writeOut() {
        sb.append("}\n");

        System.out.println(sb.toString());
    }
}
