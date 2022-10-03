package com.adventnet.cli.messageset;

import java.io.Serializable;

public class CmdOptions implements Serializable
{
    private SimpleOpts[] simpleOptsList;
    private SimpleOptsArgs[] simpleOptsArgsList;
    private LongOpts[] longOptsList;
    private LongOptsArgs[] longOptsArgsList;
    private String description;
    
    public CmdOptions() {
        this.simpleOptsList = null;
        this.simpleOptsArgsList = null;
        this.longOptsList = null;
        this.longOptsArgsList = null;
        this.description = null;
    }
    
    public void setSimpleOptsList(final SimpleOpts[] simpleOptsList) {
        this.simpleOptsList = simpleOptsList;
    }
    
    public SimpleOpts[] getSimpleOptsList() {
        return this.simpleOptsList;
    }
    
    public void setSimpleOptsArgsList(final SimpleOptsArgs[] simpleOptsArgsList) {
        this.simpleOptsArgsList = simpleOptsArgsList;
    }
    
    public SimpleOptsArgs[] getSimpleOptsArgsList() {
        return this.simpleOptsArgsList;
    }
    
    public void setLongOptsList(final LongOpts[] longOptsList) {
        this.longOptsList = longOptsList;
    }
    
    public LongOpts[] getLongOptsList() {
        return this.longOptsList;
    }
    
    public void setLongOptsArgsList(final LongOptsArgs[] longOptsArgsList) {
        this.longOptsArgsList = longOptsArgsList;
    }
    
    public LongOptsArgs[] getLongOptsArgsList() {
        return this.longOptsArgsList;
    }
    
    public String getOptionsDescription() {
        return this.description;
    }
    
    public void setOptionsDescription(final String description) {
        this.description = description;
    }
    
    public String toString() {
        return "Options";
    }
}
