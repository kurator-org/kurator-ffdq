package org.datakurator.data.ffdq.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lowery on 12/15/16.
 */
public class ValidationTest {
    private String name;
    private Method method;
    private List<ValidationParam> inputs = new ArrayList<>();

    public ValidationTest(String name, Method method) {
        this.name = name;
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public List<ValidationParam> getInputs() {
        return inputs;
    }

    public void setInputs(List<ValidationParam> inputs) {
        this.inputs = inputs;
    }

    public void addParam(ValidationParam param) {
        inputs.add(param);
    }

    public List<String> fieldsActedUpon() {
        List<String> actedUpon = new ArrayList<>();

        for (ValidationParam param : inputs) {
            if (param.getUsage() == ValidationParam.ACTED_UPON)
                actedUpon.add(param.getTerm());
        }

        return actedUpon;
    }

    public List<String> fieldsConsulted() {
        List<String> consulted = new ArrayList<>();

        for (ValidationParam param : inputs) {
            if (param.getUsage() == ValidationParam.CONSULTED)
                consulted.add(param.getTerm());
        }

        return consulted;
    }


    @Override
    public String toString() {
        return "ValidationTest{" +
                "name='" + name + '\'' +
                ", method=" + method +
                ", inputs=" + inputs +
                '}';
    }
}
