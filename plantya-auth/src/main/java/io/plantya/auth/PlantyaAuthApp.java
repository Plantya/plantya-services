package io.plantya.auth;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class PlantyaAuthApp {
    public static void main(String ...args) {
        Quarkus.run(args);
    }
}
