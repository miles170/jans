package io.jans.configapi.core.util;

import io.jans.as.model.json.JsonApplier;
import io.jans.orm.exception.MappingException;
import io.jans.orm.reflect.property.Getter;
import io.jans.orm.reflect.property.Setter;
import io.jans.orm.reflect.util.ReflectHelper;
import io.jans.util.StringHelper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Named("dataUtil")
public class DataUtil {

    private static final Logger logger = LoggerFactory.getLogger(DataUtil.class);

    public static Class<?> getPropertType(String className, String name) throws MappingException {
        logger.debug("className:{} , name:{} ", className, name);
        return ReflectHelper.reflectedPropertyClass(className, name);
    }

    public static Getter getGetterMethod(Class<?> clazz, String name) throws MappingException {
        logger.debug("Get Getter fromclazz:{} , name:{} ", clazz, name);
        return ReflectHelper.getGetter(clazz, name);
    }

    public static Setter getSetterMethod(Class<?> clazz, String name) throws MappingException {
        logger.debug("Get Setter from clazz:{} for name:{} ", clazz, name);
        return ReflectHelper.getSetter(clazz, name);
    }

    public static Object getValue(Object object, String property) throws MappingException {
        logger.debug("Get value from object:{} for property:{} ", object, property);
        return ReflectHelper.getValue(object, property);
    }

    public static Method getSetter(String fieldName, Class<?> clazz) throws IntrospectionException {
        PropertyDescriptor[] props = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
        for (PropertyDescriptor p : props)
            if (p.getName().equals(fieldName))
                return p.getWriteMethod();
        return null;
    }

