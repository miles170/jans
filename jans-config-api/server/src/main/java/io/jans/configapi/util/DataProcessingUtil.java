package io.jans.configapi.util;

import io.jans.as.common.model.common.User;
import io.jans.configapi.core.util.DataTypeConversionMapping;
import io.jans.configapi.configuration.ConfigurationFactory;
import io.jans.configapi.service.auth.AttributeService;
import io.jans.model.GluuAttribute;
import io.jans.model.attribute.AttributeDataType;
import io.jans.orm.PersistenceEntryManager;
import io.jans.orm.annotation.AttributesList;
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
import java.lang.annotation.Annotation;
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

    @Inject
    AttributeService attributeService;

    public DataTypeConversionMapping getDataTypeConversionMapping() {
        return this.configurationFactory.getApiAppConfiguration().getDataTypeConversionMap();
    }

    public <T> T encodeObjDataType(T obj) throws ClassNotFoundException, IllegalAccessException, InstantiationException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        log.error("Encode DataType for obj:{} with  name:{}", obj, obj.getClass().getName());
        if (obj == null) {
            return (T) obj;
        }

        obj = encodeObjDataType(obj, getDataTypeConversionMapping());

        log.error("Data after encoding - obj:{}", obj);

        return obj;
    }

    public <T> T encodeObjDataType(T obj, DataTypeConversionMapping dataTypeConversionMap)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException {

        log.error("Encode DataType for obj:{} using dataTypeConversionMap:{} ", obj, dataTypeConversionMap);

        if (obj == null || dataTypeConversionMap == null) {
            return obj;
        }

        log.error("Getting propertMap for obj:{} ", obj.getClass());

        List<PropertyAnnotation> propertiesAnnotations = persistenceEntryManager
                .getEntryPropertyAnnotations(obj.getClass());
        log.error("\n DataProcessingUtil:::encodeObjDataType() -  propertiesAnnotations:{} ", propertiesAnnotations);

        List<AttributeData> attributes = persistenceEntryManager.getAttributesListForPersist(obj,
                propertiesAnnotations);
        log.error("\n DataProcessingUtil:::encodeObjDataType() -  attributes:{} ", attributes);

        for (PropertyAnnotation property : propertiesAnnotations) {
            log.error("property:{}", property);

            // check if attribute is in exclusion map
            log.error("Is propertyName:{} to be excluded ", property.getPropertyName(), DataUtil.isAttributeInExclusion(
                    obj.getClass().getName(), property.getPropertyName(), dataTypeConversionMap.getExclusion()));

            // ignore if the attribute is to be excluded from conversion
            if (DataUtil.isAttributeInExclusion(obj.getClass().getName(), property.getPropertyName(),
                    dataTypeConversionMap.getExclusion())) {
                log.error("Breaking as the filed:{} is in exclusion list for obj:{}", property.getPropertyName(),
                        obj.getClass().getName());
                break;
            }

            // encode data
            encodeData(obj, dataTypeConversionMap, property);
            log.error("Final obj:{} ", obj);
        }

        return obj;
    }

    public <T> T encodeData(T obj, DataTypeConversionMapping dataTypeConversionMap,
            PropertyAnnotation propertyAnnotation) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

        String dataTypeConverterClassName = dataTypeConversionMap.getDataTypeConverterClassName();
        Map<String, String> encoderMap = dataTypeConversionMap.getEncoder();

        log.error("Encoding data for obj:{}, propertyAnnotation:{}, dataTypeConverterClassName:{}, encoderMap:{} ", obj,
                propertyAnnotation, dataTypeConverterClassName, encoderMap);

        if (obj == null || dataTypeConverterClassName == null || encoderMap == null || encoderMap.isEmpty()) {
            return obj;
        }
        // Check if list
        if (propertyAnnotation.getAnnotations() != null
                && propertyAnnotation.getAnnotations().contains("io.jans.orm.annotation.AttributesList")) {
            log.error(
                    "\n In DataProcessingUtil:::encodeObjDataType() *********  It is a AttributesList!!!! ********* \n\n\n");
            // process AttributesList
            Annotation ldapAttribute = ReflectHelper.getAnnotationByType(propertyAnnotation.getAnnotations(),
                    AttributesList.class);
            getAttributeDataList(obj, dataTypeConversionMap, (AttributesList) ldapAttribute,
                    propertyAnnotation.getPropertyName());
        }

        // Get Attribute details
        String propertyName = propertyAnnotation.getPropertyName();
        AttributeDataType attributeDataType = getAttributeDetails(propertyName);
        log.error("\n In DataProcessingUtil:::encodeObjDataType() - propertyName:{}, attributeDataType.getValue():{}",
                propertyName, attributeDataType.getValue());
        try {

            if (DataUtil.isKeyPresentInMap(attributeDataType.getValue(), encoderMap)) {
                Getter getterMethod = DataUtil.getGetterMethod(obj.getClass(), propertyName);
                log.error("propertyName:{}, getterMethod:{} ", propertyName, getterMethod);

                Object propertyValue = getterMethod.getMethod().invoke(obj);
                log.error("from getterMethod() method -  key:{}, propertyValue:{} , getterMethod.getReturnType():{},",
                        propertyName, propertyValue, getterMethod.getReturnType());

                propertyValue = DataUtil.getValue(obj, propertyName);
                log.error("from getValue() method - key:{}, propertyValue:{} , getterMethod.getReturnType():{},",
                        propertyName, propertyValue, getterMethod.getReturnType());

                if (propertyValue != null) {
                    // Invoke encode method
                    propertyValue = getEncodedPropertyValue(dataTypeConverterClassName, encoderMap, propertyName,
                            attributeDataType, propertyValue);
                    log.error("After encoding value key:{}, propertyValue:{}  ", propertyName, propertyValue);

                    Setter setterMethod = DataUtil.getSetterMethod(obj.getClass(), propertyName);
                    propertyValue = setterMethod.getMethod().invoke(obj, propertyValue);
                    log.error("After setterMethod invoked key:{}, propertyValue:{} ", propertyName, propertyValue);

                    propertyValue = getterMethod.get(obj);
                    log.error("Final - key:{}, propertyValue:{} ", propertyName, propertyValue);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Error while encoding propertyName:{} is:{} ", propertyName, ex);
        }

        return obj;
    }

    public <T> T encodeData2(T obj, DataTypeConversionMapping dataTypeConversionMap, String propertyName) {
        log.error("\n In DataProcessingUtil:::encodeData2() - obj:{}, dataTypeConversionMap:{}, propertyName:{}", obj,
                dataTypeConversionMap, propertyName);
        String dataTypeConverterClassName = dataTypeConversionMap.getDataTypeConverterClassName();
        Map<String, String> encoderMap = dataTypeConversionMap.getEncoder();
        log.error("\n In DataProcessingUtil:::encodeData2() - dataTypeConverterClassName:{}, encoderMap:{}",
                dataTypeConverterClassName, encoderMap);

        AttributeDataType attributeDataType = getAttributeDetails(propertyName);
        log.error("\n In DataProcessingUtil:::encodeData() - propertyName:{}, attributeDataType.getValue():{}",
                propertyName, attributeDataType.getValue());
        try {

            if (DataUtil.isKeyPresentInMap(attributeDataType.getValue(), encoderMap)) {
                Getter getterMethod = DataUtil.getGetterMethod(obj.getClass(), propertyName);
                log.error("propertyName:{}, getterMethod:{} ", propertyName, getterMethod);

                Object propertyValue = getterMethod.getMethod().invoke(obj);
                log.error("from getterMethod() method -  key:{}, propertyValue:{} , getterMethod.getReturnType():{},",
                        propertyName, propertyValue, getterMethod.getReturnType());

                propertyValue = DataUtil.getValue(obj, propertyName);
                log.error("from getValue() method - key:{}, propertyValue:{} , getterMethod.getReturnType():{},",
                        propertyName, propertyValue, getterMethod.getReturnType());

                if (propertyValue != null) {
                    // Invoke encode method
                    propertyValue = getEncodedPropertyValue(dataTypeConverterClassName, encoderMap, propertyName,
                            attributeDataType, propertyValue);
                    log.error("After encoding value key:{}, propertyValue:{}  ", propertyName, propertyValue);

                    Setter setterMethod = DataUtil.getSetterMethod(obj.getClass(), propertyName);
                    propertyValue = setterMethod.getMethod().invoke(obj, propertyValue);
                    log.error("After setterMethod invoked key:{}, propertyValue:{} ", propertyName, propertyValue);

                    propertyValue = getterMethod.get(obj);
                    log.error("Final - key:{}, propertyValue:{} ", propertyName, propertyValue);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Error while encoding propertyName:{} is:{} ", propertyName, ex);
        }

        return obj;
    }

    public Object getEncodedPropertyValue(String dataTypeConverterClassName, Map<String, String> encoderMap,
            String propertyName, AttributeDataType attributeDataType, Object value)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException {
        log.error(
                "Invoke encode method from dataTypeConverterClassName:{} based on encoderMap:{} for propertyName:{} attributeDataType:{}  value:{}",
                dataTypeConverterClassName, encoderMap, propertyName, attributeDataType, value);

        Object returnValue = null;
        if (attributeDataType == null || encoderMap == null || encoderMap.isEmpty()) {
            return returnValue;
        }

        log.error(" From map:{} the value of attributeDataType():{} is :{}", encoderMap, attributeDataType,
                encoderMap.get(attributeDataType.getValue()));
        String methodName = encoderMap.get(attributeDataType.getValue());
        log.error(" key:{} methodName:{}", propertyName, methodName);

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
        log.error(" key:{} value:{} -> returnValue:{}", propertyName, value, returnValue);
        return returnValue;

    }

    /*
     * 
     * public Object getEncodeMethod(String dataTypeConverterClassName, Map<String,
     * String> encoderMap, String propertyName, AttributeDataType attributeDataType,
     * Object value) throws ClassNotFoundException, IllegalAccessException,
     * InstantiationException, NoSuchMethodException, IllegalArgumentException,
     * InvocationTargetException { log.error(
     * "Invoke encode method from dataTypeConverterClassName:{} based on encoderMap:{} for propertyName:{} attributeDataType:{}  value:{}"
     * , dataTypeConverterClassName, encoderMap, propertyName, attributeDataType,
     * value);
     * 
     * Object returnValue = null; if (attributeDataType == null || encoderMap ==
     * null || encoderMap.isEmpty()) { return returnValue; }
     * 
     * log.error(" From map:{} the value of attributeDataType():{} is :{}",
     * encoderMap, attributeDataType, encoderMap.get(attributeDataType.getValue()));
     * String methodName = encoderMap.get(attributeDataType.getValue());
     * log.error(" key:{} methodName:{}", propertyName, methodName);
     * 
     * Class<?> clazz = DataUtil.getClass(dataTypeConverterClassName);
     * log.error(" dataTypeConverterClassName:{}, clazz.isPrimitive():{} ", clazz,
     * clazz.isPrimitive());
     * 
     * Object obj = null;
     * 
     * if (!clazz.isPrimitive()) { obj = clazz.cast(DataUtil.getInstance(clazz));
     * log.error(" obj:{} ", obj); // Getter getterMethod = getGetterMethod(clazz,
     * methodName); // log.error(" dataTypeConverterClass getterMethod:{} ",
     * getterMethod); // returnValue = method.invoke(obj, value); Method method =
     * clazz.getMethod(methodName); log.error(" dataTypeConverterClass method:{} ",
     * method);
     * 
     * returnValue = method.invoke(value);
     * log.error(" dataTypeConverterClass returnValue:{} ", returnValue); }
     * log.error(" key:{} value:{} -> returnValue:{}", propertyName, value,
     * returnValue); return returnValue;
     * 
     * }
     */
    private AttributeDataType getAttributeDetails(String attributeName) {
        log.error(" DataProcessingUtil:::getAttributeDetails() attributeNames:{}", attributeName);

        // get attribute details
        GluuAttribute gluuAttribute = attributeService.getAttributeByName(attributeName);
        log.error(
                "\n\n\n DataProcessingUtil:::getAttributeDetails() - attributeName:{}, gluuAttribute:{}, gluuAttribute.getDataType():{} ",
                attributeName, gluuAttribute, gluuAttribute.getDataType());
        return gluuAttribute.getDataType();

    }

    private <T> void getAttributeDataList(T obj, DataTypeConversionMapping dataTypeConversionMap,
            AttributesList attributesList, String propertyName) {
        log.error(
                " DataProcessingUtil:::getAttributeDataList() - obj:{}, dataTypeConversionMap:{}, attributesList:{}, propertyName:{}",
                obj, dataTypeConversionMap, attributesList, propertyName);

        List<AttributeData> attributes = persistenceEntryManager.getAttributeDataListFromCustomAttributesList(obj,
                attributesList, propertyName);
        log.error("\n DataProcessingUtil:::getAttributeDataList() -  attributes:{} ", attributes);

        for (AttributeData attributeData : attributes) {
            log.error("\n DataProcessingUtil:::getAttributeDataList() -  attributeData:{} ", attributeData);
            // Get Attribute Details
            encodeData2(obj, dataTypeConversionMap, propertyName);
        }

    }

}
