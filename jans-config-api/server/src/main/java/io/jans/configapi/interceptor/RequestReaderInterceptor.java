/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.configapi.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import  io.jans.configapi.core.util.Jackson;
import io.jans.configapi.core.rest.ProtectedApi;
import io.jans.configapi.core.util.DataUtil;
import io.jans.configapi.security.service.AuthorizationService;
import io.jans.configapi.util.ApiConstants;
import io.jans.configapi.util.DataProcessingUtil;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;


import org.json.JSONException;
import org.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(value = 10)
public class RequestReaderInterceptor implements ReaderInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestReaderInterceptor.class);
    
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
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        System.out.println("\n\n\n RequestReaderInterceptor: entry - log:{} "+ log+" logger:{} "+logger+" ,  request:{} "+ request+"  info:{} "+ info+" , context:{} "+ context );
        logger.error(
                "======================= RequestReaderInterceptor Performing DataType Conversion ============================");
        logger.error(" request.getRemoteAddr():{},  info.getPath():{} , context.getMediaType():{}",
                request.getRemoteAddr(), info.getPath(), context.getMediaType());
       // InputStream is = context.getInputStream();
       // String body = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));

        //context.setInputStream(
        //        new ByteArrayInputStream((body + " message added in server reader interceptor").getBytes()));

        logger.error("======ReaderInterceptorContext - resourceInfo:{}, resourceInfo.getResourceMethod():{} ", resourceInfo,
                resourceInfo.getResourceMethod());
        logger.error(
                "======ReaderInterceptorContext - resourceInfo.getResourceMethod().getParameterCount():{}, resourceInfo.getResourceMethod().getParameters():{},  resourceInfo.getResourceMethod().getParameterTypes():{}",
                resourceInfo.getResourceMethod().getParameterCount(), resourceInfo.getResourceMethod().getParameters(),
                resourceInfo.getResourceMethod().getParameterTypes());
        
        readObject(context);
        processRequest(context);

        return context.proceed();
    }
    
    private JsonNode readObject(ReaderInterceptorContext context) throws IOException {
        logger.error("======ReaderInterceptorContext - readObject() - context:{}, context.getType() ", context, context.getType());
     // Create a Jackson ObjectMapper instance (it can be injected instead)
        
        String entityStr = IOUtils.toString(request.getInputStream(), "UTF-8");
        logger.error("======ReaderInterceptorContext - readObject() - entityStr:{} ", entityStr);
        
        ObjectMapper mapper = new ObjectMapper();

        // Parse the requested entity into a JSON tree
        JsonNode tree = mapper.readTree(context.getInputStream());
        logger.error("======ReaderInterceptorContext - readObject() - tree:{} ", tree);
        
        InputStream is = context.getInputStream();
        String body = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
        tree = Jackson.asJsonNode(body);
        logger.error("======ReaderInterceptorContext - readObject() - Jackson.asJsonNode(body):{} ", tree);
        
        logger.error("======ReaderInterceptorContext - readObject() - Jackson.read(is, new Object()):{} ", Jackson.read(is, new Object()));
        
        return tree;
    }

    private void processRequest(ReaderInterceptorContext context) {
        logger.error("ReaderInterceptorContext Data -  context.getHeaders():{} , context.getPropertyNames():{} ",
                context.getHeaders(), context.getPropertyNames());
        int paramCount = resourceInfo.getResourceMethod().getParameterCount();
        Parameter[] parameters = resourceInfo.getResourceMethod().getParameters();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Collection<String> propertyNames = context.getPropertyNames();
        logger.error(
                "AuthorizationFilter Processing  Data -  paramCount:{} , parameters:{}, parameterMap:{} , propertyNames:{}",
                paramCount, parameters, parameterMap, propertyNames);

        if (propertyNames != null && !propertyNames.isEmpty()) {
            for (String propertyName : propertyNames) {
                logger.error("propertyName:{} , propertyNames", propertyName, context.getProperty(propertyName));

                // encode data
                context.setProperty(propertyName,
                        dataProcessingUtil.encodeObjDataType(context.getProperty(propertyName)));
                logger.error("Final context.getProperty(propertyName):{} ", context.getProperty(propertyName));
            }
        }
    }
}
