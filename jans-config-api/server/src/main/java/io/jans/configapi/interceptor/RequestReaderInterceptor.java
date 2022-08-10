/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.configapi.interceptor;

//import io.jans.as.common.model.common.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.jans.configapi.core.interceptor.RequestInterceptor;
import io.jans.orm.PersistenceEntryManager;
import io.jans.configapi.core.rest.ProtectedApi;
import io.jans.configapi.core.util.DataUtil;
import io.jans.configapi.security.service.AuthorizationService;
import io.jans.configapi.util.ApiConstants;
import io.jans.configapi.util.DataProcessingUtil;
import io.jans.orm.PersistenceEntryManager;
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
import java.util.stream.Stream;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;
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
    private static final String[] IGNORE_METHODS = { "@jakarta.ws.rs.GET()", "@jakarta.ws.rs.DELETE()", "@jakarta.ws.rs.OPTIONS()", "@jakarta.ws.rs.PATCH()" };

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
    

    @Inject
    PersistenceEntryManager persistenceEntryManager;

    @SuppressWarnings({ "all" })
    @AroundInvoke
    public Object aroundReadFrom(InvocationContext context) throws Exception {
        System.out.println("\n\n\n RequestReaderInterceptor: entry - log=" + log + " logger=" + logger
                + " ,  request:{} " + request + "  info:{} " + info + ". resourceInfo=" + resourceInfo
                + " , context:{} " + context + " , dataProcessingUtil = " + dataProcessingUtil+" , persistenceEntryManager"+persistenceEntryManager+" \n\n\n");
        try {
            logger.error(
                    "======================= RequestReaderInterceptor Performing DataType Conversion ============================");

            // context
            logger.error(
                    "======RequestReaderInterceptor - context.getClass():{}, context.getConstructor(), context.getContextData():{},  context.getMethod():{},  context.getParameters():{}, context.getTarget():{}, context.getInputStream():{} ",
                    context.getClass(), context.getConstructor(), context.getContextData(), context.getMethod(),
                    context.getParameters(), context.getTarget());
            
            //method
            logger.error(
                    "======RequestReaderInterceptor - context.getMethod().getAnnotatedExceptionTypes().toString() :{}, context.getMethod().getAnnotatedParameterTypes().toString() :{}, context.getMethod().getAnnotatedReceiverType().toString() :{}, context.getMethod().getAnnotation(jakarta.ws.rs.GET.class):{}, context.getMethod().getAnnotations().toString() :{}., context.getMethod().getAnnotationsByType(jakarta.ws.rs.GET.class):{} ",
                    context.getMethod().getAnnotatedExceptionTypes().toString(), context.getMethod().getAnnotatedParameterTypes().toString(), context.getMethod().getAnnotatedReceiverType().toString(), context.getMethod().getAnnotation(jakarta.ws.rs.GET.class),
                    context.getMethod().getAnnotations().toString(), context.getMethod().getAnnotationsByType(jakarta.ws.rs.GET.class));
            
            
            boolean contains = isIgnoreMethod(context);
            logger.error("====== context.getMethod():{} isIgnoreMethod:{}", contains);
            
            if (contains) {
                logger.error("====== Exiting RequestReaderInterceptor as no action required for {} method. ======",
                        context.getMethod());
                return context.proceed();
            }
           
            processRequest(context);

        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
        return context.proceed();
    }
    
    private boolean isIgnoreMethod(InvocationContext context) {
        logger.error("Checking if method to be ignored");
        if(context.getMethod().getAnnotations()==null || context.getMethod().getAnnotations().length<=0) {
            return false;            
        }
        
        for(int i=0; i<context.getMethod().getAnnotations().length;i++) {
            logger.error("======RequestReaderInterceptor - context.getMethod().getAnnotations()["+i+"]:{} ",context.getMethod().getAnnotations()[i]);
            
            logger.error("======RequestReaderInterceptor - anyMatch:{} ",Arrays.stream(IGNORE_METHODS).anyMatch(context.getMethod().getAnnotations()[i].toString()::equals));
            
            if(context.getMethod().getAnnotations()[i]!=null && Arrays.stream(IGNORE_METHODS).anyMatch(context.getMethod().getAnnotations()[i].toString()::equals)) {
                logger.error("======RequestReaderInterceptor - context.getMethod() matched and hence will be ignored!!!!");
                return true;                         
            }
        }
        return false;
    }

    private void processRequest(InvocationContext context)
            throws IOException, IllegalAccessException, InstantiationException {
        logger.error(
                "RequestReaderInterceptor Data -  context:{} , context.getClass():{}, context.getContextData():{}, context.getMethod():{} , context.getParameters():{} , context.getTarget():{} ",
                context, context.getClass(), context.getContextData(), context.getMethod(), context.getParameters(),
                context.getTarget());

        Object[] ctxParameters = context.getParameters();
        logger.error("RequestReaderInterceptor - Processing  Data -  ctxParameters:{} ", ctxParameters);

        Method method = context.getMethod();
        //logger.error("RequestReaderInterceptor - Processing  Data -  method:{} ", method, method.getParameterCount());

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
                                      
                    performDataConversion(castObject(obj, clazz));

                    logger.error("RequestReaderInterceptor final - obj -  obj:{} ", obj);

                }
            }
        }
    }

    private <T> T performDataConversion(T obj) {
        try {
            obj = dataProcessingUtil.encodeObjDataType(obj);
            logger.error("RequestReaderInterceptor -  Data  after encoding -  obj:{} , obj.getClass():{}", obj,
                    obj.getClass());
        } catch (Exception ex) {
            logger.error("Exception while data conversion ", ex.getMessage());
        }
        return obj;
    }

   

    private <T> T castObject(Object obj, Class<T> clazz) {
        T t = (T) clazz.cast(obj);
        return t;
    }
    
 
    
    public Date decodeTime(String baseDn, Long strDate) {
        log.error("Decode date value - baseDn:{}, strDate:{} ", baseDn, strDate);
        return persistenceEntryManager.decodeTime(baseDn, strDate.toString());
    }

}
