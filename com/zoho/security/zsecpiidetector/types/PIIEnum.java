package com.zoho.security.zsecpiidetector.types;

import java.util.Arrays;
import java.util.List;

public class PIIEnum
{
    public enum Category
    {
        IDENTITY("Identity"), 
        PERSONAL("Personal"), 
        ACCOUNT("Account"), 
        INTERNET_AND_TELECOMMUNICATION("Internet_And_Telecommunication"), 
        AUTHENTICATION_AND_AUTHORIZATION("Authentication_And_Authorization"), 
        ZOHOPII("ZOHOPII"), 
        FINANCIAL("Financial"), 
        PLACE("Place"), 
        EDUCATION_AND_WORK("Education_And_Work"), 
        SOCIAL("Social"), 
        MEDICAL("Medical"), 
        DEVICE_INFORMATION("Device_Information"), 
        OTHERS("Others");
        
        private String constant;
        
        private Category(final String s) {
            this.constant = s;
        }
        
        public String value() {
            return this.constant;
        }
        
        public static List<Category> getAllCategory() {
            return Arrays.asList(values());
        }
    }
    
    public enum DetectionType
    {
        REGEX, 
        REGEX_AND_DICTIONARY, 
        DICTIONARY, 
        MACHINE_LEARNING, 
        COMMON;
        
        public static List<DetectionType> getAllDetectionTypes() {
            return Arrays.asList(DetectionType.REGEX, DetectionType.REGEX_AND_DICTIONARY, DetectionType.DICTIONARY, DetectionType.MACHINE_LEARNING, DetectionType.COMMON);
        }
    }
    
    public enum Sensitivity
    {
        HIGH, 
        MEDIUM, 
        LOW;
        
        public static List<Sensitivity> getAllSensitivities() {
            return Arrays.asList(Sensitivity.HIGH, Sensitivity.MEDIUM, Sensitivity.LOW);
        }
    }
    
    public enum JsonKeys
    {
        PII_DATA("piiData"), 
        CONFIDENCE_LEVEL("confidenceLevel"), 
        START_INDEX("startIndex"), 
        END_INDEX("endIndex"), 
        PII_CATEGORY("piiCategory"), 
        SENSITIVITY_LEVEL("sensitivityLevel"), 
        PII_TYPE("piiType"), 
        ML_KEY("ml"), 
        REGEX_KEY("regex"), 
        ML_BASED_MASKED_DATA("mlBasedMaskedData"), 
        REGEX_BASED_MASKED_DATA("regexBasedMaskedData"), 
        DETECTION_TYPE("DetectionType");
        
        String key;
        
        private JsonKeys(final String key) {
            this.key = key;
        }
        
        public String value() {
            return this.key;
        }
    }
}
