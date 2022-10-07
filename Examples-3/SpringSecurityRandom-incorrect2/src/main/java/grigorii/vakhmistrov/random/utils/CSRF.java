package grigorii.vakhmistrov.random.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CSRF {
    public static boolean check(HttpServletRequest request, HttpSession session) {
        var token = request.getParameter("csrf_token");

        if (token != null && token.equals(session.getAttribute("tkn"))) {
            CSRF.createToken(session);
            return true;
        }

        else if (token != null && !token.equals(session.getAttribute("tkn")))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    public static void createToken(HttpSession session) {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var random = new Random();

        random.setSeed(now.toEpochSecond());

        session.setAttribute("time", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));

        var final_token = md5(random.nextLong());
        System.out.println(final_token);
        session.setAttribute("tkn", final_token);
    }

    private static String md5(long seed) {
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hash = digest.digest(Long.toString(seed).getBytes(StandardCharsets.UTF_8));

        return bytesToHex(hash);
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
