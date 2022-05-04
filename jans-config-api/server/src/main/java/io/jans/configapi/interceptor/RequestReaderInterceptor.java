/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.configapi.interceptor;

//import io.jans.as.common.model.common.User;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jans.configapi.core.interceptor.RequestInterceptor;

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

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@RequestInterceptor
@Priority(Interceptor.Priority.APPLICATION)
public class RequestReaderInterceptor {

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
        System.out.println("\n\n\n RequestReaderInterceptor: entry - log=" + log + " logger=" + logger
                + " ,  request:{} " + request + "  info:{} " + info + ". resourceInfo=" + resourceInfo
                + " , context:{} " + context + " , dataProcessingUtil = " + dataProcessingUtil);
        try {
            logger.error(
                    "======================= RequestReaderInterceptor Performing DataType Conversion ============================");

            // context
            logger.error(
                    "======ReaderInterceptorContext - context.getClass():{}, context.getConstructor(), context.getContextData():{},  context.getMethod():{},  context.getParameters():{}, context.getTarget():{}, context.getInputStream():{} ",
                    context.getClass(), context.getConstructor(), context.getContextData(), context.getMethod(),
                    context.getParameters(), context.getTarget());

            // resourceInfo
            /*
             * logger.error(
             * "======ReaderInterceptorContext - resourceInfo:{}, resourceInfo.getResourceMethod():{} , getResourceMethod().getParameterCount():{}, resourceInfo.getResourceMethod().getParameters(), resourceInfo.getResourceMethod().getParameterTypes():{}"
             * , resourceInfo, resourceInfo.getResourceMethod(),
             * resourceInfo.getResourceMethod().getParameterCount(),
             * resourceInfo.getResourceMethod().getParameters(),
             * resourceInfo.getResourceMethod().getParameterTypes());
             */

            boolean contains = Arrays.stream(IGNORE_METHODS).anyMatch(context.getMethod()::equals);
            logger.error("====== context.getMethod():{} present in ignoreList contains:{}", context.getMethod(),
                    contains);
            if (contains) {
                logger.error("====== Exiting ReaderInterceptorContext as no action required for {} method. ======",
                        context.getMethod());
                return context.proceed();
            }

            Object createdObject = context.getTarget();
            logger.error("====== createdObject:{] , createdObject.getClass():{}", createdObject,
                    createdObject.getClass());
            /*
             * JsonNode jsonNode = readObject(context);
             * logger.error("====== ReaderInterceptorContext jsonNode:{} ",jsonNode);
             * processRequest(context, jsonNode);
             */

            processRequest(context);

            // String jsonNode = readObject(context);
            // logger.error("====== ReaderInterceptorContext jsonNode:{} ", jsonNode);
            // processRequest(context, jsonNode);
        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
        return context.proceed();
    }

    private void processRequest(InvocationContext context)
            throws IOException, IllegalAccessException, InstantiationException {
        logger.error(
                "ReaderInterceptorContext Data -  context:{} , context.getClass():{}, context.getContextData():{}, context.getMethod():{} , context.getParameters():{} , context.getTarget():{} ",
                context, context.getClass(), context.getContextData(), context.getMethod(), context.getParameters(),
                context.getTarget());

        Object beanInstance = context.getTarget();
        logger.error("RequestReaderInterceptor - Processing  Data -  beanInstance:{} ", beanInstance);
        String json = getJsonString(beanInstance);
        logger.error("RequestReaderInterceptor - target -  json:{} ", json);

        Object[] ctxParameters = context.getParameters();
        logger.error("RequestReaderInterceptor - Processing  Data -  ctxParameters:{} ", ctxParameters);

        Method method = context.getMethod();
        logger.error("RequestReaderInterceptor - Processing  Data -  method:{} ", method, method.getParameterCount());

        int paramCount = method.getParameterCount();
        Parameter[] parameters = method.getParameters();
        Class[] clazzArray = method.getParameterTypes();

        logger.error(
                "RequestReaderInterceptor - Processing  Data -  paramCount:{} , parameters:{}, clazzArray:{} , dataProcessingUtil:{}",
                paramCount, parameters, clazzArray, dataProcessingUtil);

        if (clazzArray != null && clazzArray.length > 0) {
            for (int i = 0; i < clazzArray.length; i++) {
                Class<?> clazz = clazzArray[i];
                String propertyName = parameters[i].getName();
                logger.error("propertyName:{}, clazz:{} , clazz.isPrimitive():{} ", propertyName, clazz,
                        clazz.isPrimitive());

                Object obj = ctxParameters[i];
                if (!clazz.isPrimitive()) {
                    obj = clazz.cast(dataProcessingUtil.getInstance(clazz));
                    json = getJsonString(obj);
                    logger.error("RequestReaderInterceptor final - obj -  json:{} ", json);
                    
                    json = getJsonString(obj);
                    logger.error("RequestReaderInterceptor - obj -  json:{} ", json);

                    logger.error(
                            "RequestReaderInterceptor -  Processing  Data -  propertyName:{}, clazz.getClass():{}, clazz:{} , obj:{} , obj.getClass():{}",
                            propertyName, clazz.getClass(), clazz, parameters[i].getName(), obj, obj.getClass());

                    performDataConversion(obj);

                }
            }
        }
    }
    
    private <T> T performDataConversion(T obj) {
        try {
            obj = dataProcessingUtil.encodeObjDataType(obj);
            logger.error("RequestReaderInterceptor -  Data  after encoding -  obj:{} , obj.getClass():{}", obj, obj.getClass());            
        } catch (Exception ex) {
            logger.error("Exception while data conversion ", ex.getMessage());
        }
        return obj;
    }
    
    private <T> String getJsonString(T obj) {
        String jsonStr = null;
        try {
            jsonStr = dataProcessingUtil.getJsonString(obj);
            logger.error("RequestReaderInterceptor -  Object string -  sonStr:{}", jsonStr);            
        } catch (Exception ex) {
            logger.error("Exception while data conversion ", ex.getMessage());
        }
        return jsonStr;
    }

}
