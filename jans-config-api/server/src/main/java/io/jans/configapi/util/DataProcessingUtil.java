package io.jans.configapi.util;

import io.jans.as.model.json.JsonApplier;
import io.jans.configapi.configuration.ConfigurationFactory;
import io.jans.configapi.core.util.DataUtil;
import io.jans.configapi.core.util.DataTypeConversionMapping;
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
public class DataProcessingUtil {

    @Inject
    Logger log;
	
    @Inject
    ConfigurationFactory configurationFactory;
    
    
    public DataTypeConversionMapping getDataTypeConversionMapping() {
        return this.configurationFactory.getApiAppConfiguration().getDataTypeConversionMap();
    }

   
    public <T> T encodeObjDataType(T obj) {
        log.error("Encode DataType for obj:{} using dataTypeConversionMap:{} ", obj, getDataTypeConversionMapping());
        if (obj == null) {
            return (T) obj;
        }

        obj = DataUtil.encodeObjDataType(obj, getDataTypeConversionMapping());
        
        log.error("Data after encoding - obj:{}", obj);

        return obj;
    }

    


}
