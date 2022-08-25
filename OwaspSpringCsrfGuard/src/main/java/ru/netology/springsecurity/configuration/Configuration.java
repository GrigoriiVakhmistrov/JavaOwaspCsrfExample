package ru.netology.springsecurity.configuration;

import org.owasp.csrfguard.CsrfGuardFilter;
import org.owasp.csrfguard.CsrfGuardServletContextListener;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http
                .formLogin()
                .loginPage("/login")
                .permitAll()
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

    @Bean
    public FilterRegistrationBean<CsrfGuardFilter> someFilterRegistration() {

        FilterRegistrationBean<CsrfGuardFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CsrfGuardFilter());
        registration.addUrlPatterns("/*");
        registration.setName("csrfGuardFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public CsrfGuardServletContextListener executorListener() {
        return new CsrfGuardServletContextListener();
    }
}
