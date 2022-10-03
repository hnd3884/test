package com.adventnet.cli.messageset;

import java.io.Serializable;

public class SimpleOptsArgs implements Serializable
{
    public static final byte INTEGER = 1;
    public static final byte CHAR = 2;
    public static final byte FLOAT = 3;
    public static final byte STRING = 4;
    private String optChar;
    private String optDep;
    private String optName;
    private String optArg;
    private String optPrefix;
    private byte optType;
    
    public SimpleOptsArgs() {
        this.optChar = null;
        this.optDep = null;
        this.optName = null;
        this.optArg = null;
        this.optPrefix = "-";
        this.optType = 0;
    }
    
    public void setSimpleOptArgsChar(final String optChar) {
        this.optChar = optChar;
    }
    
    public String getSimpleOptArgsChar() {
        return this.optChar;
    }
    
    public void setSimpleOptArgsDep(final String optDep) {
        this.optDep = optDep;
    }
    
    public String getSimpleOptArgsDep() {
        return this.optDep;
    }
    
    public void setSimpleOptArgsPrefix(final String optPrefix) {
        this.optPrefix = optPrefix;
    }
    
    public String getSimpleOptArgsPrefix() {
        return this.optPrefix;
    }
    
    public void setSimpleOptArgsName(final String optName) {
        this.optName = optName;
    }
    
    public String getSimpleOptArgsName() {
        return this.optName;
    }
    
    public void setSimpleOptArgsArg(final String optArg) {
        this.optArg = optArg;
    }
    
    public String getSimpleOptArgsArg() {
        return this.optArg;
    }
    
    public void setOptionType(final byte optType) {
        this.optType = optType;
    }
    
    public byte getOptionType() {
        return this.optType;
    }
    
    public String toString() {
        return this.getSimpleOptArgsChar();
    }
}
