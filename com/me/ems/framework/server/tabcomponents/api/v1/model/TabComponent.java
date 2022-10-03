package com.me.ems.framework.server.tabcomponents.api.v1.model;

import com.me.ems.framework.server.tabcomponents.core.ServerAPIConstants;
import java.util.Map;
import java.util.List;

public class TabComponent
{
    private List<Map<ServerAPIConstants.TabAttribute, Object>> tabs;
    
    public List<Map<ServerAPIConstants.TabAttribute, Object>> getTabs() {
        return this.tabs;
    }
    
    public void setTabs(final List<Map<ServerAPIConstants.TabAttribute, Object>> tabs) {
        this.tabs = tabs;
    }
}
