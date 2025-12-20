package io.plantya.management.common.util;

import com.password4j.Password;

public class PasswordUtil {

    public static String hash(String password) {
        return Password.hash(password).withArgon2().getResult();
    }

    public static boolean verify(String password, String hashedPassword) {
        return Password.check(password, hashedPassword).withArgon2();
    }

}