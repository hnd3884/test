package com.adventnet.client.view.common;

import com.adventnet.client.view.web.ViewContext;

public class ExportAuditModel
{
    private long viewName;
    private long startTime;
    private long exportedTime;
    private long accountID;
    private ViewContext viewctx;
    
    public ExportAuditModel() {
        this.viewName = -1L;
        this.startTime = -1L;
        this.exportedTime = -1L;
        this.accountID = -1L;
        this.viewctx = null;
    }
    
    public void setViewName(final long viewName) {
        this.viewName = viewName;
    }
    
    public long getViewName() {
        return this.viewName;
    }
    
    public int getExportType() {
        return this.viewctx.getRenderType();
    }
    
    public void setStartTime(final long time) {
        this.startTime = time;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public void setExportedTime(final long time) {
        this.exportedTime = time;
    }
    
    public long getExportedTime() {
        return this.exportedTime;
    }
    
    public void setAccountID(final long accountID) {
        this.accountID = accountID;
    }
    
    public long getAccountID() {
        return this.accountID;
    }
    
    public void setViewContext(final ViewContext viewctx) {
        this.viewctx = viewctx;
    }
    
    public ViewContext getViewContext() {
        return this.viewctx;
    }
}
