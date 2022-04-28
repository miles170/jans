/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.configapi.filters;

import io.jans.configapi.core.rest.ProtectedApi;
import io.jans.configapi.security.service.AuthorizationService;
import io.jans.configapi.util.ApiConstants;
import io.jans.configapi.util.DataProcessingUtil;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.io.*;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

/**
 * @author Mougang T.Gasmyr
 */
@Provider
@ProtectedApi
@Priority(Priorities.AUTHENTICATION)
public class AuthorizationFilter implements ContainerRequestFilter {

    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Inject
    Logger log;

    @Context
    UriInfo info;

    @Context
    HttpServletRequest request;

    @Context
    private HttpHeaders httpHeaders;

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    AuthorizationService authorizationService;

    @Inject
    DataProcessingUtil dataProcessingUtil;

    @SuppressWarnings({ "all" })
    public void filter(ContainerRequestContext context) {
        log.info("=======================================================================");
        log.info("====== context = " + context + " , info.getAbsolutePath() = " + info.getAbsolutePath()
                + " , info.getRequestUri() = " + info.getRequestUri() + "\n\n");
        log.info("====== info.getBaseUri()=" + info.getBaseUri() + " info.getPath()=" + info.getPath()
                + " info.toString()=" + info.toString());
        log.info("====== request.getContextPath()=" + request.getContextPath() + " request.getRequestURI()="
                + request.getRequestURI() + " request.toString() " + request.toString());
        log.info("======" + context.getMethod() + " " + info.getPath() + " FROM IP " + request.getRemoteAddr());
        log.info("======PERFORMING AUTHORIZATION=========================================");
        String authorizationHeader = context.getHeaderString(HttpHeaders.AUTHORIZATION);
        String issuer = context.getHeaderString(ApiConstants.ISSUER);
        boolean configOauthEnabled = authorizationService.isConfigOauthEnabled();
        log.info("\n\n\n AuthorizationFilter::filter() - authorizationHeader = " + authorizationHeader + " , issuer = "
                + issuer + " , configOauthEnabled = " + configOauthEnabled + "\n\n\n");

        if (!configOauthEnabled) {
            log.info("====== Authorization Granted...====== ");
            return;
        }

        log.info("\n\n\n AuthorizationFilter::filter() - Config Api OAuth Valdation Enabled");
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(context);
            log.info("======ONLY TOKEN BASED AUTHORIZATION IS SUPPORTED======================");
            return;
        }
        try {
            authorizationHeader = this.authorizationService.processAuthorization(authorizationHeader, issuer,
                    resourceInfo, context.getMethod(), request.getRequestURI());

            if (authorizationHeader != null && authorizationHeader.trim().length() > 0) {
                context.getHeaders().remove(HttpHeaders.AUTHORIZATION);
                context.getHeaders().add(HttpHeaders.AUTHORIZATION, authorizationHeader);
            }

            // encode data
            log.error("======AuthorizationFilter - resourceInfo:{}, resourceInfo.getResourceMethod():{} ", resourceInfo,
                    resourceInfo.getResourceMethod());
            log.error(
                    "======AuthorizationFilter - resourceInfo.getResourceMethod().getParameterCount():{}, resourceInfo.getResourceMethod().getParameters():{},  resourceInfo.getResourceMethod().getParameterTypes():{}",
                    resourceInfo.getResourceMethod().getParameterCount(),
                    resourceInfo.getResourceMethod().getParameters(),
                    resourceInfo.getResourceMethod().getParameterTypes());
            processData(context);

            log.info("======AUTHORIZATION  GRANTED===========================================");
        } catch (Exception ex) {
            log.error("======AUTHORIZATION  FAILED ===========================================", ex);
            abortWithUnauthorized(context);
        }

    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null
                && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME).build());
    }

    private void processData(ContainerRequestContext context) {
        log.error(
                "AuthorizationFilter Processing  Data -  request.getAttributeNames():{} , request.getAttributeNames():{} ",
                request.getAttributeNames(), request.getAttributeNames());

        log.error(
                "AuthorizationFilter Processing  Data -  request.getParameterMap():{} , request.getParameterNames():{} ",
                request.getParameterMap(), request.getParameterNames());

        // get path parameters
        context.getUriInfo().getPathParameters();
        // get request message body
        InputStream inputStreamOriginal = context.getEntityStream();
        log.error(
                "AuthorizationFilter Processing  Data -  context.getUriInfo().getPathParameters():{} , inputStreamOriginal:{} ",
                context.getUriInfo().getPathParameters(), inputStreamOriginal);

        int paramCount = resourceInfo.getResourceMethod().getParameterCount();
        Parameter[] parameters = resourceInfo.getResourceMethod().getParameters();
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.error("AuthorizationFilter Processing  Data -  paramCount:{} , parameters:{, parameterMap:{} }", paramCount,
                parameters, parameterMap);

        /*
         * if(parameterMap!=null && !parameterMap.isEmpty()) { for (Map.Entry<String,
         * String[]> entry : parameterMap.entrySet()) {
         * log.error("entry.getKey():{}, entry.getValue():{}", entry.getKey(),
         * entry.getValue());
         * 
         * //encode data
         * dataProcessingUtil.encodeObjDataType(request.getParameter(entry.getKey()));
         * 
         * log.error("Final entry.getKey():{}, entry.getValue():{}", entry.getKey(),
         * entry.getValue()); } }
         */

        /*
         * for(int i = 0; i< parameters.length; i ++) { log.
         * error("AuthorizationFilter Processing  Data -   parameters[i].getName():{} ,  parameters[i].getParameterizedType():{}, parameters[i].getType():{}"
         * , parameters[i].getName() , parameters[i].getParameterizedType(),
         * parameters[i].getType()); Object obj =
         * dataProcessingUtil.encodeObjDataType(request.getAttribute(parameters[i].
         * getName())); request.setAttribute(parameters[i].getName(), obj);
         * 
         * }
         */
    }
/*
    private void getEntityInputStream(ContainerRequestContext context) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = request.getInputStream();
        final StringBuilder b = new StringBuilder();
        try {
            if (in.available() > 0) {
                //ReaderWriter.writeTo(in, out);

                byte[] requestEntity = out.toByteArray();
                printEntity(b, requestEntity);

              
            }
          
        } catch (IOException ex) {
            throw new  Exception(ex);
        }
    }
    */
    private void printEntity(StringBuilder b, byte[] entity) throws IOException {
        if (entity.length == 0)
            return;
        b.append(new String(entity)).append("\n");
        log.error("#### Intercepted Entity ####");
        log.error(b.toString());
    }
}
