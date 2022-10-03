package org.apache.axiom.soap;

import org.apache.axiom.om.OMCloneOptions;

public class SOAPCloneOptions extends OMCloneOptions
{
    private Boolean processedFlag;
    
    public Boolean getProcessedFlag() {
        return this.processedFlag;
    }
    
    public void setProcessedFlag(final Boolean processedFlag) {
        this.processedFlag = processedFlag;
    }
}
