package ru.netology.springsecurity;

import org.owasp.csrfguard.CsrfGuard;
import org.owasp.csrfguard.CsrfValidator;
import org.owasp.csrfguard.http.InterceptRedirectResponse;
import org.owasp.csrfguard.session.LogicalSession;
import org.owasp.csrfguard.token.storage.LogicalSessionExtractor;
import org.owasp.csrfguard.token.transferobject.TokenTO;
import org.owasp.csrfguard.util.CsrfGuardUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

public class CsrfGuardFilter implements Filter {

    private FilterConfig filterConfig = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(CsrfGuardFilter.class);

    @Override
    public void init(final FilterConfig filterConfig) {
        var properties = new Properties();
        properties.put("org.owasp.csrfguard.configuration.provider.factory", "org.owasp.csrfguard.config.overlay.ConfigurationAutodetectProviderFactory");
        properties.put("org.owasp.csrfguard.Enabled", true);
        properties.put("org.owasp.csrfguard.LogicalSessionExtractor", "org.owasp.csrfguard.session.SessionTokenKeyExtractor");
        properties.put("org.owasp.csrfguard.action.Empty", "org.owasp.csrfguard.action.Empty");
        properties.put("org.owasp.csrfguard.TokenPerPage", true);
        properties.put("org.owasp.csrfguard.Ajax", true);

        CsrfGuard.load(properties);

        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        final CsrfGuard csrfGuard = CsrfGuard.getInstance();

        if (csrfGuard.isEnabled()) {
            if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
                doFilter((HttpServletRequest) request, (HttpServletResponse) response, filterChain, csrfGuard);
            } else {
                handleNonHttpServletMessages(request, response, filterChain);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    private void doFilter(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final FilterChain filterChain, final CsrfGuard csrfGuard) throws IOException, ServletException {
        final InterceptRedirectResponse interceptRedirectResponse = new InterceptRedirectResponse(httpServletResponse, httpServletRequest, csrfGuard);

        final LogicalSessionExtractor sessionKeyExtractor = csrfGuard.getLogicalSessionExtractor();
        final LogicalSession logicalSession = sessionKeyExtractor.extract(httpServletRequest);

        if (logicalSession == null) {
            if (csrfGuard.isUseNewTokenLandingPage()) {
                final LogicalSession createdLogicalSession = sessionKeyExtractor.extractOrCreate(httpServletRequest);
                csrfGuard.writeLandingPage(interceptRedirectResponse, createdLogicalSession.getKey());
            } else {
                handleNoSession(httpServletRequest, httpServletResponse, interceptRedirectResponse, filterChain, csrfGuard);
            }
        } else {
            handleSession(httpServletRequest, interceptRedirectResponse, filterChain, logicalSession, csrfGuard);
        }
    }

    private void handleSession(final HttpServletRequest httpServletRequest, final InterceptRedirectResponse interceptRedirectResponse, final FilterChain filterChain,
                               final LogicalSession logicalSession, final CsrfGuard csrfGuard) throws IOException, ServletException {

        final String logicalSessionKey = logicalSession.getKey();

        if (new CsrfValidator().isValid(httpServletRequest, interceptRedirectResponse)) {
            filterChain.doFilter(httpServletRequest, interceptRedirectResponse);
        } else {
            logInvalidRequest(httpServletRequest);
        }

        final String requestURI = httpServletRequest.getRequestURI();
        final String generatedToken = csrfGuard.getTokenService().generateTokensIfAbsent(logicalSessionKey, httpServletRequest.getMethod(), requestURI);


        interceptRedirectResponse.setHeader(csrfGuard.getTokenName(), generatedToken);
        //CsrfGuardUtils.addResponseTokenHeader(csrfGuard, httpServletRequest, interceptRedirectResponse, new TokenTO(Collections.singletonMap(requestURI, generatedToken)));
    }

    private void handleNoSession(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final InterceptRedirectResponse interceptRedirectResponse, final FilterChain filterChain,
                                 final CsrfGuard csrfGuard) throws IOException, ServletException {
        if (csrfGuard.isValidateWhenNoSessionExists()) {
            if (new CsrfValidator().isValid(httpServletRequest, interceptRedirectResponse)) {
                filterChain.doFilter(httpServletRequest, interceptRedirectResponse);
            } else {
                logInvalidRequest(httpServletRequest);
            }
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    private void handleNonHttpServletMessages(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        final String message = String.format("CSRFGuard does not know how to work with requests of class %s ", request.getClass().getName());
        LOGGER.warn(message);
        this.filterConfig.getServletContext().log("[WARNING]" + message);

        filterChain.doFilter(request, response);
    }

    private void logInvalidRequest(final HttpServletRequest httpRequest) {
        final String requestURI = httpRequest.getRequestURI();
        final String remoteAddress = httpRequest.getRemoteAddr();

        LOGGER.warn("Invalid request: URI: '{}' | Remote Address: '{}'", requestURI, remoteAddress);
    }
}
