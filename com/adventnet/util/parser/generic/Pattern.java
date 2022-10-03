package com.adventnet.util.parser.generic;

import java.util.Hashtable;

class Pattern
{
    static final int IGNORE = 1;
    static final int SUBS = 2;
    Hashtable parameterList;
    int action;
    String pattern;
    boolean first;
    boolean last;
    
    Pattern() {
        this.parameterList = null;
        this.action = 0;
        this.pattern = null;
        this.first = false;
        this.last = false;
    }
    
    void setPattern(final String pattern) {
        this.pattern = pattern;
    }
    
    String getPattern() {
        return this.pattern;
    }
    
    void setParameterList(final Hashtable parameterList) {
        this.parameterList = parameterList;
    }
    
    Hashtable getParameterList() {
        return this.parameterList;
    }
    
    void setAction(final int action) {
        this.action = action;
    }
    
    boolean isIgnore() {
        return this.action == 1;
    }
    
    boolean isSubs() {
        return this.action == 2;
    }
    
    void setFirst(final boolean first) {
        this.first = first;
    }
    
    void setLast(final boolean last) {
        this.last = last;
    }
    
    boolean isFirst() {
        return this.first;
    }
    
    boolean isLast() {
        return this.last;
    }
}
