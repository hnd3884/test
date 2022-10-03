package com.adventnet.iam.security;

public class InfoFields
{
    public static final String REQ_HEADER = "REQ_HEADER";
    public static final String REQ_CONTENT_TYPE = "REQ_CONTENT_TYPE";
    public static final String RES_HEADER = "RES_HEADER";
    public static final String RES_CONTENT_TYPE = "RES_CONTENT_TYPE";
    public static final String URI = "URI";
    public static final String REMOTE_IP = "REMOTE_IP";
    public static final String METHOD = "METHOD";
    public static final String USER_AGENT = "USER_AGENT";
    public static final String REFERER = "REFERER";
    public static final String ACTIONRULE = "ACTIONRULE";
    public static final String INPUTSTREAM = "INPUTSTREAM";
    
    public enum ACCESSLOGFIELDS
    {
        PARAM("PARAM"), 
        FILE("FILE"), 
        PARAM_CNT("PARAM_CNT"), 
        EP_CNT("EP_CNT"), 
        HEADER_CNT("HEADER_CNT"), 
        QP_CNT("QP_CNT"), 
        COOKIE_CNT("COOKIE_CNT"), 
        FILE_CNT("FILE_CNT"), 
        DETECTOR("DETECTOR"), 
        RES_HEADER_CNT("RES_HEADER_CNT"), 
        REQ_HEADER_CNT("REQ_HEADER_CNT"), 
        RES_HEADER_SIZE_IN_BYTES("RES_HEADER_SIZE_IN_BYTES"), 
        INDIVIDUAL_RES_HEADER_SIZE_IN_BYTES("INDIVIDUAL_RES_HEADER_SIZE_IN_BYTES"), 
        ERROR_CODE("ERROR_CODE");
        
        private String fieldName;
        private String logfieldName;
        
        private ACCESSLOGFIELDS(final String field) {
            this.fieldName = field;
            this.logfieldName = "_C_ZSEC_" + field;
        }
        
        public String getValue() {
            return this.fieldName;
        }
        
        public String getLogFieldValue() {
            return this.logfieldName;
        }
    }
}
