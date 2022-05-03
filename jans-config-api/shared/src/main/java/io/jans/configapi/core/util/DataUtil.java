package io.jans.configapi.core.util;

import io.jans.as.model.json.JsonApplier;
import io.jans.configapi.core.util.Jackson;
import io.jans.orm.exception.MappingException;
import io.jans.orm.reflect.property.Getter;
import io.jans.orm.reflect.property.Setter;
import io.jans.orm.reflect.util.ReflectHelper;
import io.jans.util.StringHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    private DataUtil() {
    }

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

    public static Setter getSetterMethod(Class<?> clazz, String name) throws MappingException {
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

    public static <T> T encodeObjDataType(T obj, DataTypeConversionMapping dataTypeConversionMap)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, IllegalArgumentException,
            InvocationTargetException {
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

                return obj;
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
            InstantiationException, IllegalArgumentException, InvocationTargetException {
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

            Object propertyValue = getterMethod.get(obj);
            logger.error("key:{}, propertyValue:{} , getterMethod.getReturnType():{},", entryData.getKey(),
                    propertyValue, getterMethod.getReturnType());

            // Invoke encode method
            propertyValue = getEncodeMethod(entryData.getKey(), dataTypeConverterClassName, encoderMap, propertyValue);
            logger.error("key:{}, propertyValue:{} ", entryData.getKey(), propertyValue);

            Setter setterMethod = getSetterMethod(obj.getClass(), entryData.getKey());
            propertyValue = setterMethod.getMethod().invoke(obj, propertyValue);
            logger.error("After setterMethod invoked key:{}, propertyValue:{} ", entryData.getKey(), propertyValue);

            propertyValue = getterMethod.get(obj);
            logger.error("Final - key:{}, propertyValue:{} ", entryData.getKey(), propertyValue);
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

    public static Object getEncodeMethod(String key, String dataTypeConverterClassName, Map<String, String> encoderMap,
            Object value) throws ClassNotFoundException, IllegalAccessException, InstantiationException,
            IllegalArgumentException, InvocationTargetException {
        logger.error(
                "Invoke encode method from dataTypeConverterClassName:{} for key:{} based on encoderMap:{}, value:{}",
                dataTypeConverterClassName, key, encoderMap, value);
        Object returnValue = null;
        if (StringHelper.isEmpty(key) || encoderMap == null || encoderMap.isEmpty()) {
            return returnValue;
        }
        logger.error(" From map:{} the value of key:{} is :{}", encoderMap, key, encoderMap.get(key));
        String methodName = encoderMap.get(key);
        logger.error(" key:{} methodName:{}", key, methodName);

        Class<?> clazz = getClass(dataTypeConverterClassName);
        Object obj = null;
        if (!clazz.isPrimitive()) {
            obj = clazz.cast(getInstance(clazz));
            Getter getterMethod = getGetterMethod(clazz, methodName);
            returnValue = getterMethod.getMethod().invoke(obj, value);
        }
        logger.error(" key:{} value:{} -> returnValue:{}", key, value, returnValue);
        return returnValue;

    }

    public static boolean isAttributeInExclusion(String className, String attribute,
            Map<String, List<String>> exclusionMap) {
        logger.error("Check if object:{} attribute:{} is in exclusionMap:{}", className, attribute, exclusionMap);
        if (StringHelper.isEmpty(className) || StringHelper.isEmpty(attribute) || exclusionMap == null
                || exclusionMap.isEmpty()) {
            return false;
        }

        logger.error("Map contains key exclusionMap.keySet().contains(className):{}",
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

    /*
     * private static Map<String, String> computeGettersMap(Class<?> clazz) {
     * logger.error("clazz:{} ", clazz); Map<String, String> propertyTypeMap = new
     * HashMap<>();
     * 
     * List<Field> fields = getAllFields(clazz); logger.error("fields:{} ", fields);
     * 
     * Class baseclazz = clazz; logger.error("baseclazz:{} ", baseclazz); for (Field
     * field : fields) { logger.error("field:{} ", field);
     * 
     * Class fieldClass = field.getClass(); logger.error("fieldClass:{} ",
     * fieldClass);
     * 
     * if (isCollection(fieldClass)) { //Use class of parameter in collection Field
     * f=findField(clazz, prop); Attribute
     * attrAnnot=f.getAnnotation(Attribute.class); if (attrAnnot!=null)
     * baseclazz=attrAnnot.multiValueClass(); } else
     * baseclazz=method.getReturnType(); } propertyTypeMap.put(attrName, list);
     * 
     * return propertyTypeMap;
     * 
     * }
     */

    private static Map<String, String> computeGettersMap(List<String> attrNames, Class clazz) throws Exception {

        logger.error("clazz:{} ", clazz);
        Map<String, String> propertyTypeMap = new HashMap<>();

        List<Field> fields = getAllFields(clazz);
        logger.error("fields:{} ", fields);
        for (Field field : fields) {
            logger.error("field:{} ", field);

            Type type = field.getGenericType();
            logger.error("type: " + type);

            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                logger.error("raw type: " + pt.getRawType());
                logger.error("owner type: " + pt.getOwnerType());
                logger.error("actual type args:");
                for (Type t : pt.getActualTypeArguments()) {
                    logger.error("    " + t);
                    propertyTypeMap.put(field.getName(), t.getClass().getName());
                }
            } else {
                propertyTypeMap.put(field.getName(), field.getType().getName());
            }
        }

        return propertyTypeMap;

    }

    public static Class getClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    public static Object getObjectInstance(String name)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> cls = Class.forName(name);
        Object clsInstance = (Object) cls.newInstance();
        return clsInstance;
    }

    public static <T> T getInstance(Class<T> type) throws IllegalAccessException, InstantiationException {
        Object o = type.newInstance();
        T t = type.cast(o);
        return t;
    }

    public static <T> T read(InputStream inputStream, T obj) throws IOException {
        return Jackson.read(inputStream, obj);
    }

    public static <T> boolean isJDKClass(T t) {
        return t.getClass().getPackage().getName().startsWith("java");
    }

    public static boolean isCollection(Class clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    private static Field findField(final Class<?> cls, final String fieldName) {

        Class<?> currentClass = cls;

        while (currentClass != null) {
            Field fields[] = currentClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(fieldName))
                    return field;
            }
            currentClass = currentClass.getSuperclass();
        }
        return null;

    }

}
