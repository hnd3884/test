package com.me.ems.framework.server.tabcomponents.core;

public class TabBean
{
    String tabID;
    String displayName;
    String url;
    String toolTip;
    private Integer position;
    
    public String getTabID() {
        return this.tabID;
    }
    
    public void setTabID(final String tabID) {
        this.tabID = tabID;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
    
    public String getToolTip() {
        return this.toolTip;
    }
    
    public void setToolTip(final String toolTip) {
        this.toolTip = toolTip;
    }
    
    public Integer getPosition() {
        return this.position;
    }
    
    public void setPosition(final Integer position) {
        this.position = position;
    }
}
