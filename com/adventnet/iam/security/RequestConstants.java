package com.adventnet.iam.security;

public class RequestConstants
{
    public enum PARAM_NAME
    {
        ISC_SIGNATURE_PARAM_NAME("iscsignature");
        
        private String value;
        
        private PARAM_NAME(final String value) {
            this.value = null;
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    public enum HEADER_NAME
    {
        AUTHORIZATION("Authorization"), 
        SYSTEM_AUTHORIZATION("ZS-SystemAuthorization");
        
        private String value;
        
        private HEADER_NAME(final String value) {
            this.value = null;
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
}
