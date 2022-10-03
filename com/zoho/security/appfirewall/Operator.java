package com.zoho.security.appfirewall;

public enum Operator
{
    STRINGMATCHES("stringmatches"), 
    STRINGCONTAINS("stringcontains"), 
    STARTSWITHPREFIX("startswithprefix"), 
    ENDSWITHSUFFIX("endswithsuffix"), 
    REGEXFIND("regexfind"), 
    REGEXMATCHES("regexmatches"), 
    RANGEMATCHES("rangematches");
    
    private String operator;
    
    private Operator(final String operator) {
        this.operator = null;
        this.operator = operator;
    }
    
    public String getOperator() {
        return this.operator;
    }
}
