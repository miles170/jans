package io.jans.configapi.util;

import io.jans.as.model.json.JsonApplier;
import io.jans.configapi.configuration.ConfigurationFactory;
import io.jans.configapi.core.util.DataUtil;
import io.jans.configapi.core.util.Jackson;
import io.jans.configapi.core.util.DataTypeConversionMapping;
import io.jans.orm.exception.MappingException;
import io.jans.orm.reflect.property.Getter;
import io.jans.orm.reflect.property.Setter;
import io.jans.orm.reflect.util.ReflectHelper;
import io.jans.util.StringHelper;
import io.jans.util.security.StringEncrypter;
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
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;

@ApplicationScoped
public class DataProcessingUtil {

    @Inject
    Logger log;
	
    @Inject
    ConfigurationFactory configurationFactory;
    
   
    public DataTypeConversionMapping getDataTypeConversionMapping() {
        return this.configurationFactory.getApiAppConfiguration().getDataTypeConversionMap();
    }

   
    public <T> T encodeObjDataType(T obj) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IllegalArgumentException,
    InvocationTargetException {
        log.error("Encode DataType for obj:{} using dataTypeConversionMap:{} ", obj,  getDataTypeConversionMapping());
        if (obj == null) {
            return (T) obj;
        }

        obj = DataUtil.encodeObjDataType(obj, getDataTypeConversionMapping());
        
        log.error("Data after encoding - obj:{}", obj);

        return obj;
    }

    public static <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
        return DataUtil.convertInstanceOfObject(o, clazz);
    }

    public Object getObjectInstance(String name) throws  ClassNotFoundException, IllegalAccessException, InstantiationException {
        return DataUtil.getObjectInstance(name);
    }
    
    public <T> T getInstance(Class<T> type) throws IllegalAccessException, InstantiationException {
        return DataUtil.getInstance(type);
    }
    
    public <T> T read(InputStream inputStream, T obj) throws IOException {
        return DataUtil.read(inputStream, obj);        
    }
    
    public <T> String getJsonString(T obj) throws IOException {
        return DataUtil.getJsonString(obj);
    }
    


}
