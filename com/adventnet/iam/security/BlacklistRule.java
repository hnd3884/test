package com.adventnet.iam.security;

import java.util.regex.Pattern;

public class BlacklistRule
{
    private String value;
    private Pattern valuePattern;
    private Operator operator;
    
    public BlacklistRule(final String value, final String operator) {
        this.operator = Operator.STRINGEQUALS;
        this.value = value;
        if (SecurityUtil.isValid(operator)) {
            this.operator = Operator.valueOf(operator.toUpperCase());
            if (this.operator == Operator.REGEXFIND || this.operator == Operator.REGEXMATCHES) {
                this.valuePattern = Pattern.compile(value);
            }
        }
    }
    
    public String getValue() {
        return this.value;
    }
    
    public Pattern getValuePattern() {
        return this.valuePattern;
    }
    
    public Operator getOperator() {
        return this.operator;
    }
    
    enum Operator
    {
        STRINGEQUALS("stringequals"), 
        STRINGCONTAINS("stringcontains"), 
        REGEXMATCHES("regexmatches"), 
        REGEXFIND("regexfind"), 
        STARTSWITH("startswith"), 
        ENDSWITH("endswith");
        
        private String operator;
        
        private Operator(final String operator) {
            this.operator = null;
            this.operator = operator;
        }
        
        public String getOperator() {
            return this.operator;
        }
    }
}
