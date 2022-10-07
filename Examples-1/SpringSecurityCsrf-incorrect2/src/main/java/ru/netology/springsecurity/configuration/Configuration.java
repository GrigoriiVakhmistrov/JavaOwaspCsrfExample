package ru.netology.springsecurity.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@org.springframework.context.annotation.Configuration
public class Configuration {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .successHandler((request, response, authentication) -> {
                    var token = new HttpSessionCsrfTokenRepository().generateToken(request);
                    var session = request.getSession();
                    System.out.println(token.getToken());
                    session.setAttribute("tkn", token.getToken());

                    redirectStrategy.sendRedirect(request, response, "/");
                })
                .and()
                .logout().logoutSuccessHandler((request, response, authentication) -> {
                    var session = request.getSession();
                    session.removeAttribute("tkn");

                    redirectStrategy.sendRedirect(request, response, "/");
                })
                .and()
                .authorizeRequests().anyRequest().authenticated();

        return http.build();
    }

    @Bean
    public UserDetailsManager userDetailsService() {
        UserDetails user1 = User.withDefaultPasswordEncoder()
                .username("test")
                .password("test")
                .roles("USER")
                .build();
        UserDetails user2 = User.withDefaultPasswordEncoder()
                .username("hacker")
                .password("hacker")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user1, user2);
    }
}
