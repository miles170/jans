package io.jans.configapi.core.util;

import java.util.Map;

public class DataTypeConversionMapping {

    /**
     * Accepatable dateFormat
     */
    private String dateFormat;
    
    /**
     * Map of DataType and method to invoke for encoding
     */
    private Map<String, String> encoder;

    /**
     * Map of DataType and method to invoke for decoding
     */
    private Map<String, String> decoder;
    
    /**
     * Name of the class to be used for DataType conversion
     */
    private String dataTypeConverterClassName;
    
    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Map<String, String> getEncoder() {
        return encoder;
    }

    public void setEncoder(Map<String, String> encoder) {
        this.encoder = encoder;
    }

    public Map<String, String> getDecoder() {
        return decoder;
    }

    public void setDecoder(Map<String, String> decoder) {
        this.decoder = decoder;
    }

    public String getDataTypeConverterClassName() {
        return dataTypeConverterClassName;
    }

    public void setDataTypeConverterClassName(String dataTypeConverterClassName) {
        this.dataTypeConverterClassName = dataTypeConverterClassName;
    }

    @Override
    public String toString() {
        return "DataTypeConversionMapping [dateFormat=" + dateFormat + ", encoder=" + encoder + ", decoder=" + decoder
                + ", dataTypeConverterClassName=" + dataTypeConverterClassName + "]";
    }

}