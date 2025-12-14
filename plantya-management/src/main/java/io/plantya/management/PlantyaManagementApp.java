package io.plantya.management;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class PlantyaManagementApp {
    public static void main(String... args) {
        Quarkus.run(args);
    }
}
