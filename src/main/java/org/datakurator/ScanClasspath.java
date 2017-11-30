package org.datakurator;

import org.datakurator.ffdq.annotations.DQClass;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;

public class ScanClasspath {
    public static void main(String[] args) {
        Set<Class<?>> cls = new Reflections().getTypesAnnotatedWith(DQClass.class);

        for (Class<?> c : cls) {
            System.out.println(cls);
        }
    }
}
