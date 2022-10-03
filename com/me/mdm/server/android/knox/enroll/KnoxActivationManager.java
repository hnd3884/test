package com.me.mdm.server.android.knox.enroll;

import java.util.Hashtable;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccess;
import java.util.Iterator;
import com.me.mdm.server.alerts.MDMAlertConstants;
import java.util.Properties;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.logging.Level;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.android.knox.KnoxUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.HashMap;
import java.util.logging.Logger;

public class KnoxActivationManager
{
    String sourceClass;
    private static Logger logger;
    private static KnoxActivationManager knoxEnrollUtil;
    
    public KnoxActivationManager() {
        this.sourceClass = "MDMEnrollmentUtil";
    }
    
    public static KnoxActivationManager getInstance() {
        return KnoxActivationManager.knoxEnrollUtil;
    }
    
    public synchronized void processIamKnoxMsg(final HashMap<String, String> hmap) throws DataAccessException, JSONException {
        final String UUID = hmap.get("UDID");
        final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(UUID);
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
        final HashMap<String, String> msg = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject((String)hmap.get("CommandResponse")).getJSONObject("ResponseData"));
        final Integer knoxVersion = Integer.parseInt(msg.get("KnoxVersion"));
        int knoxAPILevel = 0;
        if (msg.containsKey("KnoxAPILevel")) {
            knoxAPILevel = Integer.parseInt(msg.get("KnoxAPILevel"));
        }
        final String src = msg.get("Source");
        if (!KnoxUtil.getInstance().isRegisteredAsKnox(resourceId)) {
            KnoxActivationManager.logger.info("Enrolled but not registered as Knox");
            KnoxUtil.getInstance().addOrUpdateManagedKnoxContainer(resourceId.toString(), knoxVersion, 20004, "dc.mdm.android.knox.container.notAvailable", knoxAPILevel);
        }
        if (src.equalsIgnoreCase("Enrollment")) {
            Long licenseId = null;
            try {
                licenseId = KnoxUtil.getInstance().getAssignedLicense(resourceId);
                if (licenseId == null) {
                    licenseId = this.getLicenseIdToSend(resourceId.toString(), DBUtil.getValueFromDB("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID", (Object)resourceId, "ENROLLMENT_REQUEST_ID").toString());
                    if (licenseId != null) {
                        final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
                        final Boolean isSuperVisedorAbove = ManagedDeviceHandler.getInstance().isSupervisedOr10Above(resourceId);
                        if (isSuperVisedorAbove != null && !isSuperVisedorAbove) {
                            if (this.decreaseLicenseCount(licenseId)) {
                                try {
                                    KnoxActivationManager.logger.info("Knox license key is available and going to assign and one count decremented");
                                    this.addOrUpdateKnoxDeviceToLicenseRel(resourceId, licenseId);
                                    KnoxUtil.getInstance().updateStatus(resourceId, 20000, "dc.mdm.android.knox.license.remarks.distributed", -1);
                                    DeviceInvCommandHandler.getInstance().SendCommandToContainer(deviceDetails, "ActivateKnox", null);
                                    final List resList = Arrays.asList(resourceId);
                                    NotificationHandler.getInstance().SendNotification(resList, 2);
                                }
                                catch (final Exception e) {
                                    KnoxActivationManager.logger.log(Level.SEVERE, "Exception while sending license so reverting back the count");
                                }
                            }
                            else {
                                KnoxUtil.getInstance().updateStatus(resourceId, 20004, "dc.mdm.android.knox.license.remarks.failed.qualityExhaust", -1);
                            }
                        }
                    }
                    else {
                        KnoxActivationManager.logger.info("License is not assigned by mean of license distribution settings");
                    }
                }
                else {
                    KnoxUtil.getInstance().updateStatus(resourceId, 20000, "dc.mdm.android.knox.license.remarks.distributed", -1);
                    final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
                    DeviceInvCommandHandler.getInstance().SendCommandToContainer(deviceDetails, "ActivateKnox", null);
                    final List resList2 = Arrays.asList(resourceId);
                    NotificationHandler.getInstance().SendNotification(resList2, 2);
                }
            }
            catch (final Exception ex) {
                Logger.getLogger(MDMEnrollmentUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        KnoxUtil.getInstance().updateKnoxAPILevel(resourceId, knoxAPILevel, knoxVersion);
    }
    
    public synchronized void addOrUpdateKnoxDeviceToLicenseRel(final Long resourceId, final Long licenseId) throws Exception {
        final SelectQuery relQuery = (SelectQuery)new SelectQueryImpl(new Table("KNOXDeviceToLicenseRel"));
        relQuery.setCriteria(new Criteria(new Column("KNOXDeviceToLicenseRel", "RESOURCE_ID"), (Object)resourceId, 0));
        relQuery.addSelectColumn(new Column("KNOXDeviceToLicenseRel", "LICENSE_ID"));
        relQuery.addSelectColumn(new Column("KNOXDeviceToLicenseRel", "RESOURCE_ID"));
        final DataObject dO = SyMUtil.getPersistence().get(relQuery);
        if (dO.isEmpty()) {
            final Row relRow = new Row("KNOXDeviceToLicenseRel");
            relRow.set("RESOURCE_ID", (Object)resourceId);
            relRow.set("LICENSE_ID", (Object)licenseId);
            relRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            dO.addRow(relRow);
            SyMUtil.getPersistence().update(dO);
            KnoxUtil.getInstance().updateStatus(resourceId, 20000, "dc.mdm.android.knox.license.remarks.distributed", 0);
            AppsUtil.getInstance().resetAppCatalog(resourceId, 1);
        }
        else {
            final Row relRow = dO.getFirstRow("KNOXDeviceToLicenseRel");
            relRow.set("LICENSE_ID", (Object)licenseId);
            relRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            dO.updateRow(relRow);
            SyMUtil.getPersistence().update(dO);
        }
        this.addOrUpdateKnoxDeviceToLicenseHistoryRel(resourceId, licenseId);
    }
    
    public synchronized void addOrUpdateKnoxDeviceToLicenseHistoryRel(final Long resourceId, final Long licenseId) throws Exception {
        final SelectQuery relQuery = (SelectQuery)new SelectQueryImpl(new Table("UnmanagedKNOXDevToLicRel"));
        relQuery.setCriteria(new Criteria(new Column("UnmanagedKNOXDevToLicRel", "RESOURCE_ID"), (Object)resourceId, 0));
        relQuery.addSelectColumn(new Column("UnmanagedKNOXDevToLicRel", "LICENSE_ID"));
        relQuery.addSelectColumn(new Column("UnmanagedKNOXDevToLicRel", "RESOURCE_ID"));
        final DataObject dO = SyMUtil.getPersistence().get(relQuery);
        if (dO.isEmpty()) {
            final Row relRow = new Row("UnmanagedKNOXDevToLicRel");
            relRow.set("RESOURCE_ID", (Object)resourceId);
            relRow.set("LICENSE_ID", (Object)licenseId);
            relRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            dO.addRow(relRow);
        }
        else {
            final Row relRow = dO.getFirstRow("UnmanagedKNOXDevToLicRel");
            relRow.set("LICENSE_ID", (Object)licenseId);
            relRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            dO.updateRow(relRow);
        }
        SyMUtil.getPersistence().update(dO);
    }
    
    private synchronized boolean decreaseLicenseCount(final Long licenseId) throws DataAccessException, Exception {
        final SelectQuery licenseQuery = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDetail"));
        licenseQuery.addSelectColumn(new Column("KnoxLicenseDetail", "LICENSE_ID"));
        licenseQuery.addSelectColumn(new Column("KnoxLicenseDetail", "MAX_COUNT"));
        licenseQuery.addSelectColumn(new Column("KnoxLicenseDetail", "CUSTOMER_ID"));
        licenseQuery.setCriteria(new Criteria(new Column("KnoxLicenseDetail", "LICENSE_ID"), (Object)licenseId, 0));
        final DataObject dO = SyMUtil.getPersistence().get(licenseQuery);
        final Row licenseDetail = dO.getFirstRow("KnoxLicenseDetail");
        final int iUsedCount = KnoxUtil.getInstance().getUsedLicenseCount(licenseId);
        final int iMaxCount = (int)licenseDetail.get("MAX_COUNT");
        final int iRemainingLicenseCount = iMaxCount - iUsedCount;
        if (iRemainingLicenseCount == 10 || iRemainingLicenseCount == 5 || iRemainingLicenseCount == 2 || iRemainingLicenseCount == 1) {
            MessageProvider.getInstance().unhideMessage("KNOX_COUNT_EXHAUSTING", (Long)licenseDetail.get("CUSTOMER_ID"));
            MessageProvider.getInstance().hideMessage("KNOX_COUNT_EXHAUSTED", (Long)licenseDetail.get("CUSTOMER_ID"));
            final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(KnoxActivationManager.logger);
            final Properties prop = new Properties();
            ((Hashtable<String, Integer>)prop).put("remaining_license_count", iRemainingLicenseCount);
            mailGenerator.sendMail(MDMAlertConstants.MDM_ANDROID_KNOX_LICENSE_ABOUT_TO_EXHAUST, "MDM-Knox", (Long)licenseDetail.get("CUSTOMER_ID"), prop);
        }
        if (iRemainingLicenseCount == 0) {
            MessageProvider.getInstance().hideMessage("KNOX_COUNT_EXHAUSTING", (Long)licenseDetail.get("CUSTOMER_ID"));
            MessageProvider.getInstance().unhideMessage("KNOX_COUNT_EXHAUSTED", (Long)licenseDetail.get("CUSTOMER_ID"));
            final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(KnoxActivationManager.logger);
            final Properties prop = new Properties();
            mailGenerator.sendMail(MDMAlertConstants.MDM_ANDROID_KNOX_LICENSE_EXHAUSTED, "MDM-Knox", (Long)licenseDetail.get("CUSTOMER_ID"), prop);
        }
        return iUsedCount < iMaxCount;
    }
    
    public synchronized boolean associateLicense(final Long resourceId, final Long licenseId) throws Exception {
        try {
            if (!KnoxUtil.getInstance().isRegisteredAsKnox(resourceId)) {
                KnoxActivationManager.logger.log(Level.INFO, "License is not associated to resource : {0} since it is not a Knox device", resourceId);
                return false;
            }
            final Boolean isSuperVisedorAbove = ManagedDeviceHandler.getInstance().isSupervisedOr10Above(resourceId);
            if (isSuperVisedorAbove == null || isSuperVisedorAbove) {
                KnoxUtil.getInstance().updateStatus(resourceId, 20004, "dc.mdm.android.knox.license.remarks.failed.internalServer", -1);
                return false;
            }
            if (this.decreaseLicenseCount(licenseId)) {
                this.addOrUpdateKnoxDeviceToLicenseRel(resourceId, licenseId);
                KnoxUtil.getInstance().updateStatus(resourceId, 20000, "dc.mdm.android.knox.license.remarks.distributed", -1);
                return true;
            }
            KnoxUtil.getInstance().updateStatus(resourceId, 20004, "dc.mdm.android.knox.license.remarks.failed.qualityExhaust", -1);
            return false;
        }
        catch (final Exception e) {
            KnoxActivationManager.logger.log(Level.WARNING, "Exception while associating License", e);
            throw e;
        }
    }
    
    private synchronized Long getLicenseIdToSend(final String resourceId, final String enrollmentReqId) throws DataAccessException, Exception {
        if (!KnoxUtil.getInstance().isKnoxLicenseAvailable(CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(Long.parseLong(resourceId))))) {
            return null;
        }
        if (KnoxUtil.getInstance().getAssignedLicenseFromHistory(Long.parseLong(resourceId)) != null) {
            return KnoxUtil.getInstance().getAssignedLicenseFromHistory(Long.parseLong(resourceId));
        }
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("KNOXDistributionSettings"));
        sQuery.setCriteria(new Criteria(new Column("KNOXDistributionSettings", "CUSTOMER_ID"), (Object)CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(Long.parseLong(resourceId))), 0));
        sQuery.addSelectColumn(new Column("KNOXDistributionSettings", "*"));
        final DataObject dO = SyMUtil.getPersistence().get(sQuery);
        if (dO.isEmpty()) {
            return null;
        }
        final Row distSettingRow = dO.getFirstRow("KNOXDistributionSettings");
        final Integer option = (Integer)distSettingRow.get("KNOXSETTINGS_OPTION");
        if (option == 2) {
            return (Long)DBUtil.getValueFromDB("EnrollmentRequestToKnoxRel", "ENROLLMENT_REQUEST_ID", (Object)enrollmentReqId, "KNOX_LICENSE_ID");
        }
        final SelectQuery knoxLicenseDSQuery = (SelectQuery)new SelectQueryImpl(new Table("KNOXDistributionSettings"));
        knoxLicenseDSQuery.addSelectColumn(new Column("KNOXDistributionSettings", "*"));
        knoxLicenseDSQuery.setCriteria(new Criteria(new Column("KNOXDistributionSettings", "CUSTOMER_ID"), (Object)CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(Long.parseLong(resourceId))), 0));
        final DataObject knoxDSDO = SyMUtil.getPersistence().get(knoxLicenseDSQuery);
        final Row knoxDSRow = knoxDSDO.getFirstRow("KNOXDistributionSettings");
        final Long dsId = (Long)knoxDSRow.get("KNOXSETTINGS_ID");
        final Boolean toGroupOnly = (Boolean)knoxDSRow.get("KNOXSETTINGS_TOGROUPONLY");
        if (!toGroupOnly) {
            return KnoxUtil.getInstance().getLicenseId(CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(Long.parseLong(resourceId))));
        }
        final SelectQuery memberToGroupQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomGroupMemberRel"));
        memberToGroupQuery.addSelectColumn(new Column("CustomGroupMemberRel", "*"));
        memberToGroupQuery.setCriteria(new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceId, 0));
        final DataObject groupRelDO = SyMUtil.getPersistence().get(memberToGroupQuery);
        final Iterator it = groupRelDO.getRows("CustomGroupMemberRel");
        final SelectQuery dsToGroupQuery = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDSToGroupRel"));
        final Criteria groupIdCriteria = new Criteria(new Column("KnoxLicenseDSToGroupRel", "GROUP_ID"), (Object)DBUtil.getColumnValues(it, "GROUP_RESOURCE_ID"), 8);
        final Criteria dsIdCriteria = new Criteria(new Column("KnoxLicenseDSToGroupRel", "KNOXSETTINGS_ID"), (Object)dsId, 0);
        dsToGroupQuery.addSelectColumn(new Column("KnoxLicenseDSToGroupRel", "*"));
        dsToGroupQuery.setCriteria(dsIdCriteria.and(groupIdCriteria));
        final DataObject dsToGroupDO = SyMUtil.getPersistence().get(dsToGroupQuery);
        if (dsToGroupDO.isEmpty()) {
            return null;
        }
        return KnoxUtil.getInstance().getLicenseId(CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(Long.parseLong(resourceId))));
    }
    
    public synchronized Boolean getOverrideContainerFromDS(final Long customerId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("KNOXDistributionSettings"));
        sQuery.addSelectColumn(new Column("KNOXDistributionSettings", "*"));
        sQuery.setCriteria(new Criteria(new Column("KNOXDistributionSettings", "CUSTOMER_ID"), (Object)customerId, 0));
        final DataObject dO = SyMUtil.getPersistence().get(sQuery);
        if (dO.isEmpty()) {
            return Boolean.FALSE;
        }
        return (Boolean)dO.getFirstRow("KNOXDistributionSettings").get("OVERWRITE_EXISTING_CONTAINER");
    }
    
    public synchronized void removeAssociatedLicense(final Long resourceId) throws Exception {
        final Long licenseId = KnoxUtil.getInstance().getAssignedLicense(resourceId);
        if (licenseId != null) {
            Criteria criteria = new Criteria(new Column("KNOXDeviceToLicenseRel", "RESOURCE_ID"), (Object)resourceId, 0);
            DataAccess.delete("KNOXDeviceToLicenseRel", criteria);
            criteria = new Criteria(new Column("UnmanagedKNOXDevToLicRel", "RESOURCE_ID"), (Object)resourceId, 0);
            DataAccess.delete("UnmanagedKNOXDevToLicRel", criteria);
        }
    }
    
    public void removeAssociatedLicenseButPreserveHistory(final Long resourceId) throws Exception {
        final Long licenseId = KnoxUtil.getInstance().getAssignedLicense(resourceId);
        if (licenseId != null) {
            final Criteria criteria = new Criteria(new Column("KNOXDeviceToLicenseRel", "RESOURCE_ID"), (Object)resourceId, 0);
            DataAccess.delete("KNOXDeviceToLicenseRel", criteria);
        }
    }
    
    public void addEnrollmentReqToKnoxRel(final Long lEnrollmentId, final Long sKnoxLicenseKey) throws DataAccessException {
        final DataObject dO = (DataObject)new WritableDataObject();
        final Row relRow = new Row("EnrollmentRequestToKnoxRel");
        relRow.set("ENROLLMENT_REQUEST_ID", (Object)lEnrollmentId);
        relRow.set("KNOX_LICENSE_ID", (Object)sKnoxLicenseKey);
        dO.addRow(relRow);
        SyMUtil.getPersistence().update(dO);
    }
    
    public void deleteKNOXToEnrollmentRel(final Long resourceId) {
        try {
            final Long enrollmentId = (Long)DBUtil.getValueFromDB("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID", (Object)resourceId, "ENROLLMENT_REQUEST_ID");
            final Criteria criteria = new Criteria(Column.getColumn("EnrollmentRequestToKnoxRel", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentId, 0);
            DataAccess.delete(criteria);
        }
        catch (final Exception exp) {
            KnoxActivationManager.logger.warning("Exception occured on deleteing entry at enrollmentrequesttoknoxrel for ResourceId : " + resourceId);
        }
    }
    
    static {
        KnoxActivationManager.logger = Logger.getLogger("MDMEnrollment");
        KnoxActivationManager.knoxEnrollUtil = new KnoxActivationManager();
    }
}
