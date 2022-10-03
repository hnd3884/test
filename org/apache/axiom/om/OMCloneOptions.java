package org.apache.axiom.om;

public class OMCloneOptions
{
    private boolean fetchDataHandlers;
    private boolean copyOMDataSources;
    private boolean preserveModel;
    
    public boolean isFetchDataHandlers() {
        return this.fetchDataHandlers;
    }
    
    public void setFetchDataHandlers(final boolean fetchDataHandlers) {
        this.fetchDataHandlers = fetchDataHandlers;
    }
    
    public boolean isCopyOMDataSources() {
        return this.copyOMDataSources;
    }
    
    public void setCopyOMDataSources(final boolean copyOMDataSources) {
        this.copyOMDataSources = copyOMDataSources;
    }
    
    public boolean isPreserveModel() {
        return this.preserveModel;
    }
    
    public void setPreserveModel(final boolean preserveModel) {
        this.preserveModel = preserveModel;
    }
}
