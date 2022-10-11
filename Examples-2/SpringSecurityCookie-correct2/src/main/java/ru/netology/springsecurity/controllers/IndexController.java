package ru.netology.springsecurity.controllers;

import com.google.gson.Gson;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;
import ru.netology.springsecurity.AuthenticatedUser;
import ru.netology.springsecurity.User;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/")
@Controller
public class IndexController {

    private final byte[] secureKey;

    private final ConcurrentHashMap<Long, User> users;

    public IndexController() {
        byte[] s = new byte[32];
        new SecureRandom().nextBytes(s);
        secureKey = s;

        users = new ConcurrentHashMap<>();
        var user = User.builder()
                .id(1)
                .name("user")
                .reskey(null)
                .passwordHash("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8") //password
                .build();

        users.put(user.getId(), user);
    }

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String index(HttpServletRequest request, HttpServletResponse response) {
        if (checkAuth(request))
            return "index";
        else if (Objects.equals(request.getMethod(), "POST")) {
            if (checkCSRF(request)) {
                var name = request.getParameter("trl");
                var pswd = request.getParameter("pswd");

                if (checkPassword(name, pswd)) {
                    var userId = getUserId(name);

                    var current_user = users.get(userId);
                    current_user.setToken(getSecureToken(24));
                    current_user.setReskey("0000");

                    var cookie = new Cookie("CSRF-Token", current_user.getToken());
                    cookie.setSecure(true);
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);

                    request.setAttribute("CSRF", current_user.getToken());

                    createAuth(response, userId);
                    return "redirect:/account";
                }

                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
        }

        var first_token = getSecureToken(24);

        var cookie = new Cookie("CSRF-Token", first_token);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        request.setAttribute("CSRF", first_token);

        return "index";
    }

    @RequestMapping(value = "/account", method = {RequestMethod.GET, RequestMethod.POST})
    public String accounts(HttpServletRequest request) {
        if (checkAuth(request)) {
            var cur_user = getUser(request);

            if (Objects.equals(request.getMethod(), HttpMethod.GET.name())) {
                request.setAttribute("username", cur_user.getName());
                request.setAttribute("CSRF", cur_user.getToken());
                request.setAttribute("reskey", cur_user.getReskey());

                return "main";
            }

            if (Objects.equals(request.getMethod(), HttpMethod.POST.name())) {
                if (checkCSRF(request)) {
                    var newkey = request.getParameter("newKey");

                    cur_user.setReskey(newkey);

                    request.setAttribute("username", cur_user.getName());
                    request.setAttribute("CSRF", cur_user.getToken());
                    request.setAttribute("reskey", cur_user.getReskey());

                    return "main";
                }
            }
        } else {
            return "redirect:/";
        }
        return "warning";
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET})
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        var jsonCookie = Arrays.stream(request.getCookies()).filter(x -> Objects.equals(x.getName(), "json-cookie")).findFirst();
        var jsonHmac = Arrays.stream(request.getCookies()).filter(x -> Objects.equals(x.getName(), "json-hmac")).findFirst();

        if (jsonCookie.isPresent()) {
            var jsonCookieValue = jsonCookie.get();

            jsonCookieValue.setValue("");
            jsonCookieValue.setPath("/");
            jsonCookieValue.setMaxAge(0);

            response.addCookie(jsonCookieValue);
        }

        if (jsonHmac.isPresent()) {
            var jsonHmacValue = jsonHmac.get();

            jsonHmacValue.setValue("");
            jsonHmacValue.setPath("/");
            jsonHmacValue.setMaxAge(0);

            response.addCookie(jsonHmacValue);
        }

        return "index";
    }

    private boolean checkCSRF(HttpServletRequest request) {
        var csrfCookie = Arrays.stream(request.getCookies()).filter(x -> Objects.equals(x.getName(), "CSRF-Token")).findFirst();

        var tkn = request.getParameter("CSRF-Token");

        if (csrfCookie.isPresent() && csrfCookie.get().getValue().equals(tkn))
            return true;
        else if (csrfCookie.isPresent() && !Objects.equals(csrfCookie.get().getValue(), tkn))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    private boolean checkAuth(HttpServletRequest request) {
        if (request.getCookies() != null) {
            var jsonCookie = Arrays.stream(request.getCookies()).filter(x -> Objects.equals(x.getName(), "json-cookie")).findFirst();
            var jsonHmac = Arrays.stream(request.getCookies()).filter(x -> Objects.equals(x.getName(), "json-hmac")).findFirst();

            String msg;
            String auth;

            if (jsonCookie.isPresent() && jsonHmac.isPresent()) {
                msg = new String(Base64.getDecoder().decode(jsonCookie.get().getValue()));
                auth = jsonHmac.get().getValue();
            } else
                return false;

            if (Objects.equals(hmac(msg.getBytes()), auth)) {
                //load user from json
                // return user['is_authenticated']
                return true;
            }
        }

        return false;
    }


    private boolean checkPassword(String name, String pswd) {
        long userId = getUserId(name);

        if (userId < 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        User user = users.get(userId);

        if (Objects.equals(user.getPasswordHash(), sha256(pswd)))
            return true;
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    private static String sha256(String message) {
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("SHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(hash);
    }

    private long getUserId(String name) {
        for (var user : users.values()) {
            if (Objects.equals(user.getName(), name))
                return user.getId();
        }

        return -1;
    }

    private String hmac(byte[] data) {
        try {
            var algorithm = "HmacSHA512";

            SecretKeySpec secretKeySpec = new SecretKeySpec(secureKey, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKeySpec);
            return bytesToHex(mac.doFinal(data));
        } catch (Exception e) {
            return null;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte h : hash) {
            String hex = Integer.toHexString(0xff & h);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static String getSecureToken(int length) {
        SecureRandom random = new SecureRandom();

        byte[] iv = new byte[length];
        random.nextBytes(iv);

        return bytesToHex(iv);
    }

    private void createAuth(HttpServletResponse response, long userId) {
        var cur_user = users.get(userId);

        var jsonUser = AuthenticatedUser.builder()
                .name(cur_user.getName())
                .isAuthenticated(1L)
                .build();

        var jsonCookieString = new Gson().toJson(jsonUser);

        byte[] encodedBytes = Base64.getEncoder().encode(jsonCookieString.getBytes());
        var jsonCookie = new Cookie("json-cookie", new String(encodedBytes));
        jsonCookie.setSecure(true);
        jsonCookie.setHttpOnly(true);
        response.addCookie(jsonCookie);

        var jsonHmac = new Cookie("json-hmac", hmac(jsonCookieString.getBytes()));
        jsonHmac.setSecure(true);
        jsonHmac.setHttpOnly(true);
        response.addCookie(jsonHmac);
    }

    private User getUser(HttpServletRequest request) {
        var jsonCookie = Arrays.stream(request.getCookies()).filter(x -> Objects.equals(x.getName(), "json-cookie")).findFirst();

        if (jsonCookie.isPresent()) {

            var authUser = new Gson().fromJson(new String(Base64.getDecoder().decode(jsonCookie.get().getValue())), AuthenticatedUser.class);
            var userId= getUserId(authUser.getName());
            return users.get(userId);
        }

        return null;
    }
}
