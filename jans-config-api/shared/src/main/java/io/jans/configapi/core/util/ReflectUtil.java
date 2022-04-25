package io.jans.configapi.core.util;

import io.jans.as.model.json.JsonApplier;
import io.jans.orm.exception.MappingException;
import io.jans.orm.reflect.property.Getter;
import io.jans.orm.reflect.property.Setter;
import io.jans.orm.reflect.util.ReflectHelper;
import io.jans.util.StringHelper;
import io.jans.util.security.StringEncrypter;
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

import org.slf4j.Logger;

@ApplicationScoped
public class ReflectUtil {

    @Inject
    Logger log;

    public <T> JSONObject getJSONObject(T obj) throws JSONException, StringEncrypter.EncryptionException {
        JSONObject responseJsonObject = new JSONObject();
        JsonApplier.getInstance().apply(obj, responseJsonObject);
        log.error("responseJsonObject:{} ", responseJsonObject);
        return responseJsonObject;
    }

    public <T> T fromJson(JSONObject requestObject, T obj) throws JSONException {
        JsonApplier.getInstance().apply(requestObject, obj);
        log.error("obj:{} ", obj);
        return (T) obj;
    }

    public Class<?> getPropertType(String className, String name) throws MappingException {
        log.error("className:{} , name:{} ", className, name);
        return ReflectHelper.reflectedPropertyClass(className, name);

    }

    public Getter getGetterMethod(Class<?> clazz, String name) throws MappingException {
        log.error("Get Getter fromclazz:{} , name:{} ", clazz, name);
        return ReflectHelper.getGetter(clazz, name);
    }

    public Setter getSetterMethod(Class<?> clazz, String name) throws MappingException {
        log.error("Get Setter from clazz:{} for name:{} ", clazz, name);
        return ReflectHelper.getSetter(clazz, name);
    }

    public Object getValue(Object object, String property) throws MappingException {
        log.error("Get value from object:{} for property:{} ", object, property);
        return ReflectHelper.getValue(object, property);
    }

    public Object invokeMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.error("Invoke clazz:{} on methodName:{} with name:{} ", clazz, methodName, parameterTypes);
        Method m = clazz.getDeclaredMethod(methodName, parameterTypes);
        Object obj = m.invoke(null, parameterTypes);
        log.error("methodName:{} returned obj:{} ", methodName, obj);
        return obj;
    }

    public boolean containsField(List<Field> allFields, String attribute) {
        log.error("allFields:{},  attribute:{}, allFields.contains(attribute):{} ", allFields, attribute,
                allFields.stream().anyMatch(f -> f.getName().equals(attribute)));

        return allFields.stream().anyMatch(f -> f.getName().equals(attribute));
    }

    public List<Field> getAllFields(Class<?> type) {
        List<Field> allFields = new ArrayList<>();
        allFields = getAllFields(allFields, type);
        log.error("Fields:{} of type:{}  ", allFields, type);

        return allFields;
    }

    public List<Field> getAllFields(List<Field> fields, Class<?> type) {
        log.error("Getting fields type:{} - fields:{} ", type, fields);
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        log.error("Final fields:{} of type:{} ", fields, type);
        return fields;
    }

    public Map<String, String> getFieldTypeMap(Class<?> clazz) {
        log.error("clazz:{} ", clazz);
        Map<String, String> propertyTypeMap = new HashMap<>();

        if (clazz == null) {
            return propertyTypeMap;
        }

        List<Field> fields = getAllFields(clazz);
        log.error("fields:{} ", fields);

        for (Field field : fields) {
            log.error("field:{} ", field);
            propertyTypeMap.put(field.getName(), field.getType().getSimpleName());
        }
        log.error("Final propertyTypeMap{} ", propertyTypeMap);
        return propertyTypeMap;
    }

    public Map<Field, Class> getPropertyTypeMap(Class<?> clazz) {
        log.error("clazz:{} for getting property and field map ", clazz);
        Map<Field, Class> propertyTypeMap = new HashMap<>();
        if (clazz == null) {
            return propertyTypeMap;
        }
        log.error("clazz.getCanonicalName():{}, clazz.getName():{}, clazz.getPackageName():{} ",
                clazz.getCanonicalName(), clazz.getName(), clazz.getPackageName());
        String className = clazz.getName();
        List<Field> fields = getAllFields(clazz);
        log.error("fields:{} ", fields);
        if (fields == null) {
            return propertyTypeMap;
        }
        for (Field field : fields) {
            log.error("field:{} ", field);
            Class dataTypeClass = getPropertType(className, field.getName());
            log.error("dataTypeClass:{} ", dataTypeClass);
            propertyTypeMap.put(field, dataTypeClass);
        }

        log.error("Final propertyTypeMap{} ", propertyTypeMap);
        return propertyTypeMap;
    }

    public <T> T encodeObjDataType(T obj, DataTypeConversionMapping dataTypeConversionMap) {
        log.error("Encode DataType for obj:{} using dataTypeConversionMap:{} ", obj, dataTypeConversionMap);
        if (obj == null || dataTypeConversionMap == null) {
            return (T) obj;
        }

        log.error("Getting propertMap for obj:{} ", obj.getClass());
        Map<String, String> propertyTypeMap = getFieldTypeMap(obj.getClass());
        log.error("obj:{} propertyTypeMap:{} ", obj.getClass(), propertyTypeMap);

        for (Map.Entry<String, String> entry : propertyTypeMap.entrySet()) {
            log.error(entry.getKey() + "/" + entry.getValue());

        }

        return obj;
    }

    private <T> T encodeData(T obj, Map.Entry<String, String> entryData, Map<String, String> encoderMap) {
        log.error("Encoding data for obj:{} , entryData:{}, encoderMap:{} ", obj, entryData, encoderMap);

        if (obj == null || entryData == null || encoderMap == null || encoderMap.isEmpty()) {
            return obj;
        }
        log.error("isKeyPresentInMap(entryData.getKey():{}, encoderMap:{}):{} ", entryData.getKey(), encoderMap,
                isKeyPresentInMap(entryData.getKey(), encoderMap));

        if (isKeyPresentInMap(entryData.getKey(), encoderMap)) {
            Getter getterMethod = getGetterMethod(obj.getClass(), entryData.getKey());
            log.error("getterMethod:{}, getValue(obj:{},entryData.getKey():{}) ->:{} ", getterMethod, obj,
                    entryData.getKey(), getValue(obj, entryData.getKey()));
            getValue(obj, entryData.getKey());
            
            getterMethod.getMethodName();
        }

        return obj;
    }
    
    private Object invokeGetterMethod(Object obj, String variableName) {
        return JsonApplier.getInstance().invokeReflectionGetter(obj, variableName);
    }

    private boolean isKeyPresentInMap(String key, Map<String, String> map) {
        log.error("Check key:{} is present in map:{}", key, map);
        if (StringHelper.isEmpty(key) || map == null || map.isEmpty()) {
            return false;
        }
        log.error(" key:{} present in map:{} ?:{}", key, map, map.keySet().contains(key));
        return map.keySet().contains(key);
    }
    
    private boolean isAttributeInExclusion(String baseDn, String attribute, Map<String, List<String>> exclusionMap) {
        log.error("Check if object:{} attribute:{} is in exclusionMap:{}", baseDn, attribute, exclusionMap);
        if (StringHelper.isEmpty(baseDn) || StringHelper.isEmpty(attribute)  || exclusionMap == null || exclusionMap.isEmpty()) {
            return false;
        }
        
        log.error("Map contains key exclusionMap.keySet().contains(baseDn)" , exclusionMap.keySet().contains(baseDn));
        
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
