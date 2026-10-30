package com.mycompany.mavenproject1;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utilitas pengamanan kata sandi (hanya memakai pustaka bawaan Java).
 * Kata sandi tidak pernah disimpan apa adanya: yang disimpan adalah salt
 * acak dan hash PBKDF2-HMAC-SHA256 dari kata sandi tersebut.
 */
public final class PasswordUtil {

    private static final int ITERASI = 65536;
    private static final int PANJANG_BIT = 256;

    private PasswordUtil() { }

    public static String buatSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hash(String password, String saltBase64) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                Base64.getDecoder().decode(saltBase64), ITERASI, PANJANG_BIT);
        try {
            byte[] hasil = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hasil);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Gagal membuat hash kata sandi.", e);
        } finally {
            spec.clearPassword();
        }
    }

    /** Membandingkan kata sandi yang diketik dengan hash tersimpan. */
    public static boolean cocok(String password, String saltBase64, String hashTersimpan) {
        byte[] a = hash(password, saltBase64).getBytes(StandardCharsets.UTF_8);
        byte[] b = hashTersimpan.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }
}
