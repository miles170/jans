package io.jans.configapi.core.util;

import io.jans.as.model.json.JsonApplier;
import io.jans.orm.exception.MappingException;
import io.jans.orm.reflect.property.Getter;
import io.jans.orm.reflect.property.Setter;
import io.jans.orm.reflect.util.ReflectHelper;
import io.jans.util.StringHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DataUtil {
  
    private DataUtil() { }
    
    private static final Logger logger = LoggerFactory.getLogger(DataUtil.class);
    
    public static <T> JSONObject getJSONObject(T obj) throws JSONException {
        JSONObject responseJsonObject = new JSONObject();
        JsonApplier.getInstance().apply(obj, responseJsonObject);
        logger.error("responseJsonObject:{} ", responseJsonObject);
        return responseJsonObject;
    }

    public static <T> T fromJson(JSONObject requestObject, T obj) throws JSONException {
        JsonApplier.getInstance().apply(requestObject, obj);
        logger.error("obj:{} ", obj);
        return obj;
    }

    public static Class<?> getPropertType(String className, String name) throws MappingException {
        logger.error("className:{} , name:{} ", className, name);
        return ReflectHelper.reflectedPropertyClass(className, name);

    }

    public static Getter getGetterMethod(Class<?> clazz, String name) throws MappingException {
        logger.error("Get Getter fromclazz:{} , name:{} ", clazz, name);
        return ReflectHelper.getGetter(clazz, name);
    }

    public Setter getSetterMethod(Class<?> clazz, String name) throws MappingException {
        logger.error("Get Setter from clazz:{} for name:{} ", clazz, name);
        return ReflectHelper.getSetter(clazz, name);
    }

    public static Object getValue(Object object, String property) throws MappingException {
        logger.error("Get value from object:{} for property:{} ", object, property);
        return ReflectHelper.getValue(object, property);
    }

    public static Object invokeMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        logger.error("Invoke clazz:{} on methodName:{} with name:{} ", clazz, methodName, parameterTypes);
        Method m = clazz.getDeclaredMethod(methodName, parameterTypes);
        Object obj = m.invoke(null, parameterTypes);
        logger.error("methodName:{} returned obj:{} ", methodName, obj);
        return obj;
    }

    public static boolean containsField(List<Field> allFields, String attribute) {
        logger.error("allFields:{},  attribute:{}, allFields.contains(attribute):{} ", allFields, attribute,
                allFields.stream().anyMatch(f -> f.getName().equals(attribute)));

        return allFields.stream().anyMatch(f -> f.getName().equals(attribute));
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> allFields = new ArrayList<>();
        allFields = getAllFields(allFields, type);
        logger.error("Fields:{} of type:{}  ", allFields, type);

        return allFields;
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        logger.error("Getting fields type:{} - fields:{} ", type, fields);
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        logger.error("Final fields:{} of type:{} ", fields, type);
        return fields;
    }

    public static Map<String, String> getFieldTypeMap(Class<?> clazz) {
        logger.error("clazz:{} ", clazz);
        Map<String, String> propertyTypeMap = new HashMap<>();

        if (clazz == null) {
            return propertyTypeMap;
        }

        List<Field> fields = getAllFields(clazz);
        logger.error("fields:{} ", fields);

        for (Field field : fields) {
            logger.error("field:{} ", field);
            propertyTypeMap.put(field.getName(), field.getType().getSimpleName());
        }
        logger.error("Final propertyTypeMap{} ", propertyTypeMap);
        return propertyTypeMap;
    }

    public static Map<Field, Class> getPropertyTypeMap(Class<?> clazz) {
        logger.error("clazz:{} for getting property and field map ", clazz);
        Map<Field, Class> propertyTypeMap = new HashMap<>();
        if (clazz == null) {
            return propertyTypeMap;
        }
        logger.error("clazz.getCanonicalName():{}, clazz.getName():{}, clazz.getPackageName():{} ",
                clazz.getCanonicalName(), clazz.getName(), clazz.getPackageName());
        String className = clazz.getName();
        List<Field> fields = getAllFields(clazz);
        logger.error("fields:{} ", fields);
        if (fields == null) {
            return propertyTypeMap;
        }
        for (Field field : fields) {
            logger.error("field:{} ", field);
            Class dataTypeClass = getPropertType(className, field.getName());
            logger.error("dataTypeClass:{} ", dataTypeClass);
            propertyTypeMap.put(field, dataTypeClass);
        }

        logger.error("Final propertyTypeMap{} ", propertyTypeMap);
        return propertyTypeMap;
    }

    public static <T> T encodeObjDataType(T obj, DataTypeConversionMapping dataTypeConversionMap) {
        logger.error("Encode DataType for obj:{} using dataTypeConversionMap:{} ", obj, dataTypeConversionMap);
        if (obj == null || dataTypeConversionMap == null) {
            return obj;
        }

        logger.error("Getting propertMap for obj:{} ", obj.getClass());
        Map<String, String> objectPropertyMap = getFieldTypeMap(obj.getClass());
        logger.error("obj:{} objectPropertyMap:{} ", obj.getClass(), objectPropertyMap);

        for (Map.Entry<String, String> entry : objectPropertyMap.entrySet()) {
            logger.error("entry.getKey():{}, entry.getValue():{}", entry.getKey(), entry.getValue());
            
            //encode data
            encodeData(obj, entry, dataTypeConversionMap.getEncoder());
        }

        return obj;
    }

    public static <T> T encodeData(T obj, Map.Entry<String, String> entryData, Map<String, String> encoderMap) {
        logger.error("Encoding data for obj:{} , entryData:{}, encoderMap:{} ", obj, entryData, encoderMap);

        if (obj == null || entryData == null || encoderMap == null || encoderMap.isEmpty()) {
            return obj;
        }
        logger.error("isKeyPresentInMap(entryData.getKey():{}, encoderMap:{}):{} ", entryData.getKey(), encoderMap,
                isKeyPresentInMap(entryData.getKey(), encoderMap));

        if (isKeyPresentInMap(entryData.getKey(), encoderMap)) {
            Getter getterMethod = getGetterMethod(obj.getClass(), entryData.getKey());
            logger.error("getterMethod:{}, getValue(obj:{},entryData.getKey():{}) ->:{} ", getterMethod, obj,
                    entryData.getKey(), getValue(obj, entryData.getKey()));
            
            
            Object propertyValue = getterMethod.get(obj);
            logger.error("key:{}, propertyValue:{} , getterMethod.getReturnType():{},", entryData.getKey(), propertyValue, getterMethod.getReturnType());
            
            
            
        }

        return obj;
    }
    
    public static Object invokeGetterMethod(Object obj, String variableName) {
        return JsonApplier.getInstance().invokeReflectionGetter(obj, variableName);
    }

    public static boolean isKeyPresentInMap(String key, Map<String, String> map) {
        logger.error("Check key:{} is present in map:{}", key, map);
        if (StringHelper.isEmpty(key) || map == null || map.isEmpty()) {
            return false;
        }
        logger.error(" key:{} present in map:{} ?:{}", key, map, map.keySet().contains(key));
        return map.keySet().contains(key);
    }
    
    public static boolean isAttributeInExclusion(String baseDn, String attribute, Map<String, List<String>> exclusionMap) {
        logger.error("Check if object:{} attribute:{} is in exclusionMap:{}", baseDn, attribute, exclusionMap);
        if (StringHelper.isEmpty(baseDn) || StringHelper.isEmpty(attribute)  || exclusionMap == null || exclusionMap.isEmpty()) {
            return false;
        }
        
        logger.error("Map contains key exclusionMap.keySet().contains(baseDn):{}" , exclusionMap.keySet().contains(baseDn));
        
        if(exclusionMap.keySet().contains(baseDn)) {
            if(exclusionMap.get(baseDn)!=null) {
                return false;
            }else if(exclusionMap.get(baseDn).contains(attribute)) {
                return true;
            }
        }
        return false;
    }


}
