package io.jans.configapi.core.util;

import io.jans.orm.PersistenceEntryManager;
import io.jans.util.StringHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.slf4j.Logger;

@ApplicationScoped
@Priority(1)
@Named("dataTypeConverter")
public class DataTypeConverter {

    @Inject
    Logger log;

    @Inject
    PersistenceEntryManager persistenceEntryManager;


    public Date validateDateFormat(String strDate, String format) throws ParseException {
        log.error("Validate date value - strDate:{}, format:{} ", strDate, format);
        Date formatedDate = null;
        if (StringHelper.isEmpty(strDate) || StringHelper.isEmpty(format)) {
            return formatedDate;
        }

        SimpleDateFormat parser = new SimpleDateFormat(format);
        return parser.parse(strDate);
    }

    public Date decodeTime(String baseDn, String strDate) {
        log.error("Decode date value - baseDn:{}, strDate:{} ", baseDn, strDate);
        return persistenceEntryManager.decodeTime(baseDn, strDate);
    }
    
    public String encodeTime(String baseDn, Date date) {
        log.error("Encode Date value - baseDn:{}, date:{} ", baseDn, date);
        return persistenceEntryManager.encodeTime(baseDn, date);
    }

    public Boolean decodeBoolean(String strBoolean, boolean defaultValue) {
        log.error("Decode boolean value - strBoolean:{} ", strBoolean);
        return StringHelper.toBoolean(strBoolean, defaultValue);
    }

    public String encodeBoolean(boolean booleanValue) {
        log.error("Encode boolean value - booleanValue:{} ", booleanValue);
        return Boolean.toString(booleanValue);
    }

}
