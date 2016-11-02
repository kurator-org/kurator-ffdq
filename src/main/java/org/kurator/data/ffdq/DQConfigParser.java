package org.kurator.data.ffdq;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lowery on 11/2/16.
 */
public class DQConfigParser {
    private AssertionsConfig assertions;
    private static DQConfigParser instance;

    private DQConfigParser() { }

    public static DQConfigParser getInstance() {
        if (instance == null) {
            instance = new DQConfigParser();
        }

        return instance;
    }

    public void load(InputStream config) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        assertions = mapper.readValue(config, AssertionsConfig.class);
    }

    public AssertionsConfig getAssertions() {
        return assertions;
    }

    public static void main(String[] args) throws IOException {
        DQConfigParser configParser = DQConfigParser.getInstance();

        configParser.load(DQConfigParser.class.getResourceAsStream("/ffdq-assertions.json"));
        System.out.println(configParser.getAssertions().getValidations());
    }
}
