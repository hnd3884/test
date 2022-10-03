package com.me.devicemanagement.framework.webclient.audit;

import com.me.devicemanagement.framework.server.common.DMModuleHandler;
import com.me.devicemanagement.framework.server.util.CommonUtils;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import java.util.TreeMap;
import java.util.Map;

public class EventLogUtil
{
    private static EventLogUtil event;
    
    public static EventLogUtil getInstance() {
        if (EventLogUtil.event == null) {
            EventLogUtil.event = new EventLogUtil();
        }
        return EventLogUtil.event;
    }
    
    public Map<String, String> getActionLogModules() throws Exception {
        final DataObject eventCodeObject = this.getEventCodeDOForModuleCriteria();
        final Iterator eventModuleIterator = eventCodeObject.getRows("EventCode");
        final Map<String, String> eventCodeMap = new TreeMap<String, String>();
        while (eventModuleIterator.hasNext()) {
            final Row eventRow = eventModuleIterator.next();
            final String eventModule = (String)eventRow.get("EVENT_MODULE");
            final String eventLabel = I18N.getMsg((String)eventRow.get("EVENT_MODULE_LABEL"), new Object[0]);
            eventCodeMap.put(eventModule, eventLabel);
        }
        return eventCodeMap;
    }
    
    public DataObject getEventCodeDOForModuleCriteria() throws Exception {
        final Criteria desktopModuleCri = this.getDesktopModuleCriteriaForEventCode();
        final Criteria licenseCrit = this.getLicenseCriteriaForEventCode();
        final Criteria osdModuleCri = this.getOSDModuleCriteriaForEventCode();
        final Criteria pmpModuleCri = this.getPMPModuleCriteriaForEventCode();
        final Criteria vmpModuleCri = this.getVMPModuleCriteriaForEventCode();
        final Criteria rapModuleCri = this.getRAPModuleCriteriaForEventCode();
        final Criteria blmModuleCri = this.getBLMModuleCriteriaForEventCode();
        final Criteria dcmModuleCri = this.getDCMModuleCriteriaForEventCode();
        final Criteria acpModuleCri = this.getACPModuleCriteriaForEventCode();
        final Criteria uesProductCri = this.getUESProductCriteriaForEventCode();
        Criteria otherModuleCri = null;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EventCode"));
        Criteria crit = null;
        if (desktopModuleCri != null) {
            crit = desktopModuleCri;
        }
        if (licenseCrit != null) {
            if (crit != null) {
                crit = crit.and(licenseCrit);
            }
            else {
                crit = licenseCrit;
            }
        }
        if (osdModuleCri != null) {
            otherModuleCri = osdModuleCri;
        }
        if (pmpModuleCri != null) {
            if (otherModuleCri != null) {
                otherModuleCri = otherModuleCri.and(pmpModuleCri);
            }
            else {
                otherModuleCri = pmpModuleCri;
            }
        }
        if (vmpModuleCri != null) {
            if (otherModuleCri != null) {
                otherModuleCri = otherModuleCri.and(vmpModuleCri);
            }
            else {
                otherModuleCri = vmpModuleCri;
            }
        }
        if (rapModuleCri != null) {
            if (otherModuleCri != null) {
                otherModuleCri = otherModuleCri.and(rapModuleCri);
            }
            else {
                otherModuleCri = rapModuleCri;
            }
        }
        if (blmModuleCri != null) {
            if (otherModuleCri != null) {
                otherModuleCri = otherModuleCri.and(blmModuleCri);
            }
            else {
                otherModuleCri = blmModuleCri;
            }
        }
        if (dcmModuleCri != null) {
            if (otherModuleCri != null) {
                otherModuleCri = otherModuleCri.and(dcmModuleCri);
            }
            else {
                otherModuleCri = dcmModuleCri;
            }
        }
        if (acpModuleCri != null) {
            if (otherModuleCri != null) {
                otherModuleCri = otherModuleCri.and(acpModuleCri);
            }
            else {
                otherModuleCri = acpModuleCri;
            }
        }
        if (otherModuleCri != null) {
            if (crit != null) {
                crit = crit.or(otherModuleCri);
            }
            else {
                crit = otherModuleCri;
            }
        }
        if (uesProductCri != null) {
            crit = uesProductCri;
        }
        if (CustomerInfoUtil.isSAS) {
            final String[] array = { "AD Reports", "Query Reports", "Remote DB Access", "Maintenance Window", "Help Desk", "Forwarding Server", "Server Maintenance" };
            crit = crit.and(new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)array, 9));
        }
        query.setCriteria(crit);
        query.addSelectColumn(Column.getColumn("EventCode", "EVENT_ID"));
        query.addSelectColumn(Column.getColumn("EventCode", "EVENT_MODULE"));
        query.addSelectColumn(Column.getColumn("EventCode", "EVENT_MODULE_LABEL"));
        final SortColumn sortCol = new SortColumn(Column.getColumn("EventCode", "EVENT_MODULE"), true);
        query.addSortColumn(sortCol);
        DataObject resultDO = null;
        resultDO = SyMUtil.getPersistence().get(query);
        return resultDO;
    }
    
    public Criteria getPMPModuleCriteriaForEventCode() {
        Criteria cri = null;
        if (CustomerInfoUtil.isPMP()) {
            String[] array;
            if (!CustomerInfoUtil.isSAS) {
                array = new String[] { "Configuration", "Schedule", "General", "User Management", "Patch Mgmt", "SoM", "Database Backup", "Custom Group", "Privacy Settings", "Maintenance Window", "Remote Shutdown", "Reports" };
            }
            else {
                array = new String[] { "Schedule", "General", "User Management", "Patch Mgmt", "SoM", "Custom Group", "Configuration", "Privacy Settings", "Reports" };
            }
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)array, 8);
        }
        return cri;
    }
    
    public Criteria getLicenseCriteriaForEventCode() {
        Criteria cri = null;
        final String productType = LicenseProvider.getInstance().getProductType();
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (productType.equalsIgnoreCase("TOOLSADDON")) {
            final String[] array = { "Configuration", "Remote DB Access", "Software Deployment", "MDM", "Patch Mgmt", "Inventory Mgmt", "AD Reports", "Status Update", "Schedule", "OS Deployer" };
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)array, 9);
        }
        else if (CustomerInfoUtil.isVMPProduct()) {
            final String[] array = { "Schedule", "General", "User Management", "Configuration", "Patch Mgmt", "SoM", "Database Backup", "Custom Group", "Wake On LAN", "Privacy Settings", "Maintenance Window", "Vulnerability", "Misconfiguration", "Web Server Misconfiguration", "Network Device" };
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)array, 8);
        }
        else if (CustomerInfoUtil.isDCPProduct()) {
            final String[] array = { "Schedule", "General", "User Management", "Configuration", "SoM", "Database Backup", "Custom Group", "Wake On LAN", "Privacy Settings", "Maintenance Window", "Device Control" };
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)array, 8);
        }
        else if (CustomerInfoUtil.isPMP()) {
            String[] array;
            if (!CustomerInfoUtil.isSAS) {
                array = new String[] { "Configuration", "Schedule", "General", "User Management", "Patch Mgmt", "SoM", "Database Backup", "Custom Group", "Privacy Settings", "Maintenance Window", "Remote Shutdown" };
            }
            else {
                array = new String[] { "Schedule", "General", "User Management", "Patch Mgmt", "SoM", "Custom Group" };
            }
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)array, 8);
        }
        else if (CustomerInfoUtil.getInstance().isRAP()) {
            String[] array;
            if (!CustomerInfoUtil.isSAS()) {
                array = new String[] { "Chat", "Custom Group", "Database Backup", "Forwarding Server", "General", "Maintenance Window", "Privacy Settings", "Remote Control", "Remote DB Access", "Remote Shutdown", "Security Settings", "SoM", "System Manager", "System Tools", "User Management", "Wake On LAN", "Configuration" };
            }
            else {
                array = new String[] { "Chat", "Custom Group", "General", "Privacy Settings", "Remote Control", "Remote Shutdown", "Security Settings", "SoM", "System Manager", "System Tools", "User Management", "Wake On LAN", "Configuration" };
            }
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)array, 8);
        }
        return cri;
    }
    
    public Criteria getDesktopModuleCriteriaForEventCode() {
        Criteria cri = null;
        if (!DMApplicationHandler.getInstance().getDesktopModuleState() && !CustomerInfoUtil.isACPProduct() && !CustomerInfoUtil.isDCPProduct() && !CustomerInfoUtil.isVMPProduct()) {
            final String[] array = { "Database Backup", "General", "MDM", "Remote DB Access", "User Management", "Security Settings" };
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)array, 8);
        }
        if (!DMUserHandler.getAdminRoleUserIds().contains(CommonUtils.getUserId())) {
            final Criteria adminCriteria = new Criteria(Column.getColumn("EventCode", "EVENT_MODULE"), (Object)"User Management", 1);
            cri = ((cri != null) ? cri.and(adminCriteria) : adminCriteria);
        }
        return cri;
    }
    
    public Criteria getOSDModuleCriteriaForEventCode() {
        Criteria cri = null;
        final boolean isOSDProduct = CustomerInfoUtil.isOSDProduct();
        if (!DMModuleHandler.isOSDEnabled() || CustomerInfoUtil.getInstance().isMSP()) {
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)"OS Deployer", 1);
        }
        if (isOSDProduct) {
            final String[] modules = { "Schedule", "General", "User Management", "SoM", "Database Backup", "Privacy Settings", "Maintenance Window", "Remote DB Access", "Forwarding Server", "Security Settings" };
            cri = new Criteria(Column.getColumn("EventCode", "EVENT_MODULE"), (Object)modules, 8);
        }
        return cri;
    }
    
    public Criteria getVMPModuleCriteriaForEventCode() {
        Criteria cri = null;
        if (CustomerInfoUtil.isVMPProduct()) {
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)new String[] { "Vulnerability", "Misconfiguration", "Web Server Misconfiguration", "Network Device" }, 8);
        }
        return cri;
    }
    
    public Criteria getRAPModuleCriteriaForEventCode() {
        Criteria cri = null;
        if (CustomerInfoUtil.getInstance().isRAP()) {
            String[] array;
            if (!CustomerInfoUtil.isSAS()) {
                array = new String[] { "Chat", "Custom Group", "Database Backup", "Forwarding Server", "General", "Maintenance Window", "Privacy Settings", "Remote Control", "Remote DB Access", "Remote Shutdown", "Security Settings", "SoM", "System Manager", "System Tools", "User Management", "Wake On LAN", "Configuration" };
            }
            else {
                array = new String[] { "Chat", "Custom Group", "General", "Privacy Settings", "Remote Control", "Remote Shutdown", "Security Settings", "SoM", "System Manager", "System Tools", "User Management", "Wake On LAN", "Configuration" };
            }
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)array, 8);
        }
        return cri;
    }
    
    public Criteria getDCMModuleCriteriaForEventCode() {
        Criteria cri = null;
        if (!CustomerInfoUtil.isDCPAddonEnabled()) {
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)new String[] { "Device Control" }, 9);
        }
        else if (CustomerInfoUtil.isDCPProduct()) {
            final String[] array = { "Schedule", "General", "User Management", "Configuration", "SoM", "Database Backup", "Custom Group", "Privacy Settings", "Device Control" };
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)array, 8);
        }
        return cri;
    }
    
    public Criteria getBLMModuleCriteriaForEventCode() {
        Criteria cri = null;
        if (!CustomerInfoUtil.isBLMEnabled()) {
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)new String[] { "BitLocker Management" }, 9);
        }
        return cri;
    }
    
    public Criteria getACPModuleCriteriaForEventCode() {
        Criteria cri = null;
        if (!CustomerInfoUtil.isACPAddonEnabled()) {
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)new String[] { "Application Control" }, 9);
        }
        else if (CustomerInfoUtil.isACPProduct()) {
            final String[] array = { "Schedule", "General", "User Management", "Configuration", "SoM", "Database Backup", "Custom Group", "Privacy Settings", "Application Control" };
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)array, 8);
        }
        return cri;
    }
    
    public Criteria getUESProductCriteriaForEventCode() {
        Criteria cri = null;
        if (CustomerInfoUtil.isUESProduct()) {
            final String[] array = { "Schedule", "General", "User Management", "Configuration", "Patch Mgmt", "SoM", "Database Backup", "Custom Group", "Wake On LAN", "Privacy Settings", "Maintenance Window", "Vulnerability", "Misconfiguration", "Web Server Misconfiguration", "Device Control", "BitLocker Management", "Application Control" };
            cri = new Criteria(new Column("EventCode", "EVENT_MODULE"), (Object)array, 8);
        }
        return cri;
    }
    
    static {
        EventLogUtil.event = null;
    }
}
