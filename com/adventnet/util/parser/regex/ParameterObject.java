package com.adventnet.util.parser.regex;

public class ParameterObject
{
    private int tokenNo;
    private String paramName;
    
    public String getParamName() {
        return this.paramName;
    }
    
    public void setParamName(final String paramName) {
        this.paramName = paramName;
    }
    
    public int getTokenNo() {
        return this.tokenNo;
    }
    
    public void setTokenNo(final int tokenNo) {
        this.tokenNo = tokenNo;
    }
}
