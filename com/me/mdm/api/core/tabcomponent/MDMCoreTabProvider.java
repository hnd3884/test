package com.me.mdm.api.core.tabcomponent;

import java.util.EnumMap;
import com.me.ems.framework.server.tabcomponents.core.ServerAPIConstants;
import java.util.Map;
import com.me.ems.framework.server.tabcomponents.core.TabProvider;

public abstract class MDMCoreTabProvider implements TabProvider
{
    protected final String homeTab = "home";
    protected final String deviceMgmtTab = "devicemgmt";
    protected final String inventoryTab = "inventory";
    protected final String enrollmentTab = "enrollment";
    protected final String reportsTab = "reports";
    protected final String adminTab = "admin";
    protected final String supportTab = "support";
    protected final String auditLogTab = "auditLog";
    protected final Map<ServerAPIConstants.TabAttribute, Object> homeTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> deviceMgmtTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> inventoryTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> enrollmentTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> reportsTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> adminTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> supportTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> auditLogTabMap;
    
    public MDMCoreTabProvider() {
        this.homeTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "home");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.tab.home");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "10");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "#/uems/mdm/home");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "homeTab");
            }
        };
        this.deviceMgmtTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "devicemgmt");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.mdm.manage");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "20");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "#/uems/mdm/manage");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "MDM_AppMgmt_Read,MDM_ContentMgmt_Read,MDM_Configurations_Read,MDM_RemoteControl_Read,MDM_OSUpdateMgmt_Read,MDM_Geofence_Read,MDM_Compliance_Read,MDM_GroupMgmt_Read,ModernMgmt_Geofence_Read,ModernMgmt_Compliance_Read,ModernMgmt_MDMGroupMgmt_Read,MDM_Announcement_Read,ModernMgmt_Announcement_Read");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "deviceManagementTab");
            }
        };
        this.inventoryTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "inventory");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.common.INVENTORY");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "30");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "#/uems/mdm/inventory");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "MDM_Inventory_Read,ModernMgmt_Inventory_Read");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "inventoryTab");
            }
        };
        this.enrollmentTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "enrollment");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.mdm.general.enrollment");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "40");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "#/uems/mdm/enrollment");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "MDM_Enrollment_Read,ModernMgmt_Enrollment_Read");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "enrollmentTab");
            }
        };
        this.reportsTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "reports");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.common.REPORTS");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "50");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "#/uems/mdm/reports");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "MDM_Report_Read,ModernMgmt_Report_Read");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "reportsTab");
            }
        };
        this.adminTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "admin");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.admin.common.ADMIN");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "60");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "#/uems/mdm/admin");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "Common_Read");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "settingsTab");
            }
        };
        this.supportTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "support");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.common.SUPPORT");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "70");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "#/uems/mdm/support");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "");
            }
        };
        this.auditLogTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "auditLog");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.mdm.general.audit");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "80");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "#/uems/mdm/auditLog");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "MDM_Settings_Read");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "auditTab");
            }
        };
    }
}
