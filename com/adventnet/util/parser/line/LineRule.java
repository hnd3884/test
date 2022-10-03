package com.adventnet.util.parser.line;

import java.util.Vector;

class LineRule
{
    Vector paramList;
    String delimiter;
    int startLineNo;
    int endLineNo;
    boolean table;
    
    LineRule() {
        this.paramList = null;
        this.delimiter = null;
        this.startLineNo = -1;
        this.endLineNo = 0;
        this.table = false;
    }
    
    void setParamList(final Vector paramList) {
        this.paramList = paramList;
    }
    
    Vector getParamList() {
        return this.paramList;
    }
    
    void setStartLineNo(final int startLineNo) {
        this.startLineNo = startLineNo;
    }
    
    int getStartLineNo() {
        return this.startLineNo;
    }
    
    void setEndLineNo(final int endLineNo) {
        this.endLineNo = endLineNo;
    }
    
    int getEndLineNo() {
        return this.endLineNo;
    }
    
    void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }
    
    String getDelimiter() {
        return this.delimiter;
    }
    
    boolean isTable() {
        return this.table;
    }
    
    void setTable(final boolean table) {
        this.table = table;
    }
}
