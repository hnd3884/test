package com.adventnet.iam.security;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZSecConstants
{
    public static final String ZSEC_REQUEST_PATH = "ZSEC_REQUEST_PATH";
    public static final String ZSEC_URL_UNIQUE_PATH = "ZSEC_URL_UNIQUE_PATH";
    public static final String ZSEC_PROXY_URL_UNIQUE_PATH = "ZSEC_PROXY_URL_UNIQUE_PATH";
    
    public enum TRUSTED_FEATURES
    {
        CORS(0), 
        IFRAME(1);
        
        public int value;
        
        private TRUSTED_FEATURES(final int value) {
            this.value = value;
        }
    }
    
    public enum DataType
    {
        Vcard("vcardArray"), 
        Property("properties"), 
        Csv("csv");
        
        public static Map<String, String> errorcodeMap;
        public String value;
        public static List<String> dataTypesList;
        
        private DataType(final String value) {
            this.value = value;
        }
        
        public static List<String> getDataTypeList() {
            if (DataType.dataTypesList == null) {
                DataType.dataTypesList = new ArrayList<String>();
                for (final DataType d : values()) {
                    DataType.dataTypesList.add(d.value);
                }
            }
            return DataType.dataTypesList;
        }
        
        static {
            (DataType.errorcodeMap = new HashMap<String, String>()).put("vcardArray", "VCARD/VCARDARRAY PARSE ERROR");
            DataType.errorcodeMap.put("properties", "PROPERTIES_PARSE_ERROR");
            DataType.errorcodeMap.put("csv", "CSV PARSE ERROR");
        }
    }
    
    public enum DESTINATION_TYPE
    {
        FOLDER, 
        ZIP, 
        NONE;
    }
}
