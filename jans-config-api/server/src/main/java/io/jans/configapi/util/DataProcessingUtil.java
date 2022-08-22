package io.jans.configapi.util;


import io.jans.agama.model.Flow;
import io.jans.configapi.core.util.DataTypeConversionMapping;
import io.jans.configapi.configuration.ConfigurationFactory;
import io.jans.orm.PersistenceEntryManager;
import io.jans.orm.model.AttributeData;
import io.jans.orm.reflect.property.Getter;
import io.jans.orm.reflect.property.PropertyAnnotation;
import io.jans.orm.reflect.property.Setter;
import io.jans.orm.reflect.util.ReflectHelper;
import io.jans.util.StringHelper;
import io.jans.util.security.StringEncrypter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;


import io.jans.configapi.core.util.DataUtil;

@ApplicationScoped
public class DataProcessingUtil {

    @Inject
    Logger log;
	

    @Inject
    ConfigurationFactory configurationFactory;
    
    @Inject 
    PersistenceEntryManager persistenceEntryManager;
    
    public DataTypeConversionMapping getDataTypeConversionMapping() {
        return this.configurationFactory.getApiAppConfiguration().getDataTypeConversionMap();
    }
   
   
    public <T> T encodeObjDataType(T obj) throws ClassNotFoundException, IllegalAccessException,
    InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        log.error("Encode DataType for obj:{} with  name:{}", obj, obj.getClass().getName());
        if (obj == null) {
            return (T) obj;
        }

        obj = encodeObjDataType(obj, getDataTypeConversionMapping());
        
        log.error("Data after encoding - obj:{}", obj);

        return obj;
    }
    
    

    public <T> T encodeObjDataType(T obj, DataTypeConversionMapping dataTypeConversionMap)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

        log.error("Encode DataType for obj:{} using dataTypeConversionMap:{} ", obj, dataTypeConversionMap);

        if (obj == null || dataTypeConversionMap == null) {
            return obj;
        }

        log.error("Getting propertMap for obj:{} ", obj.getClass());
        Map<String, String> objectPropertyMap = DataUtil.getFieldTypeMap(obj.getClass());
        log.error("obj:{} objectPropertyMap:{} ", obj.getClass(), objectPropertyMap);

        for (Map.Entry<String, String> entry : objectPropertyMap.entrySet()) {
            log.error("entry.getKey():{}, entry.getValue():{}", entry.getKey(), entry.getValue());

            // check if attribute is in exclusion map
            log.error(
                    "obj.getClass().getName():{}, entry.getKey():{} dataTypeConversionMap.getExclusion():{}, isAttributeInExclusion:{}",
                    obj.getClass().getName(), entry.getKey(), dataTypeConversionMap.getExclusion(),
                    DataUtil.isAttributeInExclusion(obj.getClass().getName(), entry.getKey(),
                            dataTypeConversionMap.getExclusion()));
            if (DataUtil.isAttributeInExclusion(obj.getClass().getName(), entry.getKey(),
                    dataTypeConversionMap.getExclusion())) {
                log.error("Breaking as the filed:{} is in exclusion list for obj:{}", obj.getClass().getName(),
                        entry.getKey());
                break;
            }

            // encode data
            encodeData(obj, entry, dataTypeConversionMap.getDataTypeConverterClassName(),
                    dataTypeConversionMap.getEncoder());
            log.error("Final obj:{} ", obj);
        }

        return obj;
    }

    public <T> T encodeData(T obj, Map.Entry<String, String> entryData, String dataTypeConverterClassName,
            Map<String, String> encoderMap) throws ClassNotFoundException, IllegalAccessException,
    InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        log.error("Encoding data for obj:{} , entryData:{}, dataTypeConverterClassName:{}, encoderMap:{} ", obj,
                entryData, dataTypeConverterClassName, encoderMap);

        if (obj == null || entryData == null || dataTypeConverterClassName == null || encoderMap == null
                || encoderMap.isEmpty()) {
            return obj;
        }

     
        log.error("DataUtil.isKeyPresentInMap(entryData.getValue():{}, encoderMap:{}):{} ", entryData.getValue(), encoderMap,
                DataUtil.isKeyPresentInMap(entryData.getValue(), encoderMap));

        if (DataUtil.isKeyPresentInMap(entryData.getValue(), encoderMap)) {
            Getter getterMethod = DataUtil.getGetterMethod(obj.getClass(), entryData.getKey());
            log.error("getterMethod:{}, getValue(obj:{},entryData.getKey():{}) ->:{} ", getterMethod, obj,
                    entryData.getKey(), DataUtil.getValue(obj, entryData.getKey()));

            Object propertyValue = getterMethod.getMethod().invoke(obj);
            log.error("from getterMethod() method -  key:{}, propertyValue:{} , getterMethod.getReturnType():{},",
                    entryData.getKey(), propertyValue, getterMethod.getReturnType());

            propertyValue = DataUtil.getValue(obj, entryData.getKey());
            log.error("from getValue() method - key:{}, propertyValue:{} , getterMethod.getReturnType():{},",
                    entryData.getKey(), propertyValue, getterMethod.getReturnType());

            // Invoke encode method
            propertyValue = getEncodeMethod(dataTypeConverterClassName, entryData, encoderMap, propertyValue);
            log.error("After encoding value key:{}, propertyValue:{}  ", entryData.getKey(), propertyValue);

            Setter setterMethod = DataUtil.getSetterMethod(obj.getClass(), entryData.getKey());
            propertyValue = setterMethod.getMethod().invoke(obj, propertyValue);
            log.error("After setterMethod invoked key:{}, propertyValue:{} ", entryData.getKey(), propertyValue);

            propertyValue = getterMethod.get(obj);
            log.error("Final - key:{}, propertyValue:{} ", entryData.getKey(), propertyValue);
        }

        return obj;
    }
    
    public Object getEncodeMethod(String dataTypeConverterClassName, Map.Entry<String, String> entryData,
            Map<String, String> encoderMap, Object value) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        log.error(
                "Invoke encode method from dataTypeConverterClassName:{} for entryData:{} based on encoderMap:{}, value:{}",
                dataTypeConverterClassName, entryData, encoderMap, value);

        Object returnValue = null;
        if (entryData == null || encoderMap == null || encoderMap.isEmpty()) {
            return returnValue;
        }

        log.error(" From map:{} the value of entryData.getKey():{} is :{}", encoderMap, entryData.getKey(),
                encoderMap.get(entryData.getValue()));
        String methodName = encoderMap.get(entryData.getValue());
        log.error(" key:{} methodName:{}", entryData.getKey(), methodName);

        Class<?> clazz = DataUtil.getClass(dataTypeConverterClassName);
        log.error(" dataTypeConverterClassName:{}, clazz.isPrimitive():{} ", clazz, clazz.isPrimitive());

        Object obj = null;

        if (!clazz.isPrimitive()) {
            obj = clazz.cast(DataUtil.getInstance(clazz));
            log.error(" obj:{} ", obj);
            // Getter getterMethod = getGetterMethod(clazz, methodName);
            // log.error(" dataTypeConverterClass getterMethod:{} ", getterMethod);
            // returnValue = method.invoke(obj, value);
            Method method = clazz.getMethod(methodName);
            log.error(" dataTypeConverterClass method:{} ", method);

            returnValue = method.invoke(value);
            log.error(" dataTypeConverterClass returnValue:{} ", returnValue);
        }
        log.error(" key:{} value:{} -> returnValue:{}", entryData.getKey(), value, returnValue);
        return returnValue;

    }
    

     

   
}
