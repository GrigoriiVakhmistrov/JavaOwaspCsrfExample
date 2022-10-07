import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
        SecureRandom random = new SecureRandom();

        byte[] s = new byte[32];
        random.nextBytes(s);
        String secret_key = bytesToHex(s);
        System.out.println("Secret key: " + secret_key);

        byte[] r = new byte[16];
        random.nextBytes(r);
        String csrf_token = bytesToHex(r);
        System.out.println("CSRF: " + csrf_token);

        long timestamp = System.currentTimeMillis() / 1000L;
        System.out.println(timestamp);

        String message = csrf_token + '_' + timestamp;
        System.out.println("Message: " + message);


        String algorithm = "HmacSHA512";

        String result = hmacWithJava(algorithm, message.getBytes(), secret_key.getBytes());
        //String result = hmacWithApacheCommons(algorithm, message, secret_key);
        System.out.println("Result: " + result);

    }



    public static String hmacWithApacheCommons(String algorithm, String data, String key) {
        return new HmacUtils(algorithm, key).hmacHex(data);
    }

    public static String hmacWithJava(String algorithm, byte[] data, byte[] key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        return bytesToHex(mac.doFinal(data));
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
