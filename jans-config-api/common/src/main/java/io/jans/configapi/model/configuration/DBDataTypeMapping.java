package io.jans.configapi.model.configuration;

import java.util.Map;

public class DBDataTypeMapping {

    private Map<String, DataTypeConversionMapping> dbDataTypeMap;

    public Map<String, DataTypeConversionMapping> getDbDataTypeMap() {
        return dbDataTypeMap;
    }

    public void setDbDataTypeMap(Map<String, DataTypeConversionMapping> dbDataTypeMap) {
        this.dbDataTypeMap = dbDataTypeMap;
    }

    @Override
    public String toString() {
        return "DBDataTypeMapping [dbDataTypeMap=" + dbDataTypeMap + "]";
    }

}