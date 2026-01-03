package io.plantya.user.config;

import com.password4j.Argon2Function;
import com.password4j.types.Argon2;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordService {

    private final Argon2Function argon2;

    public PasswordService(Argon2Config config) {
        this.argon2 = Argon2Function.getInstance(
                config.memory(),
                config.iterations(),
                config.parallelism(),
                config.outputLength(),
                Argon2.valueOf(config.type().toUpperCase())
        );
    }

    public String hash(String raw) {
        return argon2.hash(raw).getResult();
    }
}