package com.me.mdm.api.core.tabcomponent;

import java.util.ArrayList;
import com.me.ems.framework.server.tabcomponents.core.ServerAPIConstants;
import java.util.Map;
import java.util.List;

public class MDMTabProvider extends MDMCoreTabProvider
{
    private final List<Map<ServerAPIConstants.TabAttribute, Object>> allTabList;
    
    public MDMTabProvider() {
        this.allTabList = new ArrayList<Map<ServerAPIConstants.TabAttribute, Object>>() {
            {
                this.add(MDMTabProvider.this.homeTabMap);
                this.add(MDMTabProvider.this.deviceMgmtTabMap);
                this.add(MDMTabProvider.this.inventoryTabMap);
                this.add(MDMTabProvider.this.enrollmentTabMap);
                this.add(MDMTabProvider.this.reportsTabMap);
                this.add(MDMTabProvider.this.adminTabMap);
                this.add(MDMTabProvider.this.supportTabMap);
            }
        };
    }
    
    public List<Map<ServerAPIConstants.TabAttribute, Object>> getProductSpecificTabComponents() throws Exception {
        return this.allTabList;
    }
    
    public String getHomePageUrl() {
        return "/webclient" + this.homeTabMap.get(ServerAPIConstants.TabAttribute.url);
    }
}
