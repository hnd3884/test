package com.adventnet.cli.messageset;

import java.io.Serializable;

public class LongOptsArgs implements Serializable
{
    public static final byte INTEGER = 1;
    public static final byte CHAR = 2;
    public static final byte FLOAT = 3;
    public static final byte STRING = 4;
    private String optName;
    private String optDep;
    private String optArg;
    private String optPrefix;
    private byte optType;
    
    public LongOptsArgs() {
        this.optName = null;
        this.optDep = null;
        this.optArg = null;
        this.optPrefix = "--";
        this.optType = 0;
    }
    
    public void setLongOptArgsName(final String optName) {
        this.optName = optName;
    }
    
    public String getLongOptArgsName() {
        return this.optName;
    }
    
    public void setLongOptArgsDep(final String optDep) {
        this.optDep = optDep;
    }
    
    public String getLongOptArgsDep() {
        return this.optDep;
    }
    
    public void setLongOptArgsArg(final String optArg) {
        this.optArg = optArg;
    }
    
    public String getLongOptArgsArg() {
        return this.optArg;
    }
    
    public void setLongOptArgsPrefix(final String optPrefix) {
        this.optPrefix = optPrefix;
    }
    
    public String getLongOptArgsPrefix() {
        return this.optPrefix;
    }
    
    public void setOptionType(final byte optType) {
        this.optType = optType;
    }
    
    public byte getOptionType() {
        return this.optType;
    }
    
    public String toString() {
        return this.getLongOptArgsName();
    }
}
