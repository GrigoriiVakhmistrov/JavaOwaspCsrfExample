package ru.netology.springsecurity.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

@RequestMapping("/")
@Controller
public class IndexController {
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        if (session.getAttribute("nm") != null) {
            return "redirect:/account";
        }
        else if (Objects.equals(request.getMethod(), "POST")) {
            var name = request.getParameter("trl");
            var tkn = getHex();
            session.setAttribute("nm", name);
            session.setAttribute("tkn", tkn);
            session.setAttribute("resKey", "0000");

            response.addCookie(new Cookie("CSRF-Token", tkn));
            return "redirect:/account";
        }

        return "index";
    }

    @RequestMapping(value = "/account", method = {RequestMethod.GET, RequestMethod.POST})
    public String accounts(HttpServletRequest request, HttpSession session) {
        var tkn = session.getAttribute("tkn");
        var nm = session.getAttribute("nm").toString();

        if (tkn != null && nm != null) {
            if (checkCSRF(request, session)) {
                if (Objects.equals(request.getMethod(), "GET")) {
                    return "main";
                } else if (Objects.equals(request.getMethod(), "POST")) {
                    var newKey = request.getParameter("newKey");
                    session.setAttribute("resKey", newKey);
                    return "main";
                }
            }
        } else if (nm != null) {
            return "token-error";
        } else {
            return "redirect:/";
        }

        return "warning";
    }

    @GetMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("nm");
        session.removeAttribute("tkn");
        session.removeAttribute("resKey");

        return "redirect:/";
    }

    private boolean checkCSRF(HttpServletRequest request, HttpSession session) {
        var csrfCookie = Arrays.stream(request.getCookies()).filter(x -> Objects.equals(x.getName(), "CSRF-Token")).findFirst();

        if (csrfCookie.isPresent() && csrfCookie.get().getValue().equals(session.getAttribute("tkn").toString()))
            return true;
        else if (csrfCookie.isPresent() && !Objects.equals(csrfCookie.get().getValue(), session.getAttribute("tkn").toString()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    private String getHex() {
        SecureRandom secureRandom = new SecureRandom();

        byte[] hex = new byte[16];

        secureRandom.nextBytes(hex);

        return String.format("%032x", new BigInteger(1, hex));
    }
}
