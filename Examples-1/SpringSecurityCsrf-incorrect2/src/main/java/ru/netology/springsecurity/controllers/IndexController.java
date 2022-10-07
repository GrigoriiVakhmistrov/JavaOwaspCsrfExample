package ru.netology.springsecurity.controllers;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.netology.springsecurity.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/")
@Controller
public class IndexController {
    ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();

    User testUser = User
            .builder()
            .id(1)
            .username("test")
            .password("test")
            .balance(2000).build();

    User hacker = User
            .builder()
            .id(2)
            .username("hacker")
            .password("hacker")
            .balance(0).build();


    public IndexController() {
        users.put(1L, testUser);
        users.put(2L, hacker);
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/accounts", method = {RequestMethod.GET, RequestMethod.POST})
    public String accounts(HttpServletRequest request) {
        var user = getCurrentUser();

        Map<String, String[]> data = null;
        User transfer_to = null;

        long account;
        var amount = user.getTransferSum();

        if (Objects.equals(request.getMethod(), HttpMethod.POST.name())) {
            amount = Long.parseLong(request.getParameter("amount"));
            account = Long.parseLong(request.getParameter("account"));
            transfer_to = users.get(account);
        } else {
            data = request.getParameterMap();
        }

        if (data != null) {
            amount = Long.parseLong(data.get("amount")[0]);
            account = Long.parseLong(data.get("account")[0]);

            transfer_to = users.get(account);
        }

        if (amount <= user.getBalance() && transfer_to != null) {
            user.setBalance(user.getBalance() - amount);
            transfer_to.setBalance(transfer_to.getBalance() + amount);
        }

        return "index";
    }

    @ModelAttribute("user")
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var optionalUser = users.values().stream().filter(x -> Objects.equals(x.getUsername(), authentication.getName())).findFirst();

        if (optionalUser.isEmpty())
            return new User();

        return optionalUser.get();
    }
}