    public static Object invokeMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        logger.debug("Invoke clazz:{} on methodName:{} with name:{} ", clazz, methodName, parameterTypes);
        Method m = clazz.getDeclaredMethod(methodName, parameterTypes);
        Object obj = m.invoke(null, parameterTypes);
        logger.debug("methodName:{} returned obj:{} ", methodName, obj);
        return obj;
    }

    public Object invokeReflectionGetter(Object obj, String variableName) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(variableName, obj.getClass());
            Method getter = pd.getReadMethod();
            if (getter != null) {
                return getter.invoke(obj);
            } else {
                logger.error("Getter Method not found for class:{} property:{}", obj.getClass().getName(),
                        variableName);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | IntrospectionException e) {
            logger.error(String.format("Getter Method ERROR for class: %s property: %s", obj.getClass().getName(),
                    variableName), e);
        }
        return null;
    }

    public static void invokeReflectionSetter(Object obj, String propertyName, Object variableValue) {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(propertyName, obj.getClass());
            Method method = pd.getWriteMethod();
            if (method != null) {
                method.invoke(obj, variableValue);
            } else {
                logger.error("Setter Method not found for class:{} property:{}", obj.getClass().getName(),
                        propertyName);
            }
        } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            logger.error("Setter Method invocation ERROR for class:{} property:{}", obj.getClass().getName(),
                    propertyName, e);
        }
    }

    public static boolean containsField(List<Field> allFields, String attribute) {
        logger.debug("allFields:{},  attribute:{}, allFields.contains(attribute):{} ", allFields, attribute,
                allFields.stream().anyMatch(f -> f.getName().equals(attribute)));

        return allFields.stream().anyMatch(f -> f.getName().equals(attribute));
    }

    public boolean isStringField(Map<String, String> objectPropertyMap, String attribute) {
        logger.debug("Check if field is string objectPropertyMap:{}, attribute:{} ", objectPropertyMap, attribute);
        if (objectPropertyMap == null || StringUtils.isBlank(attribute)) {
            return false;
        }
        logger.debug("attribute:{} , datatype:{}", attribute, objectPropertyMap.get(attribute));
        return ("java.lang.String".equalsIgnoreCase(objectPropertyMap.get(attribute)));
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> allFields = new ArrayList<>();
        getAllFields(allFields, type);
        logger.debug("Fields:{} of type:{}  ", allFields, type);
        return allFields;
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        logger.debug("Getting fields type:{} - fields:{} ", type, fields);
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        logger.debug("Final fields:{} of type:{} ", fields, type);
        return fields;
    }

    public static Map<String, String> getFieldTypeMap(Class<?> clazz) {
        logger.debug("clazz:{} ", clazz);
        Map<String, String> propertyTypeMap = new HashMap<>();

        if (clazz == null) {
            return propertyTypeMap;
        }

        List<Field> fields = getAllFields(clazz);
        logger.debug("AllFields:{} ", fields);

        for (Field field : fields) {
            logger.debug(
                    "field:{} , field.getAnnotatedType():{}, field.getAnnotations():{} , field.getType().getAnnotations():{}, field.getType().getCanonicalName():{} , field.getType().getClass():{} , field.getType().getClasses():{} , field.getType().getComponentType():{}",
                    field, field.getAnnotatedType(), field.getAnnotations(), field.getType().getAnnotations(),
                    field.getType().getCanonicalName(), field.getType().getClass(), field.getType().getClasses(),
                    field.getType().getComponentType());
            propertyTypeMap.put(field.getName(), field.getType().getSimpleName());
        }
        logger.debug("Final propertyTypeMap{} ", propertyTypeMap);
        return propertyTypeMap;
    }

    public static Object invokeGetterMethod(Object obj, String variableName) {
        return JsonApplier.getInstance().invokeReflectionGetter(obj, variableName);
    }

    public static boolean isKeyPresentInMap(String key, Map<String, String> map) {
        logger.debug("Check key:{} is present in map:{}", key, map);
        if (StringHelper.isEmpty(key) || map == null || map.isEmpty()) {
            return false;
        }
        logger.debug(" key:{} present in map:{} ?:{}", key, map, map.keySet().contains(key));
        return map.keySet().contains(key);
    }

    public static boolean isAttributeInExclusion(String className, String attribute,
            Map<String, List<String>> exclusionMap) {
        logger.debug("Check if object:{} attribute:{} is in exclusionMap:{}", className, attribute, exclusionMap);
        if (StringHelper.isEmpty(className) || StringHelper.isEmpty(attribute) || exclusionMap == null
                || exclusionMap.isEmpty()) {
            return false;
        }

        logger.debug("Map contains key exclusionMap.keySet().contains(className):{}",
                exclusionMap.keySet().contains(className));

        if (exclusionMap.keySet().contains(className)) {
            if (exclusionMap.get(className) != null) {
                return false;
            } else if (exclusionMap.get(className).contains(attribute)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T encodeObjDataType(T obj, DataTypeConversionMapping dataTypeConversionMap)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

        logger.error("Encode DataType for obj:{} using dataTypeConversionMap:{} ", obj, dataTypeConversionMap);

        if (obj == null || dataTypeConversionMap == null) {
            return obj;
        }

        logger.error("Getting propertMap for obj:{} ", obj.getClass());
        Map<String, String> objectPropertyMap = getFieldTypeMap(obj.getClass());
        logger.error("obj:{} objectPropertyMap:{} ", obj.getClass(), objectPropertyMap);

        for (Map.Entry<String, String> entry : objectPropertyMap.entrySet()) {
            logger.error("entry.getKey():{}, entry.getValue():{}", entry.getKey(), entry.getValue());

            // check if attribute is in exclusion map
            logger.error(
                    "obj.getClass().getName():{}, entry.getKey():{} dataTypeConversionMap.getExclusion():{}, isAttributeInExclusion:{}",
                    obj.getClass().getName(), entry.getKey(), dataTypeConversionMap.getExclusion(),
                    isAttributeInExclusion(obj.getClass().getName(), entry.getKey(),
                            dataTypeConversionMap.getExclusion()));
            if (isAttributeInExclusion(obj.getClass().getName(), entry.getKey(),
                    dataTypeConversionMap.getExclusion())) {
                logger.error("Breaking as the filed:{} is in exclusion list for obj:{}", obj.getClass().getName(),
                        entry.getKey());
                break;
            }

            // encode data
            encodeData(obj, entry, dataTypeConversionMap.getDataTypeConverterClassName(),
                    dataTypeConversionMap.getEncoder());
            logger.error("Final obj:{} ", obj);
        }

        return obj;
    }

    public static <T> T encodeData(T obj, Map.Entry<String, String> entryData, String dataTypeConverterClassName,
            Map<String, String> encoderMap) throws ClassNotFoundException, IllegalAccessException,
    InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        logger.error("Encoding data for obj:{} , entryData:{}, dataTypeConverterClassName:{}, encoderMap:{} ", obj,
                entryData, dataTypeConverterClassName, encoderMap);

        if (obj == null || entryData == null || dataTypeConverterClassName == null || encoderMap == null
                || encoderMap.isEmpty()) {
            return obj;
        }

     
        logger.error("isKeyPresentInMap(entryData.getValue():{}, encoderMap:{}):{} ", entryData.getValue(), encoderMap,
                isKeyPresentInMap(entryData.getValue(), encoderMap));

        if (isKeyPresentInMap(entryData.getValue(), encoderMap)) {
            Getter getterMethod = getGetterMethod(obj.getClass(), entryData.getKey());
            logger.error("getterMethod:{}, getValue(obj:{},entryData.getKey():{}) ->:{} ", getterMethod, obj,
                    entryData.getKey(), getValue(obj, entryData.getKey()));

            Object propertyValue = getterMethod.getMethod().invoke(obj);
            logger.error("from getterMethod() method -  key:{}, propertyValue:{} , getterMethod.getReturnType():{},",
                    entryData.getKey(), propertyValue, getterMethod.getReturnType());

            propertyValue = getValue(obj, entryData.getKey());
            logger.error("from getValue() method - key:{}, propertyValue:{} , getterMethod.getReturnType():{},",
                    entryData.getKey(), propertyValue, getterMethod.getReturnType());

            // Invoke encode method
            propertyValue = getEncodeMethod(dataTypeConverterClassName, entryData, encoderMap, propertyValue);
            logger.error("After encoding value key:{}, propertyValue:{}  ", entryData.getKey(), propertyValue);

            Setter setterMethod = getSetterMethod(obj.getClass(), entryData.getKey());
            propertyValue = setterMethod.getMethod().invoke(obj, propertyValue);
            logger.error("After setterMethod invoked key:{}, propertyValue:{} ", entryData.getKey(), propertyValue);

            propertyValue = getterMethod.get(obj);
            logger.error("Final - key:{}, propertyValue:{} ", entryData.getKey(), propertyValue);
        }

        return obj;
    }
    
    public static Object getEncodeMethod(String dataTypeConverterClassName, Map.Entry<String, String> entryData,
            Map<String, String> encoderMap, Object value) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        logger.error(
                "Invoke encode method from dataTypeConverterClassName:{} for entryData:{} based on encoderMap:{}, value:{}",
                dataTypeConverterClassName, entryData, encoderMap, value);

        Object returnValue = null;
        if (entryData == null || encoderMap == null || encoderMap.isEmpty()) {
            return returnValue;
        }

        logger.error(" From map:{} the value of entryData.getKey():{} is :{}", encoderMap, entryData.getKey(),
                encoderMap.get(entryData.getValue()));
        String methodName = encoderMap.get(entryData.getValue());
        logger.error(" key:{} methodName:{}", entryData.getKey(), methodName);

        Class<?> clazz = getClass(dataTypeConverterClassName);
        logger.error(" dataTypeConverterClassName:{}, clazz.isPrimitive():{} ", clazz, clazz.isPrimitive());

        Object obj = null;

        if (!clazz.isPrimitive()) {
            obj = clazz.cast(getInstance(clazz));
            logger.error(" obj:{} ", obj);
            // Getter getterMethod = getGetterMethod(clazz, methodName);
            // logger.error(" dataTypeConverterClass getterMethod:{} ", getterMethod);
            // returnValue = method.invoke(obj, value);
            Method method = clazz.getMethod(methodName);
            logger.error(" dataTypeConverterClass method:{} ", method);

            returnValue = method.invoke(value);
            logger.error(" dataTypeConverterClass returnValue:{} ", returnValue);
        }
        logger.error(" key:{} value:{} -> returnValue:{}", entryData.getKey(), value, returnValue);
        return returnValue;

    }
    
    public static Class getClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }
    
    public static <T> T getInstance(Class<T> type) throws IllegalAccessException, InstantiationException {
        Object o = type.newInstance();
        T t = type.cast(o);
        return t;
    }

}
