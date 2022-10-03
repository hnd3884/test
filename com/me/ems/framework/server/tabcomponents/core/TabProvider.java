package com.me.ems.framework.server.tabcomponents.core;

import java.util.Map;
import java.util.List;

public interface TabProvider
{
    List<Map<ServerAPIConstants.TabAttribute, Object>> getProductSpecificTabComponents() throws Exception;
    
    default String getHomePageUrl() {
        return "";
    }
}
