package com.zoho.mickeyclient.action;

public class ActionContext
{
    private String menuItemName;
    private String viewName;
    
    public ActionContext(final String menuItemName, final String viewName) {
        this.menuItemName = menuItemName;
        this.viewName = viewName;
    }
    
    public String getMenuItemName() {
        return this.menuItemName;
    }
    
    public String getViewName() {
        return this.viewName;
    }
    
    @Override
    public String toString() {
        return "View: " + this.viewName + "\nMenuItem: " + this.menuItemName;
    }
}
