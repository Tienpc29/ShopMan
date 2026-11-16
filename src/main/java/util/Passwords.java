package util;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.*;
import java.util.Base64;

public final class Passwords {
    private Passwords() {}
    public static boolean verify(String password, String stored) {
        try {
            if (password == null || stored == null) return false;
            String[] parts = stored.split("[$]");
            if (parts.length != 4 || !"pbkdf2".equals(parts[0])) return false;
            int rounds = Integer.parseInt(parts[1]);
            if (rounds < 10000 || rounds > 1000000) return false;
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, rounds, 256);
            byte[] actual;
            try { actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
            finally { spec.clearPassword(); }
            return MessageDigest.isEqual(actual, Base64.getDecoder().decode(parts[3]));
        } catch (Exception e) { return false; }
    }
}
