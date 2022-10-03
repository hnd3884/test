package com.me.mdm.server.apps.android.afw.usermgmt;

import com.adventnet.persistence.Row;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import com.me.mdm.server.agent.AgentVersionConstants;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Level;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.apps.android.afw.AFWAccountStatusHandler;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.apps.usermgmt.StoreAccountManagementHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class GoogleManagedAccountHandler
{
    public static Logger logger;
    public static int AFW_COMPATIBLE_DEVICE;
    public static int AFW_COMPATIBLE_AGENT_UPGRADE_NEEDED;
    public static int AFW_COMPATIBLE_DIFFERENT_ENROLLMENT_NEEDED;
    public static int AFW_NOT_COMPATIBLE;
    private static final String EMM_MANAGED_ACCOUNT_PREFIX = "udid#";
    
    public void addAFWAccountAdditionCmd(final Long resourceId) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        final Join managedDeviceJoin = new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        sQuery.addJoin(managedDeviceJoin);
        sQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID", "Resource.RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID", "ManagedDevice.RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        final Criteria resourceC = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceId, 0);
        sQuery.setCriteria(resourceC);
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        final Long customerId = (Long)dO.getFirstValue("Resource", "CUSTOMER_ID");
        final String udid = (String)dO.getFirstValue("ManagedDevice", "UDID");
        final List resourceList = new StoreAccountManagementHandler().addOrUpdateStoreUserForDevice(customerId, resourceId, udid);
        this.sendAFWAccountAdditionCmd(resourceList);
    }
    
    public void addAFWAccountAdditionCmd(final Long resourceId, final String udid, final Long customerId) throws Exception {
        final List resourceList = new StoreAccountManagementHandler().addOrUpdateStoreUserForDevice(customerId, resourceId, udid);
        this.sendAFWAccountAdditionCmd(resourceList);
    }
    
    public void addAFWAccountAdditionCmd(final Long customerID, final List resDetailsList, final List resourceList) throws Exception {
        final List successList = new StoreAccountManagementHandler().addOrUpdateStoreUserForDevice(customerID, resDetailsList);
        this.sendAFWAccountAdditionCmd(successList);
    }
    
    private void sendAFWAccountAdditionCmd(final List resourceList) throws Exception {
        if (resourceList != null && !resourceList.isEmpty()) {
            new AFWAccountStatusHandler().updateAFWAccountStatusForAddition(resourceList);
            NotificationHandler.getInstance().SendNotification(resourceList, 2);
            GoogleManagedAccountHandler.logger.log(Level.INFO, "Sending AFW account command to resources {0}", resourceList);
            DeviceCommandRepository.getInstance().assignCommandToDevices(DeviceCommandRepository.getInstance().addCommand("AddAFWAccount"), resourceList);
        }
    }
    
    public SelectQuery getAFWDeviceSelectQuery() {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_VERSION_CODE"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID", "MDDEVICEINFO.RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_SUPERVISED"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_PROFILEOWNER"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_TYPE"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        return sQuery;
    }
    
    public void checkAndAddAFWAccountForAll(final Long resourceID, final Long customerId, final Long versionCode) throws Exception {
        if (versionCode >= 251L) {
            final SelectQuery sQuery = new GoogleManagedAccountHandler().getAFWDeviceSelectQuery();
            final Criteria criteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            sQuery.setCriteria(criteria);
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (dO != null && !dO.isEmpty() && ManagedDeviceHandler.getInstance().isDeviceAFWCompatible(dO.getRow("ManagedDevice"), dO.getRow("MdDeviceInfo")) && !new StoreAccountManagementHandler().isStoreAccountAddedForDevice(resourceID, GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW).getLong("BUSINESSSTORE_ID"))) {
                final String udid = (String)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "UDID");
                this.addAFWAccountAdditionCmd(resourceID, udid, customerId);
            }
        }
    }
    
    public void checkAndAddAFWAccountForSamsung(final Long resourceID, final Long versionCode, final String osVersion) throws Exception {
        final int agentType = (int)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "AGENT_TYPE");
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
        final String udid = (String)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "UDID");
        if (agentType == 3 && osVersion.matches("[0-9.]+") && new VersionChecker().isGreaterOrEqual(osVersion, "7.0") && versionCode >= 233L && GoogleForWorkSettings.isEMMTypeAFWConfigured(customerId)) {
            final Long bsId = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW).getLong("BUSINESSSTORE_ID");
            if (!new StoreAccountManagementHandler().isStoreAccountAddedForDevice(resourceID, bsId)) {
                DeviceCommandRepository.getInstance().addDeviceScanCommand(new DeviceDetails(resourceID), null);
                this.addAFWAccountAdditionCmd(resourceID, udid, customerId);
            }
        }
    }
    
    public void validateManagedAccountActions(final Long customerId) throws Exception {
        if (!GoogleForWorkSettings.isEMMTypeAFWConfigured(customerId)) {
            throw new APIHTTPException("COM0015", new Object[] { "Request does not correspond to available AfW configuration" });
        }
    }
    
    public void sendAFWAccountAdditionCmdForNewDevice(final Long resourceID, final Long customerID, final String udid) throws Exception {
        final SelectQuery sQuery = new GoogleManagedAccountHandler().getAFWDeviceSelectQuery();
        sQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (dO != null && !dO.isEmpty() && ManagedDeviceHandler.getInstance().isDeviceAFWCompatible(dO.getRow("ManagedDevice"), dO.getRow("MdDeviceInfo"))) {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("BSUsersToManagedDevices");
            updateQuery.setUpdateColumn("ACCOUNT_STATUS", (Object)3);
            updateQuery.setCriteria(new Criteria(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"), (Object)resourceID, 0));
            DataAccess.update(updateQuery);
            new GoogleManagedAccountHandler().addAFWAccountAdditionCmd(resourceID, udid, customerID);
        }
    }
    
    public String getUserNameForEMMAccountCreation(final String udid) {
        return "udid#" + udid;
    }
    
    public void sendGSuiteUserAccountDetectCmd(final Long customerId) {
        final SelectQuery sQuery = new GoogleManagedAccountHandler().getAFWDeviceSelectQuery();
        final Criteria deviceOwnerCriteria = new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)true, 0);
        final Criteria profileOwnerCriteria = new Criteria(new Column("MdDeviceInfo", "IS_PROFILEOWNER"), (Object)true, 0);
        final Criteria managedDeviceCriteria = ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria();
        final Criteria customerIdCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria agentVersionCriteria = new Criteria(new Column("ManagedDevice", "AGENT_VERSION_CODE"), (Object)AgentVersionConstants.GSUITE_USER_DETECT_AGENT_VERSION, 4);
        final Criteria platFormCriteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
        sQuery.setCriteria(agentVersionCriteria.and(platFormCriteria.and(managedDeviceCriteria.and(customerIdCriteria.and(deviceOwnerCriteria.or(profileOwnerCriteria))))));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDevice");
                final List<Long> resourceList = new ArrayList<Long>(new HashSet<Long>(DBUtil.getColumnValuesAsList(iterator, "RESOURCE_ID")));
                DeviceCommandRepository.getInstance().assignCommandToDevices(DeviceCommandRepository.getInstance().addCommand("DetectUserGSuiteAccount"), resourceList);
                GoogleManagedAccountHandler.logger.log(Level.INFO, "Sending GsuiteAccountDetection command to resources {0}", resourceList);
                NotificationHandler.getInstance().SendNotification(resourceList, 2);
            }
        }
        catch (final Exception e) {
            GoogleManagedAccountHandler.logger.log(Level.WARNING, "Cannot add user account detection command for Gsuite", e);
        }
    }
    
    public int isAndWhyDeviceNotAFWCompatible(final Long resourceId) {
        try {
            final SelectQuery sQuery = new GoogleManagedAccountHandler().getAFWDeviceSelectQuery();
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            sQuery.setCriteria(criteria);
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (dO != null && !dO.isEmpty()) {
                return this.isAndWhyDeviceNotAFWCompatible(dO.getRow("ManagedDevice"), dO.getRow("MdDeviceInfo"));
            }
        }
        catch (final Exception ex) {
            GoogleManagedAccountHandler.logger.log(Level.WARNING, "Exception in isAndWhyDeviceNotAFWCompatible()", ex);
        }
        return GoogleManagedAccountHandler.AFW_NOT_COMPATIBLE;
    }
    
    public Map<Integer, List<Long>> isAndWhyDevicesNotAFWCompatible(final List<Long> resourceIds) {
        final Map<Integer, List<Long>> afwCompatibilityStatusToResourceList = new HashMap<Integer, List<Long>>();
        try {
            final SelectQuery sQuery = new GoogleManagedAccountHandler().getAFWDeviceSelectQuery();
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
            sQuery.setCriteria(criteria);
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (dO != null && !dO.isEmpty()) {
                for (final Long resourceId : resourceIds) {
                    final Criteria deviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
                    int afwCompatibilityStatus = GoogleManagedAccountHandler.AFW_NOT_COMPATIBLE;
                    final Row managedDeviceRow = dO.getRow("ManagedDevice", deviceCriteria);
                    final Row mdDeviceInfo = dO.getRow("MdDeviceInfo", deviceCriteria);
                    if (managedDeviceRow != null && mdDeviceInfo != null) {
                        afwCompatibilityStatus = this.isAndWhyDeviceNotAFWCompatible(managedDeviceRow, mdDeviceInfo);
                    }
                    List devicesList = afwCompatibilityStatusToResourceList.get(afwCompatibilityStatus);
                    if (devicesList == null) {
                        devicesList = new ArrayList();
                    }
                    devicesList.add(resourceId);
                    afwCompatibilityStatusToResourceList.put(afwCompatibilityStatus, devicesList);
                }
            }
        }
        catch (final Exception ex) {
            GoogleManagedAccountHandler.logger.log(Level.WARNING, "Exception in isAndWhyDevicesNotAFWCompatible()", ex);
        }
        return afwCompatibilityStatusToResourceList;
    }
    
    public int isAndWhyDeviceNotAFWCompatible(final Row managedDeviceRow, final Row mdDeviceInfoRow) {
        Boolean deviceOwner = (Boolean)mdDeviceInfoRow.get("IS_SUPERVISED");
        Boolean profileOwner = (Boolean)mdDeviceInfoRow.get("IS_PROFILEOWNER");
        final int agentType = (int)managedDeviceRow.get("AGENT_TYPE");
        final String osVersion = (String)mdDeviceInfoRow.get("OS_VERSION");
        final Long agentVersion = (Long)managedDeviceRow.get("AGENT_VERSION_CODE");
        deviceOwner = (deviceOwner != null && deviceOwner);
        profileOwner = (profileOwner != null && profileOwner);
        if (osVersion == null) {
            return GoogleManagedAccountHandler.AFW_NOT_COMPATIBLE;
        }
        if (profileOwner || deviceOwner) {
            return GoogleManagedAccountHandler.AFW_COMPATIBLE_DEVICE;
        }
        if (agentType == 3 && osVersion.startsWith("6.")) {
            return GoogleManagedAccountHandler.AFW_COMPATIBLE_DIFFERENT_ENROLLMENT_NEEDED;
        }
        if (agentVersion % 100000L >= 253L) {
            return GoogleManagedAccountHandler.AFW_COMPATIBLE_DEVICE;
        }
        return GoogleManagedAccountHandler.AFW_COMPATIBLE_AGENT_UPGRADE_NEEDED;
    }
    
    static {
        GoogleManagedAccountHandler.logger = Logger.getLogger("MDMLogger");
        GoogleManagedAccountHandler.AFW_COMPATIBLE_DEVICE = 0;
        GoogleManagedAccountHandler.AFW_COMPATIBLE_AGENT_UPGRADE_NEEDED = 1;
        GoogleManagedAccountHandler.AFW_COMPATIBLE_DIFFERENT_ENROLLMENT_NEEDED = 2;
        GoogleManagedAccountHandler.AFW_NOT_COMPATIBLE = 3;
    }
}
