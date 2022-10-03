package com.me.ems.framework.server.tabcomponents.core;

public class ServerAPIConstants
{
    public static final String TAB_COMPONENT_RESPONSE = "application/tabComponentResponse.v1+json";
    public static final String TAB_COMPONENTS_CACHE_NAME = "TAB_COMPONENTS";
    public static final String TAB_COMPONENT_PATH = "tabComponents";
    public static final String ENTERPRISE = "Enterprise";
    public static final String PROFESSIONAL = "Professional";
    public static final String UEM = "UEM";
    public static final String STANDARD = "Standard";
    public static final String CUSTOM_TAB_STRING = "CUSTOM";
    public static final String NEW_TAB_COUNTER = "COUNTER";
    public static final String DEFAULT_NEW_TAB_CLICKS = "defaultNewTabClicks";
    
    public enum TabAttribute
    {
        tabID, 
        displayName, 
        tabOrder, 
        canBeReordered, 
        roles, 
        url, 
        mspURL, 
        iconURL, 
        toolTip, 
        isNewTab, 
        isCustomTab;
    }
    
    public enum TabComponentCacheParam
    {
        USER_TO_TAB_ORDER, 
        NEW_TABS, 
        HAS_USER_CUSTOMIZED;
    }
}
