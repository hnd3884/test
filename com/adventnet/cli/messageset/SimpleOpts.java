package com.adventnet.cli.messageset;

import java.io.Serializable;

public class SimpleOpts implements Serializable
{
    private String optChar;
    private String optDep;
    private String optPrefix;
    private String optVal;
    
    public SimpleOpts() {
        this.optChar = null;
        this.optDep = null;
        this.optPrefix = "-";
        this.optVal = null;
    }
    
    public String getSimpleOptChar() {
        return this.optChar;
    }
    
    public void setSimpleOptChar(final String optChar) {
        this.optChar = optChar;
    }
    
    public String getSimpleOptDep() {
        return this.optDep;
    }
    
    public void setSimpleOptDep(final String optDep) {
        this.optDep = optDep;
    }
    
    public String getSimpleOptPrefix() {
        return this.optPrefix;
    }
    
    public void setSimpleOptPrefix(final String optPrefix) {
        this.optPrefix = optPrefix;
    }
    
    public String getSimpleOptVal() {
        return this.optVal;
    }
    
    public void setSimpleOptVal(final String optVal) {
        this.optVal = optVal;
    }
    
    public String toString() {
        return this.getSimpleOptChar();
    }
}
