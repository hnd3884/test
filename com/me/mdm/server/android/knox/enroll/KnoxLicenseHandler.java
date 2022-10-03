package com.me.mdm.server.android.knox.enroll;

import com.me.devicemanagement.framework.server.util.Utils;
import java.util.HashMap;
import org.json.simple.JSONArray;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccess;
import java.util.List;
import java.util.Iterator;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.alerts.AlertMailGeneratorUtil;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.simple.JSONObject;
import java.util.logging.Logger;

public class KnoxLicenseHandler
{
    private static final Logger LOGGER;
    private static KnoxLicenseHandler handler;
    public static final int KNOX_LICENSE_NOT_AVAILABLE = 0;
    public static final int KNOX_LICENSE_EXPIRY_ABOUT_TO_NO = 1;
    public static final int KNOX_LICENSE_EXPIRY_ABOUT_TO_YES = 2;
    public static final int KNOX_LICENSE_EXPIRY_EXPIRED = 3;
    
    public static KnoxLicenseHandler getInstance() {
        return KnoxLicenseHandler.handler;
    }
    
    public void addOrUpdateKnoxLicenseData(final JSONObject data) throws DataAccessException, Exception {
        String oldLicenseData = null;
        String newLicenseData = null;
        final SelectQuery licenseQuery = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDetail"));
        licenseQuery.addSelectColumn(new Column((String)null, "*"));
        licenseQuery.setCriteria(new Criteria(new Column("KnoxLicenseDetail", "CUSTOMER_ID"), (Object)data.get((Object)"CUSTOMER_ID"), 0));
        final DataObject dO = SyMUtil.getPersistence().get(licenseQuery);
        String sEventLogRemarks;
        if (dO.isEmpty()) {
            Row licenseRow = new Row("KnoxLicenseDetail");
            licenseRow = this.getKnoxLicenseRow(licenseRow, data);
            dO.addRow(licenseRow);
            sEventLogRemarks = "dc.mdm.android.knox.event_log.update_license_added";
            this.addDefaultDSEntry((Long)data.get((Object)"CUSTOMER_ID"));
            KnoxLicenseHandler.LOGGER.log(Level.INFO, "Method : addOrUpdateKnoxLicenseData KNOX license added with data {0}", data.toString());
        }
        else {
            Row licenseRow = dO.getFirstRow("KnoxLicenseDetail");
            oldLicenseData = (String)licenseRow.get("LICENSE_DATA");
            newLicenseData = (String)data.get((Object)"LICENSE_DATA");
            licenseRow = this.getKnoxLicenseRow(licenseRow, data);
            dO.updateRow(licenseRow);
            sEventLogRemarks = "dc.mdm.android.knox.event_log.update_license";
            KnoxLicenseHandler.LOGGER.log(Level.INFO, "Method : addOrUpdateKnoxLicenseData KNOX license updated data {0}", data.toString());
        }
        SyMUtil.getPersistence().update(dO);
        final Long currentlyLoggedInUserLoginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
        final String userName = DMUserHandler.getDCUser(currentlyLoggedInUserLoginId);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2064, null, userName, sEventLogRemarks, "", (Long)data.get((Object)"CUSTOMER_ID"));
        final AlertMailGeneratorUtil mailGenerator = new AlertMailGeneratorUtil();
        mailGenerator.setCustomerEMailAddress((long)data.get((Object)"CUSTOMER_ID"), (String)data.get((Object)"EMAIL_TO_NOTIFY"), "MDM-Knox");
        MessageProvider.getInstance().hideMessage("KNOX_UPLOAD_LICENSE", (Long)data.get((Object)"CUSTOMER_ID"));
        final int iAlert = this.shouldAlertForKnoxLicenseExpiry((Long)data.get((Object)"CUSTOMER_ID"));
        if (iAlert == 2) {
            MessageProvider.getInstance().unhideMessage("KNOX_LICENSE_EXPIRY", (Long)data.get((Object)"CUSTOMER_ID"));
            MessageProvider.getInstance().hideMessage("KNOX_LICENSE_EXPIRED", (Long)data.get((Object)"CUSTOMER_ID"));
        }
        else if (iAlert == 3) {
            MessageProvider.getInstance().unhideMessage("KNOX_LICENSE_EXPIRED", (Long)data.get((Object)"CUSTOMER_ID"));
            MessageProvider.getInstance().hideMessage("KNOX_LICENSE_EXPIRY", (Long)data.get((Object)"CUSTOMER_ID"));
        }
        else if (iAlert == 1) {
            MessageProvider.getInstance().hideMessage("KNOX_LICENSE_EXPIRED", (Long)data.get((Object)"CUSTOMER_ID"));
            MessageProvider.getInstance().hideMessage("KNOX_LICENSE_EXPIRY", (Long)data.get((Object)"CUSTOMER_ID"));
        }
        MessageProvider.getInstance().hideMessage("KNOX_COUNT_EXHAUSTING", (Long)data.get((Object)"CUSTOMER_ID"));
        MessageProvider.getInstance().hideMessage("KNOX_COUNT_EXHAUSTED", (Long)data.get((Object)"CUSTOMER_ID"));
        if (oldLicenseData != null && this.checkKnoxLicenseDataDiff(oldLicenseData, newLicenseData)) {
            this.reapplyKnoxLicense((Long)data.get((Object)"CUSTOMER_ID"));
        }
    }
    
    private boolean checkKnoxLicenseDataDiff(final String oldLicenseData, final String newLicenseData) {
        return !oldLicenseData.equals(newLicenseData);
    }
    
    private void reapplyKnoxLicense(final Long customerId) throws DataAccessException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("KNOXDeviceToLicenseRel"));
        sQuery.addSelectColumn(new Column("KNOXDeviceToLicenseRel", "RESOURCE_ID"));
        final Join customerJoin = new Join("KNOXDeviceToLicenseRel", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        sQuery.addJoin(customerJoin);
        sQuery.setCriteria(new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0));
        final Iterator rows = SyMUtil.getPersistence().get(sQuery).getRows("KNOXDeviceToLicenseRel");
        final String commandName = "ActivateKnoxLicense";
        final List resourceList = new ArrayList();
        while (rows.hasNext()) {
            final Long resourceId = (Long)rows.next().get("RESOURCE_ID");
            DeviceCommandRepository.getInstance().addSecurityCommand(resourceId, commandName);
            KnoxUtil.getInstance().updateStatus(resourceId, -1, "License Reapplied", -1);
            resourceList.add(resourceId);
        }
        NotificationHandler.getInstance().SendNotification(resourceList, 2);
        final Long currentlyLoggedInUserLoginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
        final String userName = DMUserHandler.getDCUser(currentlyLoggedInUserLoginId);
        final String sEventLogRemarks = "dc.mdm.android.knox.event_log.license_reapplied";
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2064, null, userName, sEventLogRemarks, "", customerId);
        KnoxLicenseHandler.LOGGER.log(Level.INFO, "Method : reapplyKnoxLicense, KNOX license reapplied for customer {0}", customerId);
    }
    
    private Row getKnoxLicenseRow(final Row row, final JSONObject data) {
        row.set("EXPIRY_DATE", (Object)data.get((Object)"EXPIRY_DATE"));
        row.set("LICENSE_DATA", data.get((Object)"LICENSE_DATA"));
        row.set("MAX_COUNT", (Object)data.get((Object)"MAX_COUNT"));
        if (row.get("CUSTOMER_ID") == null) {
            row.set("CUSTOMER_ID", (Object)data.get((Object)"CUSTOMER_ID"));
        }
        return row;
    }
    
    public void addOrUpdateKnoxLicenseDS(final JSONObject data) throws DataAccessException, Exception {
        final SelectQuery dsQuery = (SelectQuery)new SelectQueryImpl(new Table("KNOXDistributionSettings"));
        dsQuery.addSelectColumn(new Column((String)null, "*"));
        dsQuery.setCriteria(new Criteria(new Column("KNOXDistributionSettings", "CUSTOMER_ID"), data.get((Object)"CUSTOMER_ID"), 0));
        final DataObject dO = SyMUtil.getPersistence().get(dsQuery);
        if (!dO.isEmpty()) {
            final Row dsRow = dO.getFirstRow("KNOXDistributionSettings");
            DataAccess.delete(dsRow);
        }
        Row dsRow = new Row("KNOXDistributionSettings");
        dsRow = this.getDSRow(dsRow, data);
        dO.addRow(dsRow);
        SyMUtil.getPersistence().update(dO);
        this.updateDSToGroupRel(data, (Long)dsRow.get("KNOXSETTINGS_ID"));
        KnoxLicenseHandler.LOGGER.log(Level.INFO, "Method : addOrUpdateKnoxLicenseDS, KNOX distribution setting updated with {0}", data);
    }
    
    private Row getDSRow(final Row row, final JSONObject data) throws DataAccessException, Exception {
        final Long licenseId = KnoxUtil.getInstance().getLicenseId((Long)data.get((Object)"CUSTOMER_ID"));
        row.set("KNOXSETTINGS_OPTION", (Object)data.get((Object)"KNOXSETTINGS_OPTION"));
        row.set("KNOXSETTINGS_TOGROUPONLY", data.get((Object)"KNOXSETTINGS_TOGROUPONLY"));
        row.set("OVERWRITE_EXISTING_CONTAINER", (Object)data.get((Object)"OVERWRITE_EXISTING_CONTAINER"));
        if (row.get("CUSTOMER_ID") == null) {
            row.set("CUSTOMER_ID", (Object)data.get((Object)"CUSTOMER_ID"));
        }
        return row;
    }
    
    private void updateDSToGroupRel(final JSONObject data, final Long dsId) throws Exception {
        final Integer dsOption = (Integer)data.get((Object)"KNOXSETTINGS_OPTION");
        if (dsOption == 1 && (boolean)data.get((Object)"KNOXSETTINGS_TOGROUPONLY")) {
            final DataObject groupRelDO = (DataObject)new WritableDataObject();
            final JSONArray groupId = (JSONArray)data.get((Object)"KnoxLicenseDSToGroupRel");
            for (int i = 0; i < groupId.size(); ++i) {
                final Row groupRelRow = new Row("KnoxLicenseDSToGroupRel");
                groupRelRow.set("KNOXSETTINGS_ID", (Object)dsId);
                groupRelRow.set("GROUP_ID", (Object)Long.parseLong((String)groupId.get(i)));
                groupRelDO.addRow(groupRelRow);
            }
            SyMUtil.getPersistence().update(groupRelDO);
            KnoxLicenseHandler.LOGGER.log(Level.INFO, "Method : updateDSToGroupRel, KNOX distribution setting to group {0}", data);
        }
    }
    
    public int shouldAlertForKnoxLicenseExpiry(final Long customerId) {
        final SelectQuery licenseQuery = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDetail"));
        licenseQuery.addSelectColumn(new Column((String)null, "*"));
        licenseQuery.setCriteria(new Criteria(new Column("KnoxLicenseDetail", "CUSTOMER_ID"), (Object)customerId, 0));
        try {
            final DataObject dO = SyMUtil.getPersistence().get(licenseQuery);
            if (!dO.isEmpty()) {
                final Row row = dO.getFirstRow("KnoxLicenseDetail");
                final Long expiryDate = (Long)row.get("EXPIRY_DATE");
                final Integer daysToAlert = 30;
                final Long diff = expiryDate - System.currentTimeMillis();
                if (diff == 0L || diff < 0L) {
                    return 3;
                }
                if (diff > daysToAlert * 24 * 60 * 60 * 1000) {
                    return 1;
                }
                return 2;
            }
        }
        catch (final Exception ex) {
            KnoxLicenseHandler.LOGGER.log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public int knoxLicenseDaysToExpire(final Long customerId) {
        final SelectQuery licenseQuery = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDetail"));
        licenseQuery.addSelectColumn(new Column((String)null, "*"));
        licenseQuery.setCriteria(new Criteria(new Column("KnoxLicenseDetail", "CUSTOMER_ID"), (Object)customerId, 0));
        try {
            final DataObject dO = SyMUtil.getPersistence().get(licenseQuery);
            if (!dO.isEmpty()) {
                final Row row = dO.getFirstRow("KnoxLicenseDetail");
                final Long expiryDate = (Long)row.get("EXPIRY_DATE");
                final Long diff = expiryDate - System.currentTimeMillis();
                return (int)(diff / 86400000L) + 1;
            }
        }
        catch (final Exception ex) {
            KnoxLicenseHandler.LOGGER.log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public HashMap getKnoxCustomerLicense(final Long customerID) {
        final HashMap knoxLicense = new HashMap();
        try {
            final SelectQuery licenseQuery = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDetail"));
            licenseQuery.addSelectColumn(new Column((String)null, "*"));
            licenseQuery.setCriteria(new Criteria(new Column("KnoxLicenseDetail", "CUSTOMER_ID"), (Object)customerID, 0));
            final DataObject dO = SyMUtil.getPersistence().get(licenseQuery);
            if (!dO.isEmpty()) {
                final Row knoxLicenseDetails = dO.getFirstRow("KnoxLicenseDetail");
                knoxLicense.put("LICENSE_DATA", knoxLicenseDetails.get("LICENSE_DATA"));
                knoxLicense.put("MAX_COUNT", knoxLicenseDetails.get("MAX_COUNT"));
                knoxLicense.put("LICENSE_ID", knoxLicenseDetails.get("LICENSE_ID"));
                knoxLicense.put("EXPIRY_DATE", Utils.getTime((Long)knoxLicenseDetails.get("EXPIRY_DATE")));
                final AlertMailGeneratorUtil mailGenerator = new AlertMailGeneratorUtil();
                knoxLicense.put("EMAIL_TO_NOTIFY", mailGenerator.getCustomerEMailAddress(customerID, "MDM-Knox"));
            }
            return knoxLicense;
        }
        catch (final Exception exp) {
            KnoxLicenseHandler.LOGGER.log(Level.SEVERE, null, exp);
            return knoxLicense;
        }
    }
    
    public HashMap getKnoxCustomerDS(final Long customerID) {
        final HashMap knoxDS = new HashMap();
        try {
            final SelectQuery knoxDSQuery = (SelectQuery)new SelectQueryImpl(new Table("KNOXDistributionSettings"));
            knoxDSQuery.addSelectColumn(new Column((String)null, "*"));
            knoxDSQuery.setCriteria(new Criteria(new Column("KNOXDistributionSettings", "CUSTOMER_ID"), (Object)customerID, 0));
            final DataObject dO = SyMUtil.getPersistence().get(knoxDSQuery);
            if (!dO.isEmpty()) {
                final Row knoxLicenseDetails = dO.getFirstRow("KNOXDistributionSettings");
                knoxDS.put("dsOption", knoxLicenseDetails.get("KNOXSETTINGS_OPTION"));
                knoxDS.put("dsToGroup", knoxLicenseDetails.get("KNOXSETTINGS_TOGROUPONLY"));
                knoxDS.put("overwriteExistingContainer", knoxLicenseDetails.get("OVERWRITE_EXISTING_CONTAINER"));
                final SelectQuery knoxLicGroupDS = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDSToGroupRel"));
                knoxLicGroupDS.addSelectColumn(new Column((String)null, "*"));
                final Criteria dsID = new Criteria(new Column("KnoxLicenseDSToGroupRel", "KNOXSETTINGS_ID"), knoxLicenseDetails.get("KNOXSETTINGS_ID"), 0);
                final Criteria custID = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
                final Join join = new Join("KnoxLicenseDSToGroupRel", "Resource", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2);
                knoxLicGroupDS.addJoin(join);
                knoxLicGroupDS.setCriteria(dsID.and(custID));
                final DataObject dsDO = SyMUtil.getPersistence().get(knoxLicGroupDS);
                if (!dsDO.isEmpty()) {
                    final Iterator rowIt = dsDO.getRows("KnoxLicenseDSToGroupRel");
                    final Iterator rowResource = dsDO.getRows("Resource");
                    String dsGroupID = "";
                    String dsGroupName = "";
                    while (rowIt.hasNext()) {
                        final Row dsRow = rowIt.next();
                        final Row dsResource = rowResource.next();
                        dsGroupID = dsGroupID + dsRow.get("GROUP_ID") + ",";
                        dsGroupName = dsGroupName + dsResource.get("NAME") + ", ";
                    }
                    knoxDS.put("dsGroupID", dsGroupID);
                    knoxDS.put("dsGroupName", dsGroupName);
                }
            }
            return knoxDS;
        }
        catch (final Exception exp) {
            KnoxLicenseHandler.LOGGER.log(Level.SEVERE, null, exp);
            return knoxDS;
        }
    }
    
    private void addDefaultDSEntry(final Long customerId) throws Exception {
        final JSONObject row = new JSONObject();
        row.put((Object)"KNOXSETTINGS_OPTION", (Object)2);
        row.put((Object)"KNOXSETTINGS_TOGROUPONLY", (Object)false);
        row.put((Object)"OVERWRITE_EXISTING_CONTAINER", (Object)true);
        row.put((Object)"CUSTOMER_ID", (Object)customerId);
        this.addOrUpdateKnoxLicenseDS(row);
        KnoxLicenseHandler.LOGGER.log(Level.INFO, "Default distribution setting updated for customer {0}", customerId);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
        KnoxLicenseHandler.handler = new KnoxLicenseHandler();
    }
}
