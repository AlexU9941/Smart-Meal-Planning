package smart_meal_planner.service;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {

    private static final int SALT_LENGTH = 16; // bytes
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // bits

    // Generate a random salt - random string to ensure different hashing for all passwords
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hash password with a given salt
    public static String hashPassword(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                Base64.getDecoder().decode(salt),
                ITERATIONS,
                KEY_LENGTH
            );
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Verify password
    public static boolean verifyPassword(String attemptedPassword, String storedHash, String storedSalt) {
        String attemptedHash = hashPassword(attemptedPassword, storedSalt);
        return attemptedHash.equals(storedHash);
    }
}
