/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.configapi.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jans.configapi.core.util.Jackson;
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
import java.lang.reflect.Constructor;
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
        System.out.println("\n\n\n RequestReaderInterceptor: entry - log:{} " + log + " logger:{} " + logger
                + " ,  request:{} " + request + "  info:{} " + info + " , context:{} " + context);
        logger.error(
                "======================= RequestReaderInterceptor Performing DataType Conversion ============================");
        logger.error(" request.getRemoteAddr():{},  info.getPath():{} , context.getMediaType():{}",
                request.getRemoteAddr(), info.getPath(), context.getMediaType());

        logger.error("======ReaderInterceptorContext - resourceInfo:{}, resourceInfo.getResourceMethod():{} ",
                resourceInfo, resourceInfo.getResourceMethod());
        logger.error(
                "======ReaderInterceptorContext - resourceInfo.getResourceMethod().getParameterCount():{}, resourceInfo.getResourceMethod().getParameters():{},  resourceInfo.getResourceMethod().getParameterTypes():{}",
                resourceInfo.getResourceMethod().getParameterCount(), resourceInfo.getResourceMethod().getParameters(),
                resourceInfo.getResourceMethod().getParameterTypes());

        JsonNode jsonNode = readObject(context);
        processRequest(context, jsonNode);

        return context.proceed();
    }

    private JsonNode readObject(ReaderInterceptorContext context) throws WebApplicationException {
        logger.error("======ReaderInterceptorContext - readObject() - context:{}, context.getType() ", context, context.getType());
        JsonNode jsonNode = null;
 try {
        
        String entityStr = IOUtils.toString(request.getInputStream(), "UTF-8");
        logger.error("======ReaderInterceptorContext - readObject() - entityStr:{} ", entityStr);
       
        logger.error("======ReaderInterceptorContext - readObject() - request.getAttributeNames():{}, request.getContentType(),  request.getInputStream().getClass():{}", request.getAttributeNames(), request.getContentType(),  request.getInputStream().getClass());
        

        
        jsonNode = Jackson.asJsonNode(entityStr);
        logger.error("======ReaderInterceptorContext - readObject() - jsonNode:{} , jsonNode.getClass():{} , jsonNode.getClass().getName():{}", jsonNode,  jsonNode.getClass(), jsonNode.getClass().getName());
       
       
        
        Class clazz = Class.forName(jsonNode.getClass().getName());
        Object obj = clazz.newInstance();
        logger.error("======ReaderInterceptorContext - readObject() - clazz:{} ",clazz, request.getInputStream().getClass());
                
        Jackson.getObject(entityStr,obj);
        
         }catch(Exception ex) {
            throw new WebApplicationException(ex);
        }
        return jsonNode;
    }

    private void processRequest(ReaderInterceptorContext context, JsonNode jsonNode) {
        logger.error("ReaderInterceptorContext Data -  context:{} , jsonNode:{} ", context, jsonNode);
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

    public <T> T getInstance(Class<T> type) throws IllegalAccessException, InstantiationException {
        Object o = type.newInstance();
        T t = type.cast(o);
        return t;
    }

}
