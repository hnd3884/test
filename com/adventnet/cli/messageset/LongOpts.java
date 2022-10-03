package com.adventnet.cli.messageset;

import java.io.Serializable;

public class LongOpts implements Serializable
{
    private String optNames;
    private String optDep;
    private String optPrefix;
    private String optVal;
    
    public LongOpts() {
        this.optNames = null;
        this.optDep = null;
        this.optPrefix = "--";
        this.optVal = null;
    }
    
    public String getLongOptNames() {
        return this.optNames;
    }
    
    public void setLongOptNames(final String optNames) {
        this.optNames = optNames;
    }
    
    public String getLongOptDep() {
        return this.optDep;
    }
    
    public void setLongOptDep(final String optDep) {
        this.optDep = optDep;
    }
    
    public String getLongOptPrefix() {
        return this.optPrefix;
    }
    
    public void setLongOptPrefix(final String optPrefix) {
        this.optPrefix = optPrefix;
    }
    
    public String getLongOptVal() {
        return this.optVal;
    }
    
    public void setLongOptVal(final String optVal) {
        this.optVal = optVal;
    }
    
    public String toString() {
        return this.getLongOptNames();
    }
}
