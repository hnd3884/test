package com.adventnet.persistence.fos;

import java.util.HashMap;
import java.util.Map;

public enum FOSErrorCode
{
    ERROR_INCORRECT_POLL_URL(10), 
    ERROR_PING_FAILURE_DURING_REPLICATION(12), 
    ERROR_IP_CLASH(1001), 
    ERROR_IP_BINDING(1002), 
    ERROR_IP_UNBINDING(1003), 
    ERROR_IF_DOWN(1004), 
    ERROR_FILE_REPLICATION(1005), 
    ERROR_IN_STOPPING_REPLICATION_SCHEDULE(1006), 
    ERROR_GETTING_HEALTHSTATUS_FROM_DB(1007), 
    ERROR_IN_COMPLETE_PENDING_REPLICATION(1008), 
    ERROR_MISC(1009);
    
    private static final Map LOOKUP;
    public final int intValue;
    
    private FOSErrorCode(final int intValue) {
        this.intValue = intValue;
    }
    
    public static String getErrorMsg(final int value) {
        if (FOSErrorCode.LOOKUP.containsKey(value)) {
            return FOSErrorCode.LOOKUP.get(value).toString();
        }
        return null;
    }
    
    static {
        LOOKUP = new HashMap();
        for (final FOSErrorCode code : values()) {
            FOSErrorCode.LOOKUP.put(code.intValue, code);
        }
    }
}
