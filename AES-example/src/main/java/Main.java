import java.security.SecureRandom;

public class Main {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();

        byte[] iv = new byte[16];
        random.nextBytes(iv);
        System.out.println("iv: " + bytesToHex(iv));

        byte[] r = new byte[16];
        random.nextBytes(r);
        String csrf_token = bytesToHex(r);
        System.out.println("CSRF: " + csrf_token);


        byte[] s = new byte[32];
        random.nextBytes(s);
        String secret_key = bytesToHex(s);
        System.out.println("Secret key: " + secret_key);


        byte[] t = new byte[8];
        random.nextBytes(t);
        String salt = bytesToHex(t);
        System.out.println("Salt: " + salt);


        String encrypted = AES256.encrypt(iv, csrf_token, secret_key, salt);
        String decrypted = AES256.decrypt(iv, encrypted, secret_key, salt);

        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte h : hash) {
            String hex = Integer.toHexString(0xff & h);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
