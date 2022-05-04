/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.configapi.filters;

import com.fasterxml.jackson.databind.JsonNode;

import io.jans.as.common.model.common.User;
import io.jans.configapi.util.DataProcessingUtil;
import io.jans.configapi.util.ApiConstants;
import io.jans.configapi.core.util.Jackson;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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

import java.util.Enumeration;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class DataProcessingFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(DataProcessingFilter.class);
    private static final String[] IGNORE_METHODS = { "GET", "DELETE", "OPTIONS", "PATCH" };

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
    DataProcessingUtil dataProcessingUtil;

    @SuppressWarnings({ "all" })
    public void filter(ContainerRequestContext context) {
        System.out.println("\n\n\n DataProcessingFilter: entry - log:{} " + log + " logger:{} " + logger
                + " ,  request:{} " + request + "  info:{} " + info + " , context:{} " + context
                + " , dataProcessingUtil = " + dataProcessingUtil);
        try {
            // general
            log.error(
                    "======================= DataProcessingFilter Performing DataType Conversion ============================");
            log.error("======" + context.getMethod() + "" + info.getPath() + " FROM IP " + request.getRemoteAddr());
            String method = context.getMethod();
            String path = info.getPath();

            // context
            log.error(
                    "======ReaderInterceptorContext - context.getClass():{}, context.getEntityStream(), context.getMethod():{},  context.getRequest():{}",
                    context.getClass(), context.getEntityStream(), context.getMethod(), context.getRequest());

            // request
            log.error(
                    "======ReaderInterceptorContext - request.getMethod():{}, request.getAttributeNames():{},  request.getParameterMap():{}, request.getParameterNames():{} ",
                    request.getMethod(), request.getAttributeNames(), request.getParameterMap(),
                    request.getParameterNames());

            // resourceInfo
            log.error(
                    "======ReaderInterceptorContext - resourceInfo:{}, resourceInfo.getResourceMethod():{} , getResourceMethod().getParameterCount():{}, resourceInfo.getResourceMethod().getParameters(), resourceInfo.getResourceMethod().getParameterTypes():{}",
                    resourceInfo, resourceInfo.getResourceMethod(),
                    resourceInfo.getResourceMethod().getParameterCount(),
                    resourceInfo.getResourceMethod().getParameters(),
                    resourceInfo.getResourceMethod().getParameterTypes());

            boolean contains = Arrays.stream(IGNORE_METHODS).anyMatch(request.getMethod()::equals);
            log.error("====== request.getMethod():{} present in ignoreList contains:{}", request.getMethod(), contains);

            if (contains) {
                log.error("====== Exiting ReaderInterceptorContext as no action required for {} method. ======",
                        request.getMethod());
            }

       
            log.error("======DataType Conversion SUCCESS===========================================");
        } catch (Exception ex) {
            log.error("======DataType Conversion FAILED ===========================================", ex);
            // abortWithUnauthorized(context);
        }

    }

    private String readObject(ContainerRequestContext context) throws WebApplicationException {
        log.error("======ReaderInterceptorContext - readObject() - context:{}, context.getType() ", context);
        JsonNode jsonNode = null;
        String entityStr = null;
        try {

            entityStr = IOUtils.toString(request.getInputStream(), "UTF-8");
            log.error("======ReaderInterceptorContext - readObject() - entityStr:{} ", entityStr);

            log.error(
                    "======ReaderInterceptorContext - readObject() - request.getAttributeNames():{}, request.getContentType(),  request.getInputStream().getClass():{}",
                    request.getAttributeNames(), request.getContentType(), request.getInputStream().getClass());

            jsonNode = Jackson.asJsonNode(entityStr);
            log.error(
                    "======ReaderInterceptorContext - readObject() - jsonNode:{} , jsonNode.getClass():{} , jsonNode.getClass().getName():{}",
                    jsonNode, jsonNode.getClass(), jsonNode.getClass().getName());

        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
        return entityStr;
    }

    private void processRequest(ContainerRequestContext context, String jsonNode)
            throws IOException, IllegalAccessException, InstantiationException {
        log.error("ReaderInterceptorContext Data -  context:{} , jsonNode:{} ", context, jsonNode);
        int paramCount = resourceInfo.getResourceMethod().getParameterCount();
        Parameter[] parameters = resourceInfo.getResourceMethod().getParameters();
        Class[] clazzArray = resourceInfo.getResourceMethod().getParameterTypes();

        log.error(
                "DataProcessingFilter - Processing  Data -  paramCount:{} , parameters:{}, clazzArray:{} , dataProcessingUtil:{}",
                paramCount, parameters, clazzArray, dataProcessingUtil);
        
        

        if (clazzArray != null && clazzArray.length > 0) {
            for (int i = 0; i < clazzArray.length; i++) {
                Class<?> clazz = clazzArray[i];
                String propertyName = parameters[i].getName();
                log.error("propertyName:{}, clazz:{} ", propertyName, clazz);

                T obj = read(context.getEntityStream(), getInstance(clazz));
                performDataConversion(obj);
            }
        }
    }

    private <T> T readObject(Class clazz)
            throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        InputStream inputStream = request.getInputStream();
        T obj = (T) getObjectInstance(clazz.getName());
        log.error("getObjectInstance - obj:{} ", obj.getClass());
        obj = (T) getInstance(clazz);
        log.error("getInstance - obj:{}", obj);
        return read(inputStream, obj);

    }

    public Object getObjectInstance(String name)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
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

    public String[] getParameterValues(String paramName) {
        String values[] = getParameterValues(paramName);
        if ("dangerousParamName".equals(paramName)) {
            for (int index = 0; index < values.length; index++) {
                // values[index] = sanitize(values[index]);
            }
        }
        return values;
    }
       
    private <T> T performDataConversion(T obj) {
        try {
            logger.error("DataProcessingFilter -  Data  for encoding -  obj:{} ", obj);
           
            obj = dataProcessingUtil.encodeObjDataType(obj);
            logger.error("DataProcessingFilter -  Data  after encoding -  obj:{} , obj.getClass():{}", obj, obj.getClass());            
        } catch (Exception ex) {
            logger.error("Exception while data conversion ", ex.getMessage());
        }
        return obj;
    }
    
    private <T> String getJsonString(T obj) {
        String jsonStr = null;
        try {
            jsonStr = dataProcessingUtil.getJsonString(obj);
            logger.error("DataProcessingFilter -  Object string -  sonStr:{}", jsonStr);            
        } catch (Exception ex) {
            logger.error("Exception while data conversion ", ex.getMessage());
        }
        return jsonStr;
    }


}
