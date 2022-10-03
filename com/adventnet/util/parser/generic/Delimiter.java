package com.adventnet.util.parser.generic;

class Delimiter
{
    String delimiterValue;
    String[] delimiterValueList;
    String description;
    String[] trimList;
    Delimiter[] childList;
    Delimiter parent;
    Match match;
    
    Delimiter() {
        this.delimiterValue = null;
        this.delimiterValueList = null;
        this.description = null;
        this.trimList = null;
        this.childList = null;
        this.parent = null;
        this.match = null;
    }
    
    void setDelimiterValue(final String delimiterValue) {
        this.delimiterValue = delimiterValue;
    }
    
    String getDelimiterValue() {
        return this.delimiterValue;
    }
    
    void setDelimiterList(final String[] delimiterValueList) {
        this.delimiterValueList = delimiterValueList;
    }
    
    String[] getDelimiterList() {
        return this.delimiterValueList;
    }
    
    void setDescription(final String description) {
        this.description = description;
    }
    
    void setTrimList(final String[] trimList) {
        this.trimList = trimList;
    }
    
    void setChildList(final Delimiter[] childList) {
        this.childList = childList;
    }
    
    Delimiter[] getChildList() {
        return this.childList;
    }
    
    void setParent(final Delimiter parent) {
        this.parent = parent;
    }
    
    Delimiter getParent() {
        return this.parent;
    }
    
    void setMatch(final Match match) {
        this.match = match;
    }
    
    Match getMatch() {
        return this.match;
    }
    
    void printTrimList() {
        if (this.trimList != null) {
            for (int i = 0; i < this.trimList.length; ++i) {
                System.out.println(this.trimList[i]);
            }
        }
    }
}
