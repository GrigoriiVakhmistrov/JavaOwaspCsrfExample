package grigorii.vakhmistrov.random.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;

public class CSRF {
    public static boolean check(HttpServletRequest request, HttpSession session) {
        var token = request.getParameter("csrf_token");

        if (token != null && token.equals(session.getAttribute("tkn"))) {
            return true;
        }

        else if (token != null && !token.equals(session.getAttribute("tkn")))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    public static void createToken(HttpSession session) {
        SecureRandom random = new SecureRandom();

        byte[] iv = new byte[32];
        random.nextBytes(iv);

        session.setAttribute("tkn", bytesToHex(iv));
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
