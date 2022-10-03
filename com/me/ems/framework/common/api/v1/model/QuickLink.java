package com.me.ems.framework.common.api.v1.model;

import java.util.List;

public class QuickLink
{
    private String moduleName;
    private Long pageNumber;
    private String showHideStatus;
    private String primaryContact;
    private List<QuickLinkGroup> quickLinkContent;
    
    public String getModuleName() {
        return this.moduleName;
    }
    
    public void setModuleName(final String moduleName) {
        this.moduleName = moduleName;
    }
    
    public String getShowHideStatus() {
        return this.showHideStatus;
    }
    
    public void setShowHideStatus(final String showHideStatus) {
        this.showHideStatus = showHideStatus;
    }
    
    public String getPrimaryContact() {
        return this.primaryContact;
    }
    
    public void setPrimaryContact(final String primaryContact) {
        this.primaryContact = primaryContact;
    }
    
    public List<QuickLinkGroup> getQuickLinkContent() {
        return this.quickLinkContent;
    }
    
    public void setQuickLinkContent(final List<QuickLinkGroup> quickLinkContent) {
        this.quickLinkContent = quickLinkContent;
    }
    
    public Long getPageNumber() {
        return this.pageNumber;
    }
    
    public void setPageNumber(final Long pageNumber) {
        this.pageNumber = pageNumber;
    }
}
