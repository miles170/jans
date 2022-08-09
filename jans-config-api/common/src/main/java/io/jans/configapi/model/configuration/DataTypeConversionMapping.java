package io.jans.configapi.model.configuration;

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

    @Override
    public String toString() {
        return "DataTypeConversionMapping [dateFormat=" + dateFormat + ", encoder=" + encoder + ", decoder=" + decoder
                + "]";
    }

}