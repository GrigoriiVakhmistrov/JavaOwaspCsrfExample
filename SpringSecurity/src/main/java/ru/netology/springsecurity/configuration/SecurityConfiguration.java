package ru.netology.springsecurity.configuration;

import org.owasp.csrfguard.CsrfGuardFilter;
import org.owasp.csrfguard.CsrfGuardServletContextListener;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin()
                .and()
                .authorizeRequests().anyRequest().authenticated();

        http.csrf().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User
                .withUsername("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
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
