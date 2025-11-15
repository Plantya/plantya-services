package com.agrosentinel.auth;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class AgroSentinelAuthApp {
    public static void main(String ...args) {
        Quarkus.run(args);
        Quarkus.waitForExit();

        System.out.println("AgroSentinelAuthApp started");
    }
}
