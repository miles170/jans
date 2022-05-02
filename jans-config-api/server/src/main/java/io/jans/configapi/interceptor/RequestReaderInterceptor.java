/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.configapi.interceptor;

import io.jans.as.common.model.common.User;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@RequestInterceptor
@Priority(Interceptor.Priority.APPLICATION)
public class RequestReaderInterceptor  {

    private static final Logger logger = LoggerFactory.getLogger(RequestReaderInterceptor.class);
    private static final String[] IGNORE_METHODS = { "GET", "DELETE", "OPTIONS", "PATCH" };

    @Inject
    private Logger log;

    @Context
    UriInfo info;

    @Context
    HttpServletRequest request;

    @Context
    private HttpHeaders httpHeaders;

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    private DataProcessingUtil dataProcessingUtil;

    @SuppressWarnings({ "all" })
    @AroundInvoke    
    public Object aroundReadFrom(InvocationContext context) throws Exception {
        System.out.println("\n\n\n RequestReaderInterceptor: entry - log:{} " + log + " logger:{} " + logger
                + " ,  request:{} " + request + "  info:{} " + info + " , context:{} " + context+" , dataProcessingUtil = "+dataProcessingUtil);
        try {
        logger.error(
                "======================= RequestReaderInterceptor Performing DataType Conversion ============================");

        // general
        logger.error(
                " request.getMethod()():{}, request.getRemoteAddr():{},  info.getPath():{} , context.getMethod():{}",
                request.getMethod(), request.getRemoteAddr(), info.getPath(), context.getMethod());

        // context
        logger.error(
                "======ReaderInterceptorContext - context.getClass():{}, context.getConstructor(), context.getContextData():{},  context.getMethod():{},  context.getParameters():{}, context.getTarget():{}, context.getInputStream():{} ",
                context.getClass(), context.getConstructor(), context.getContextData(), context.getMethod(), context.getParameters(),
                context.getTarget());

        // request
        logger.error(
                "======ReaderInterceptorContext - request.getMethod():{}, request.getAttributeNames():{},  request.getParameterMap():{}, request.getParameterNames():{} ",
                request.getMethod(), request.getAttributeNames(), request.getParameterMap(),
                request.getParameterNames());

        // resourceInfo
        logger.error(
                "======ReaderInterceptorContext - resourceInfo:{}, resourceInfo.getResourceMethod():{} , getResourceMethod().getParameterCount():{}, resourceInfo.getResourceMethod().getParameters(), resourceInfo.getResourceMethod().getParameterTypes():{}",
                resourceInfo, resourceInfo.getResourceMethod(), resourceInfo.getResourceMethod().getParameterCount(),
                resourceInfo.getResourceMethod().getParameters(), resourceInfo.getResourceMethod().getParameterTypes());

        boolean contains = Arrays.stream(IGNORE_METHODS).anyMatch(request.getMethod()::equals);
        logger.error("====== request.getMethod():{} present in ignoreList contains:{}", request.getMethod(), contains);
        if (contains) {
            logger.error("====== Exiting ReaderInterceptorContext as no action required for {} method. ======",
                    request.getMethod());
            return context.proceed();
        }

        /*
         * JsonNode jsonNode = readObject(context);
         * logger.error("====== ReaderInterceptorContext jsonNode:{} ",jsonNode);
         * processRequest(context, jsonNode);
         */

        String jsonNode = readObject(context);
        logger.error("====== ReaderInterceptorContext jsonNode:{} ", jsonNode);
        processRequest(context, jsonNode);
        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
        return context.proceed();
    }

    private String readObject(InvocationContext context) throws WebApplicationException {
        logger.error("======ReaderInterceptorContext - readObject() - context:{}, context.getType() ", context);
        JsonNode jsonNode = null;
        String entityStr = null;
        try {

            entityStr = IOUtils.toString(request.getInputStream(), "UTF-8");
            logger.error("======ReaderInterceptorContext - readObject() - entityStr:{} ", entityStr);

            logger.error(
                    "======ReaderInterceptorContext - readObject() - request.getAttributeNames():{}, request.getContentType(),  request.getInputStream().getClass():{}",
                    request.getAttributeNames(), request.getContentType(), request.getInputStream().getClass());

            jsonNode = Jackson.asJsonNode(entityStr);
            logger.error(
                    "======ReaderInterceptorContext - readObject() - jsonNode:{} , jsonNode.getClass():{} , jsonNode.getClass().getName():{}",
                    jsonNode, jsonNode.getClass(), jsonNode.getClass().getName());

        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
        return entityStr;
    }

    private void processRequest(InvocationContext context, String jsonNode) throws IOException,IllegalAccessException, InstantiationException {
        logger.error("ReaderInterceptorContext Data -  context:{} , jsonNode:{} ", context, jsonNode);
        int paramCount = resourceInfo.getResourceMethod().getParameterCount();
        Parameter[] parameters = resourceInfo.getResourceMethod().getParameters();
        Class[] clazzArray = resourceInfo.getResourceMethod().getParameterTypes();

        logger.error("RequestReaderInterceptor - Processing  Data -  paramCount:{} , parameters:{}, clazzArray:{} , dataProcessingUtil:{}", paramCount,
                parameters, clazzArray,dataProcessingUtil);

        if (clazzArray != null && clazzArray.length > 0) {
            for (int i = 0; i < clazzArray.length; i++) {
                Class<?> clazz = clazzArray[i];
                String propertyName = parameters[i].getName();
                logger.error("propertyName:{}, clazz:{} ", propertyName, clazz);

                Object obj = getInstance(clazz);
                logger.error("RequestReaderInterceptor -  Processing  Data -  propertyName,{}, clazz.getClass():{}, clazz:{} , obj:{} , obj.getClass():{}",
                        propertyName, clazz.getClass(), clazz, parameters[i].getName(), obj, obj.getClass());

                User user = new User();
                user = Jackson.getObject(jsonNode, user);
                logger.error("user:{} , user.getUserId():{}", user, user.getUserId());
                
                obj = Jackson.getObject(jsonNode, obj);
                logger.error("obj:{} , obj.getClass():{},  dataProcessingUtil:{}", obj, obj.getClass(), dataProcessingUtil);
                
                // encode data
                //context.setProperty(propertyName, dataProcessingUtil.encodeObjDataType(clazz));
                //logger.error("Final context.getProperty(propertyName):{} ", context.getProperty(propertyName));
            }
        }
    }

    private <T> T readObject(Class clazz) throws IOException,ClassNotFoundException, IllegalAccessException, InstantiationException {
        InputStream inputStream =  request.getInputStream();
        T obj = (T) getObjectInstance(clazz.getName());
        logger.error("getObjectInstance - obj:{} ",obj.getClass());
        obj = (T) getInstance(clazz);
        logger.error("getInstance - obj:{}",obj);
        return read(inputStream,obj);
        
    }
    public Object getObjectInstance(String name) throws  ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> cls = Class.forName(name);
        Object clsInstance = (Object) cls.newInstance();
        return clsInstance;
    }
    
    public <T> T getInstance(Class<T> type) throws IllegalAccessException, InstantiationException {
        Object o = type.newInstance();
        T t = type.cast(o);
        return t;
    }
    
    private <T> T read(InputStream inputStream, T obj) throws IOException {
        return Jackson.read(inputStream, obj);        
    }

}
