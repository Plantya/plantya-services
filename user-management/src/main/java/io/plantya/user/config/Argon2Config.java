package io.plantya.user.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "security.password.argon2")
public interface Argon2Config {

    int memory();
    int iterations();
    int parallelism();
    int outputLength();
    String type();
}