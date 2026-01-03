package io.plantya.user;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class UserManagementApp {
    public static void main(String... args) {
        Quarkus.run(args);
    }
}
