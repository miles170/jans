/*
 * Janssen Project software is available under the Apache License (2004). See http://www.apache.org/licenses/ for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.eleven.service;

import com.google.common.base.Strings;
import io.jans.eleven.util.StringUtils;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Javier Rojas Blum
 * @author Yuriy Movchan
 * @version June 9, 2022
 */
@WebFilter(
        asyncSupported = true,
        urlPatterns = {
                "/restv/generateKey",
                "/restv/sign",
                "/restv/verifySignature",
                "/restv/deleteKey"
        },
        displayName = "jans-eleven Test Mode Filter")
public class TestModeTokenFilter implements Filter {

    @Inject
    private Logger log;

    @Inject
    @Named("configurationFactory")
    private ConfigurationFactory configurationFactory;

    private static final String jansElevenGenerateKeyEndpoint = "restv/generateKey";
    private static final String jansElevenSignEndpoint = "restv/sign";
    private static final String jansElevenVerifySignatureEndpoint = "restv/verifySignature";
    private static final String jansElevenDeleteKeyEndpoint = "restv/deleteKey";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String path = httpServletRequest.getRequestURL().toString();
            if (!Strings.isNullOrEmpty(path)) {
                if (path.endsWith(jansElevenGenerateKeyEndpoint)
                        || path.endsWith(jansElevenSignEndpoint)
                        || path.endsWith(jansElevenVerifySignatureEndpoint)
                        || path.endsWith(jansElevenDeleteKeyEndpoint)) {
                    if (httpServletRequest.getHeader("Authorization") != null) {
                        String header = httpServletRequest.getHeader("Authorization");
                        if (header.startsWith("Bearer ")) {
                            String accessToken = header.substring(7);
                            String testModeToken = configurationFactory.getConfiguration().getTestModeToken();
                            if (!Strings.isNullOrEmpty(accessToken) && !Strings.isNullOrEmpty(testModeToken)
                                    && accessToken.equals(testModeToken)) {
                                chain.doFilter(request, response);
                                return;
                            }
                        }
                    }

                    sendError((HttpServletResponse) response);
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse servletResponse) {
        PrintWriter out = null;
        try {
            out = servletResponse.getWriter();

            servletResponse.setStatus(401);
            servletResponse.addHeader("WWW-Authenticate", "Bearer");
            servletResponse.setContentType(MediaType.APPLICATION_JSON);
            out.write(StringUtils.getErrorResponse(
                    "unauthorized",
                    "The request is not authorized."
            ));
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public void destroy() {
        // nothing
    }
}
