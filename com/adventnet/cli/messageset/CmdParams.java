package com.adventnet.cli.messageset;

import java.io.Serializable;

public class CmdParams implements Serializable
{
    public static final byte INTEGER = 1;
    public static final byte CHAR = 2;
    public static final byte FLOAT = 3;
    public static final byte STRING = 4;
    private String paramName;
    private byte paramValueType;
    boolean sendParam;
    boolean optionFlag;
    private String paramValue;
    private String description;
    
    public CmdParams() {
        this.paramName = null;
        this.paramValueType = 0;
        this.sendParam = true;
        this.optionFlag = true;
        this.paramValue = null;
        this.description = null;
    }
    
    public void setParamName(final String paramName) {
        this.paramName = paramName;
    }
    
    public String getParamName() {
        return this.paramName;
    }
    
    public void setParamValueType(final byte paramValueType) {
        this.paramValueType = paramValueType;
    }
    
    public byte getParamValueType() {
        return this.paramValueType;
    }
    
    public void setParamValue(final String paramValue) {
        this.paramValue = paramValue;
    }
    
    public String getParamValue() {
        return this.paramValue;
    }
    
    public void setSendParam(final boolean sendParam) {
        this.sendParam = sendParam;
    }
    
    public boolean isSendParam() {
        return this.sendParam;
    }
    
    public void setOptionalFlag(final boolean optionFlag) {
        this.optionFlag = optionFlag;
    }
    
    public boolean isOptional() {
        return this.optionFlag;
    }
    
    public String getParamDescription() {
        return this.description;
    }
    
    public void setParamDescription(final String description) {
        this.description = description;
    }
    
    public String toString() {
        return this.getParamName();
    }
}
