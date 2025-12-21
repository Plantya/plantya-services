package io.plantya.iot;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class DeviceClusterManagementApp {
    public static void main(String... args) {
        Quarkus.run(args);
    }
}
