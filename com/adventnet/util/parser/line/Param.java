package com.adventnet.util.parser.line;

class Param
{
    String paramName;
    int tokenNo;
    String pattern;
    String delimiter;
    String trimString;
    
    Param() {
        this.paramName = null;
        this.tokenNo = 0;
        this.pattern = null;
        this.delimiter = null;
        this.trimString = null;
    }
    
    void setTokenNo(final int tokenNo) {
        this.tokenNo = tokenNo;
    }
    
    int getTokenNo() {
        return this.tokenNo;
    }
    
    String getParamName() {
        return this.paramName;
    }
    
    void setParamName(final String paramName) {
        this.paramName = paramName;
    }
    
    void setPattern(final String pattern) {
        this.pattern = pattern;
    }
    
    String getPattern() {
        return this.pattern;
    }
    
    void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }
    
    String getDelimiter() {
        return this.delimiter;
    }
    
    void setTrimString(final String trimString) {
        this.trimString = trimString;
    }
    
    String getTrimString() {
        return this.trimString;
    }
}
