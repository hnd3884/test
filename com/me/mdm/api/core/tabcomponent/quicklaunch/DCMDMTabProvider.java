package com.me.mdm.api.core.tabcomponent.quicklaunch;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import com.me.ems.framework.server.tabcomponents.core.ServerAPIConstants;
import java.util.Map;
import com.me.mdm.api.core.tabcomponent.MDMCoreTabProvider;

public class DCMDMTabProvider extends MDMCoreTabProvider
{
    protected final String dcMdmHomeTab = "dcmdmhome";
    protected final String dcMdmMgmtTab = "dcmdmmgmt";
    protected final String dcMdmAdminTab = "dcmdmadmin";
    protected final Map<ServerAPIConstants.TabAttribute, Object> dcMdmHomeTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> dcMdmMgmtTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> dcMdmInventoryTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> dcMdmEnrollmentTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> dcMdmReportsTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> dcMdmAdminTabMap;
    protected final Map<ServerAPIConstants.TabAttribute, Object> dcMdmAuditLogTabMap;
    private final List<Map<ServerAPIConstants.TabAttribute, Object>> allTabList;
    
    public DCMDMTabProvider() {
        this.dcMdmHomeTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "dcmdmhome");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.common.Dashboard");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "10");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "home");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "homeTab");
            }
        };
        this.dcMdmMgmtTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "dcmdmmgmt");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.common.Management");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "20");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "mdm-manage");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "MDM_AppMgmt_Read,MDM_ContentMgmt_Read,MDM_Configurations_Read,MDM_RemoteControl_Read,MDM_OSUpdateMgmt_Read,MDM_Geofence_Read,MDM_Compliance_Read,MDM_Announcement_Read,MDM_GroupMgmt_Read");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "deviceManagementTab");
            }
        };
        this.dcMdmInventoryTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "inventory");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.common.INVENTORY");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "30");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "mdm-inventory");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "MDM_Inventory_Read");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "inventoryTab");
            }
        };
        this.dcMdmEnrollmentTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "enrollment");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.mdm.general.enrollment");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "40");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "mdm-enrollment");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "MDM_Enrollment_Read");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "enrollmentTab");
            }
        };
        this.dcMdmReportsTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "reports");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.common.REPORTS");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "50");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "mdm-reports");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "MDM_Report_Read");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "reportsTab");
            }
        };
        this.dcMdmAdminTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "dcmdmadmin");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.common.SETTINGS");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "60");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "mdm-admin");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "MDM_Settings_Write");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "settingsTab");
            }
        };
        this.dcMdmAuditLogTabMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>((Class)ServerAPIConstants.TabAttribute.class) {
            {
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabID, "auditLog");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.displayName, "dc.mdm.general.audit");
                ((EnumMap<ServerAPIConstants.TabAttribute, Boolean>)this).put(ServerAPIConstants.TabAttribute.canBeReordered, false);
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.tabOrder, "80");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.toolTip, "");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.url, "auditLog");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.roles, "MDM_Settings_Read");
                ((EnumMap<ServerAPIConstants.TabAttribute, String>)this).put(ServerAPIConstants.TabAttribute.iconURL, "auditTab");
            }
        };
        this.allTabList = new ArrayList<Map<ServerAPIConstants.TabAttribute, Object>>() {
            {
                this.add(DCMDMTabProvider.this.dcMdmHomeTabMap);
                this.add(DCMDMTabProvider.this.dcMdmMgmtTabMap);
                this.add(DCMDMTabProvider.this.dcMdmInventoryTabMap);
                this.add(DCMDMTabProvider.this.dcMdmEnrollmentTabMap);
                this.add(DCMDMTabProvider.this.dcMdmReportsTabMap);
                this.add(DCMDMTabProvider.this.dcMdmAdminTabMap);
                this.add(DCMDMTabProvider.this.dcMdmAuditLogTabMap);
            }
        };
    }
    
    public List<Map<ServerAPIConstants.TabAttribute, Object>> getProductSpecificTabComponents() throws Exception {
        return this.allTabList;
    }
    
    public String getHomePageUrl() {
        return this.dcMdmHomeTabMap.get(ServerAPIConstants.TabAttribute.url);
    }
}
