package grigorii.vakhmistrov.random.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import grigorii.vakhmistrov.random.utils.CSRF;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@RequestMapping("/")
@Controller
public class IndexController {
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String index(HttpServletRequest request, HttpSession session) {
        if (session.getAttribute("nm") != null) {
            return "redirect:/account";
        } else if (Objects.equals(request.getMethod(), "POST")) {
            if (CSRF.check(request, session)) {
                var name = request.getParameter("trl");
                session.setAttribute("nm", name);
                session.setAttribute("resKey", "0000");

                return "redirect:/account";
            }
        }

        CSRF.createToken(session);

        return "index";
    }

    @RequestMapping(value = "/account", method = {RequestMethod.GET, RequestMethod.POST})
    public String accounts(HttpServletRequest request, HttpSession session) {
        var tkn = session.getAttribute("tkn");
        var nm = session.getAttribute("nm");

        if (nm != null) {
            if (Objects.equals(request.getMethod(), "GET") && tkn != null) {
                session.setAttribute("tkn_val", session.getAttribute("tkn"));

                return "main";
            }

            if (Objects.equals(request.getMethod(), "POST"))
                if (CSRF.check(request, session)) {
                    session.setAttribute("resKey", request.getParameter("newKey"));
                    session.setAttribute("tkn_val", session.getAttribute("tkn"));

                    return "main";
                }
        }

        if (tkn != null && nm != null) {
            if (CSRF.check(request, session)) {
                if (Objects.equals(request.getMethod(), "GET")) {
                    return "main";
                } else if (Objects.equals(request.getMethod(), "POST")) {
                    var newKey = request.getParameter("newKey");
                    session.setAttribute("resKey", newKey);

                    return "main";
                }
            }
        } else
            return "redirect:/";

        return "warning";
    }

    @GetMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("nm");
        session.removeAttribute("tkn");
        session.removeAttribute("resKey");
        session.removeAttribute("time");

        return "redirect:/";
    }
}
