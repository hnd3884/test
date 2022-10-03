package com.adventnet.iam.security.antivirus;

import org.json.JSONObject;
import java.io.Serializable;

public class AVScanFailureInfo implements Serializable
{
    private static final long serialVersionUID = -6706185977262189611L;
    private final String message;
    private final FailedCause cause;
    
    protected AVScanFailureInfo(final FailedCause cause, final String message) {
        this.cause = cause;
        this.message = message;
    }
    
    public FailedCause cause() {
        return this.cause;
    }
    
    public String message() {
        return this.message;
    }
    
    @Override
    public String toString() {
        return "FailedCause: " + this.cause() + "\tFailedMessage: " + this.message();
    }
    
    public JSONObject toJson() {
        return new JSONObject().put(JsonKeys.CAUSE.name(), (Object)this.cause()).put(JsonKeys.MESSAGE.name(), (Object)this.message());
    }
    
    public enum FailedCause
    {
        AV_CONNECTION_FAILED, 
        AV_FILE_SIZE_LIMIT_EXCEED, 
        AV_SCAN_TIME_OUT, 
        AV_OTHER_FAILURE_CASES;
    }
    
    public enum JsonKeys
    {
        CAUSE, 
        MESSAGE;
    }
}
