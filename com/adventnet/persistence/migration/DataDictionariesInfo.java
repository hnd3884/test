package com.adventnet.persistence.migration;

import java.net.URL;

public class DataDictionariesInfo
{
    private URL[] oldDataDictionariesPath;
    private URL[] newDataDictionariesPath;
    private DDLChanges.DDLOperationType ddlOperationType;
    private boolean ignoreMaxSize;
    
    public DataDictionariesInfo(final URL[] oldDataDictionariesPath, final URL[] newDataDictionariesPath) {
        this.ddlOperationType = DDLChanges.DDLOperationType.INSTALL;
        this.ignoreMaxSize = false;
        this.oldDataDictionariesPath = oldDataDictionariesPath;
        this.newDataDictionariesPath = newDataDictionariesPath;
    }
    
    public void ignoreMaxSizeReductionOnRevert() {
        this.ignoreMaxSize = true;
    }
    
    public void setDDLOperationType(final DDLChanges.DDLOperationType ddlOperationType) {
        this.ddlOperationType = ddlOperationType;
    }
    
    boolean ignoreMaxSizeReduction() {
        return this.ignoreMaxSize;
    }
    
    DDLChanges.DDLOperationType getDDLOperationType() {
        return this.ddlOperationType;
    }
    
    URL[] getOldDataDictionaries() {
        return this.oldDataDictionariesPath;
    }
    
    URL[] getNewDataDictionaries() {
        return this.newDataDictionariesPath;
    }
}
