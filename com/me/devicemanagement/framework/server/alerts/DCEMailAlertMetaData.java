package com.me.devicemanagement.framework.server.alerts;

import java.io.Serializable;

public class DCEMailAlertMetaData implements Serializable
{
    public String alertName;
    public String xslFileName;
    public String alertDataHandlerName;
    public long maxSize;
    public String folderName;
    public String loggerName;
    
    public DCEMailAlertMetaData() {
        this.alertName = null;
        this.xslFileName = null;
        this.alertDataHandlerName = null;
        this.maxSize = 500L;
        this.folderName = null;
        this.loggerName = null;
    }
    
    @Override
    public String toString() {
        return "alertName=" + this.alertName + "; alertDataHandlerName=" + this.alertDataHandlerName + "; xslFileName=" + this.xslFileName + "; maxSize=" + this.maxSize;
    }
}
