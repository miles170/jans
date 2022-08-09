package io.jans.configapi.util;


import io.jans.configapi.configuration.ConfigurationFactory;
import io.jans.orm.PersistenceEntryManager;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.jans.orm.PersistenceEntryManager;

@ApplicationScoped
public class DataProcessingUtil {

    @Inject
    Logger log;
	

    @Inject
    PersistenceEntryManager persistenceEntryManager;

    
   
    public <T> T encodeObjDataType(T obj) throws ClassNotFoundException, IllegalAccessException,
    InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        log.error("Encode DataType for obj:{} with  name:{}", obj, obj.getClass().getName());
        if (obj == null) {
            return (T) obj;
        }

        //obj = DataUtil.encodeObjDataType(obj, getDataTypeConversionMapping());
        
        log.error("Data after encoding - obj:{}", obj);

        return obj;
    }

     

}
