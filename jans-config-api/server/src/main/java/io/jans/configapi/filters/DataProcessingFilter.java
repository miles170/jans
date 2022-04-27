/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.configapi.filters;

import io.jans.configapi.util.DataProcessingUtil;
import io.jans.configapi.util.ApiConstants;


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

import java.util.Enumeration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;


@Provider
public class DataProcessingFilter implements ContainerRequestFilter {

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
        log.error("======================= DataProcessingFilter Performing DataType Conversion ============================");
        log.error("======" + context.getMethod() + " " + info.getPath() + " FROM IP " + request.getRemoteAddr());
        String method = context.getMethod();
        String path = info.getPath();
        
       
        try {
            log.error("======DataProcessingFilter - resourceInfo:{}, resourceInfo.getResourceMethod():{} ",resourceInfo, resourceInfo.getResourceMethod());
            log.error("======DataProcessingFilter - resourceInfo.getResourceMethod().getParameterCount():{}, resourceInfo.getResourceMethod().getParameters():{},  resourceInfo.getResourceMethod().getParameterTypes():{}", resourceInfo.getResourceMethod().getParameterCount(), resourceInfo.getResourceMethod().getParameters(), resourceInfo.getResourceMethod().getParameterTypes());
            processData();
            

            log.error("======DataType Conversion SUCCESS===========================================");
        } catch (Exception ex) {
            log.error("======DataType Conversion FAILED ===========================================", ex);
            //abortWithUnauthorized(context);
        }

    }

    public String[] getParameterValues(String paramName) {
        String values[] = getParameterValues(paramName);
        if ("dangerousParamName".equals(paramName)) {
            for (int index = 0; index < values.length; index++) {
                //values[index] = sanitize(values[index]);
            }
        }
        return values;
    }

    private void processData() {
        log.error("DataProcessingFilter - Processing  Data -  request.getAttributeNames():{} , request.getParameterNames():{} ", request.getAttributeNames(), request.getParameterNames());
        for (Enumeration en =  request.getAttributeNames(); en.hasMoreElements(); ) {
            String name = (String)en.nextElement();
            log.error(" name:{} ",name);
            String values[] = request.getParameterValues(name);
            log.error(" values:{} ",values);
            int n = values.length;
                for(int i=0; i < n; i++) {
                 //values[i] = values[i].replaceAll("[^\\dA-Za-z ]","").replaceAll("\\s+","+").trim();   
                    dataProcessingUtil.encodeObjDataType(null);
                }
            }
    }

}
