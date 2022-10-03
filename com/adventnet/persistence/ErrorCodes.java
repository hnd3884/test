package com.adventnet.persistence;

import java.util.HashMap;
import java.util.Map;

public class ErrorCodes
{
    public static final int UNDEFINED_ERROR_CODE = -9999;
    static Map<String, Map<Object, AdventNetErrorCode>> errorCodesMap;
    static Map<String, AdventNetErrorCode> advErrStr_Vs_ErrCode;
    
    public static Map<Object, AdventNetErrorCode> getErrorCodeMap(final String errorCodeTableName) {
        return ErrorCodes.errorCodesMap.get(errorCodeTableName);
    }
    
    public static AdventNetErrorCode getAdventNetErrorCode(final String adventNetErrorString) {
        return ErrorCodes.advErrStr_Vs_ErrCode.get(adventNetErrorString);
    }
    
    static {
        ErrorCodes.errorCodesMap = new HashMap<String, Map<Object, AdventNetErrorCode>>();
        ErrorCodes.advErrStr_Vs_ErrCode = new HashMap<String, AdventNetErrorCode>();
    }
    
    public static class AdventNetErrorCode
    {
        int errorCode;
        String errorMessage;
        String errorString;
        
        public int getErrorCode() {
            return this.errorCode;
        }
        
        public String getErrorMessage() {
            return this.errorMessage;
        }
        
        public String getErrorString() {
            return this.errorString;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.errorCode) + " - " + this.errorString;
        }
    }
}
