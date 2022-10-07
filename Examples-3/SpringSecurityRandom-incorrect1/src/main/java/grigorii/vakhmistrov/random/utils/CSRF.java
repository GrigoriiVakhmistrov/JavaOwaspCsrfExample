package grigorii.vakhmistrov.random.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.ByteBuffer;
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

        random.setSeed(hashLong(now.toEpochSecond()));

        session.setAttribute("time", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));

        var final_token = String.valueOf(random.nextLong());
        session.setAttribute("tkn", final_token);
    }

    private static long hashLong(long seed) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(Long.toString(seed).getBytes(StandardCharsets.UTF_8));

        ByteBuffer buffer = ByteBuffer.wrap(hash);
        return buffer.getLong();
    }
}
