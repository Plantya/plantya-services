package io.plantya.auth.util;

import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.ConfigProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class JwtUtil {

    public static PrivateKey loadPrivateKey(String path) throws Exception {
        String key = Files.readString(Path.of(path))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    public static String generateToken(String userId, String username, String role, long ttlMillis) throws Exception {
        String privateKeyPath = ConfigProvider.getConfig().getValue("private.key.path", String.class);
        PrivateKey privateKey = loadPrivateKey(privateKeyPath);

        long now = System.currentTimeMillis();
        long expired = now + ttlMillis;

        return Jwt.claims()
                .issuer("agro-sentinel")
                .subject(userId)
                .claim("username", username)
                .claim("role", role)
                .groups(role)
                .issuedAt(now / 1000)
                .expiresAt(expired / 1000)
                .sign(privateKey);
    }

}
