package com.me.mdm.core.enrollment;

import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.HashMap;
import java.util.Date;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.Set;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.WritableDataObject;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.common.MDMEventConstant;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Level;
import com.me.mdm.core.enrollment.settings.UserAssignmentRuleHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentConstants;
import com.me.mdm.server.adep.DEPConstants;
import com.me.mdm.server.adep.DEPEnrollmentUtil;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.logging.Logger;
import com.adventnet.persistence.DataObject;

public class DeviceForEnrollmentHandler
{
    protected DataObject existingDO;
    public static Logger logger;
    protected String deviceForEnrollmentChildTableName;
    
    public DeviceForEnrollmentHandler() {
        this.existingDO = null;
        this.deviceForEnrollmentChildTableName = null;
    }
    
    public void processDEPDeviceList(final JSONArray deviceArray, final JSONObject tokenDetailsJSON, final boolean fetch) throws Exception {
        this.deviceForEnrollmentChildTableName = "AppleDEPDeviceForEnrollment";
        final Long customerId = tokenDetailsJSON.getLong("CUSTOMER_ID");
        final Long tokenId = (Long)tokenDetailsJSON.get("DEP_TOKEN_ID");
        this.addOrUpdateDFEAndDEPDFE(deviceArray, customerId, tokenId, fetch);
        try {
            final JSONArray assignUserArray = new JSONArray();
            for (int i = 0; i < deviceArray.length(); ++i) {
                final JSONObject deviceJSON = deviceArray.getJSONObject(i);
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("SerialNumber", (Object)deviceJSON.getString("serial_number"));
                final JSONObject additionalContext = new JSONObject();
                final int deviceFamily = (DEPEnrollmentUtil.getDeviceFamily(deviceJSON.optString("device_family", "--")) == DEPConstants.DFEModel.MAC) ? MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MODERN_DEVICE : MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MOBILE_DEVICE;
                additionalContext.put("device_model", deviceFamily);
                jsonObject.put("additional_context", (Object)additionalContext);
                assignUserArray.put((Object)jsonObject);
            }
            MDMApiFactoryProvider.getMDMUtilAPI().addAutoUserAssignRule(tokenDetailsJSON);
            new UserAssignmentRuleHandler().applyAssignUserRules(assignUserArray, DEPEnrollmentUtil.getTemplateIDTokenForTokenID(tokenId));
        }
        catch (final Exception e) {
            DeviceForEnrollmentHandler.logger.log(Level.WARNING, "Auto user assignment threw an exception for dep devices : ", e);
        }
    }
    
    protected void deleteDEPCommonDevicesFromMacModernMgmtDO() {
        try {
            final Iterator depDevicesIterator = this.existingDO.getRows("AppleDEPDeviceForEnrollment");
            if (depDevicesIterator != null && depDevicesIterator.hasNext()) {
                final List<Long> dfeIDs = DBUtil.getColumnValuesAsList(depDevicesIterator, "ENROLLMENT_DEVICE_ID");
                final Criteria macModernDeleteCri = new Criteria(new Column("MacModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)dfeIDs.toArray(), 8);
                this.existingDO.deleteRows("MacModernMgmtDeviceForEnrollment", macModernDeleteCri);
                DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Not adding MACMODERNMGMTDEVICEFORENROLLMENT for DFE{0} since they already exists in DEP", dfeIDs);
            }
        }
        catch (final DataAccessException e) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception while deleteDEPevicesFromMacModernMgmtDO ", (Throwable)e);
        }
    }
    
    public Long addDeviceForEnrollment(final JSONObject dataJSON, final int type) throws SyMException, DataAccessException, JSONException {
        final String imei = dataJSON.optString("IMEI", (String)null);
        final String serialNo = dataJSON.optString("SerialNumber", (String)null);
        final String easID = dataJSON.optString("EASID", (String)null);
        final Long customerId = dataJSON.optLong("CustomerId");
        final String udid = dataJSON.optString("UDID", (String)null);
        dataJSON.put("template_type", type);
        final Long deviceForEnrollmentId = this.addDeviceToRepo(dataJSON);
        String addedToRepo = null;
        switch (type) {
            case 20: {
                this.addAndroidNFCDeviceForEnrollment(deviceForEnrollmentId);
                addedToRepo = "Android NFC Enrollment";
                this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MOBILE_DEVICE);
                break;
            }
            case 50: {
                this.addMigrationDeviceForEnrollment(deviceForEnrollmentId);
                addedToRepo = "Migration Enrollment";
                break;
            }
            case 21: {
                this.addKNOXDeviceForEnrollment(deviceForEnrollmentId);
                addedToRepo = "KNOX Mobile Enrollment";
                this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MOBILE_DEVICE);
                break;
            }
            case 10: {
                final String templateToken = dataJSON.optString("TEMPLATE_TOKEN");
                Long depTokenID = null;
                if (templateToken != null && !MDMStringUtils.isEmpty(templateToken)) {
                    depTokenID = DEPEnrollmentUtil.getDEPTokenIDForEnrollmentTemplateToken(templateToken);
                    if (depTokenID != null) {
                        this.addDEPDeviceForEnrollment(deviceForEnrollmentId, depTokenID);
                        final List<Long> dfeList = new ArrayList<Long>();
                        dfeList.add(deviceForEnrollmentId);
                        this.addDeviceForEnrollmentToCustomGroup(dfeList, DEPEnrollmentUtil.createNewCustomGroupForDEPToken(depTokenID));
                        EnrollmentTemplateHandler.addOrUpdateTemplateToDeviceForEnrollment(dfeList, Long.valueOf(new EnrollmentTemplateHandler().getEnrollmentTemplateForTemplateToken(templateToken).get("TEMPLATE_ID").toString()));
                    }
                }
                addedToRepo = "Apple DEP";
                final int modelType = dataJSON.optInt("DeviceType", -1);
                DeviceForEnrollmentHandler.logger.log(Level.INFO, "Adding Onboard rule for DEP device with model {0}", modelType);
                if (modelType == 4 || modelType == 3) {
                    this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MODERN_DEVICE);
                    break;
                }
                if (modelType != -1) {
                    this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MOBILE_DEVICE);
                    break;
                }
                break;
            }
            case 11: {
                this.addAppleConfigDeviceForEnrollment(deviceForEnrollmentId, JSONUtil.optLong(dataJSON, "MANAGED_USER_ID", -1L));
                this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MOBILE_DEVICE);
                addedToRepo = "Apple Configurator Enrollment";
                break;
            }
            case 30: {
                this.addWindowsWICDDeviceForEnrollment(deviceForEnrollmentId);
                this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MODERN_DEVICE);
                addedToRepo = "Windows WICD Enrollment";
                break;
            }
            case 22: {
                this.addAndroidQRDeviceForEnrollment(deviceForEnrollmentId, JSONUtil.optLong(dataJSON, "MANAGED_USER_ID", -1L));
                addedToRepo = "Android QR Enrollment";
                this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MODERN_DEVICE);
                break;
            }
            case 31: {
                this.addWindowsLaptopDeviceForEnrollment(deviceForEnrollmentId);
                addedToRepo = "Windows Laptop Enrollment";
                this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MODERN_DEVICE);
                break;
            }
            case 23: {
                this.addAndroidZTDeviceForEnrollment(deviceForEnrollmentId);
                addedToRepo = "Android ZT Enrollment";
                this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MOBILE_DEVICE);
                break;
            }
            case 40: {
                this.addGSuiteChromeDeviceForEnrollment(deviceForEnrollmentId);
                addedToRepo = "GSuite - Chrome Device Enrollment";
                this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MODERN_DEVICE);
                break;
            }
            case 32: {
                this.addWindowsAzureADDeviceForEnrollment(deviceForEnrollmentId);
                addedToRepo = "Windows Azure AD Enrollment";
                this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MODERN_DEVICE);
                break;
            }
            case 33: {
                this.addWindowsModernMgmtDeviceForEnrollment(deviceForEnrollmentId);
                addedToRepo = "Windows Modern Mgmt Enrollment";
                this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MODERN_DEVICE);
                break;
            }
            case 12: {
                this.addModernMgmtDeviceForEnrollment(deviceForEnrollmentId);
                addedToRepo = "Modern Mac Device Enrollment";
                this.addModelTypeToDataJSON(dataJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MODERN_DEVICE);
                break;
            }
        }
        final JSONArray resourceArray = new JSONArray();
        resourceArray.put((Object)dataJSON);
        try {
            if (!dataJSON.optBoolean("SkipUserAssignmentAutomation", (boolean)Boolean.FALSE)) {
                if (dataJSON.optString("TEMPLATE_TOKEN", (String)null) != null) {
                    new UserAssignmentRuleHandler().applyAssignUserRules(resourceArray, dataJSON.optString("TEMPLATE_TOKEN", (String)null));
                }
                else {
                    dataJSON.put("customer_id", (Object)customerId);
                    new UserAssignmentRuleHandler().applyAssignUserRules(resourceArray, dataJSON.optInt("template_type", -1));
                }
            }
        }
        catch (final Exception e) {
            DeviceForEnrollmentHandler.logger.log(Level.WARNING, "Auto user assignment threw an exception while adding DFE entry: ", e);
        }
        final String remarksArg = ((imei == null) ? "--" : imei) + "@@@" + ((serialNo == null) ? "--" : serialNo) + "@@@" + ((udid == null) ? "--" : udid) + "@@@" + addedToRepo;
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, MDMEventConstant.DC_SYSTEM_USER, "dc.mdm.actionlog.enrollment.device_added_to_repo", remarksArg, customerId);
        return deviceForEnrollmentId;
    }
    
    private void addWindowsModernMgmtDeviceForEnrollment(final Long deviceForEnrollmentId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("WinModernMgmtDeviceForEnrollment"));
        sQuery.setCriteria(new Criteria(new Column("WinModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("WinModernMgmtDeviceForEnrollment");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            DO.addRow(row);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    private void addMigrationDeviceForEnrollment(final Long deviceForEnrollmentId) throws DataAccessException {
        final Row row = new Row("MigrationDeviceForEnrollment");
        row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
        final DataObject dataObject = DataAccess.get("MigrationDeviceForEnrollment", row);
        if (dataObject.isEmpty()) {
            dataObject.addRow(row);
            MDMUtil.getPersistence().add(dataObject);
        }
    }
    
    private void addDEPDeviceForEnrollment(final Long deviceForEnrollmentId, final Long tokenID) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppleDEPDeviceForEnrollment"));
        sQuery.setCriteria(new Criteria(new Column("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("AppleDEPDeviceForEnrollment");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            row.set("DEP_TOKEN_ID", (Object)tokenID);
            DO.addRow(row);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    private void addAppleConfigDeviceForEnrollment(final Long deviceForEnrollmentId, final Long managedUserID) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppleConfigDeviceForEnrollment"));
        sQuery.addJoin(new Join("AppleConfigDeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        sQuery.setCriteria(new Criteria(new Column("AppleConfigDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("AppleConfigDeviceForEnrollment");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            DO.addRow(row);
            DO = this.autoAssignAdminEnrollmentDevices(DO, deviceForEnrollmentId, managedUserID);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    private void addKNOXDeviceForEnrollment(final Long deviceForEnrollmentId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("KNOXMobileDeviceForEnrollment"));
        sQuery.setCriteria(new Criteria(new Column("KNOXMobileDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("KNOXMobileDeviceForEnrollment");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            DO.addRow(row);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    private void addAndroidNFCDeviceForEnrollment(final Long deviceForEnrollmentId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidNFCDeviceForEnrollment"));
        sQuery.setCriteria(new Criteria(new Column("AndroidNFCDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("AndroidNFCDeviceForEnrollment");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            DO.addRow(row);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    private void addWindowsWICDDeviceForEnrollment(final Long deviceForEnrollmentId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("WindowsICDDeviceForEnrollment"));
        sQuery.setCriteria(new Criteria(new Column("WindowsICDDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("WindowsICDDeviceForEnrollment");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            DO.addRow(row);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    private void addAndroidQRDeviceForEnrollment(final Long deviceForEnrollmentId, final Long managedUserID) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidQRDeviceForEnrollment"));
        sQuery.setCriteria(new Criteria(new Column("AndroidQRDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("AndroidQRDeviceForEnrollment");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            DO.addRow(row);
            DO = this.autoAssignAdminEnrollmentDevices(DO, deviceForEnrollmentId, managedUserID);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    private void addWindowsLaptopDeviceForEnrollment(final Long deviceForEnrollmentId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("WindowsLaptopDeviceForEnrollment"));
        sQuery.setCriteria(new Criteria(new Column("WindowsLaptopDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("WindowsLaptopDeviceForEnrollment");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            DO.addRow(row);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    private void addAndroidZTDeviceForEnrollment(final Long deviceForEnrollmentId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidZTDeviceForEnrollment"));
        sQuery.setCriteria(new Criteria(new Column("AndroidZTDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("AndroidZTDeviceForEnrollment");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            DO.addRow(row);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    private void addGSuiteChromeDeviceForEnrollment(final Long deviceForEnrollmentId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("GSChromeDeviceForEnrollment"));
        sQuery.setCriteria(new Criteria(new Column("GSChromeDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("GSChromeDeviceForEnrollment");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            DO.addRow(row);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    private void addWindowsAzureADDeviceForEnrollment(final Long deviceForEnrollmentId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("WinAzureADDeviceForEnrollment"));
        sQuery.setCriteria(new Criteria(new Column("WinAzureADDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("WinAzureADDeviceForEnrollment");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            DO.addRow(row);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    private void addModernMgmtDeviceForEnrollment(final Long deviceForEnrollmentId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MacModernMgmtDeviceForEnrollment"));
        sQuery.setCriteria(new Criteria(new Column("MacModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("MacModernMgmtDeviceForEnrollment");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            DO.addRow(row);
            MDMUtil.getPersistence().add(DO);
        }
    }
    
    public Long addDeviceToRepo(final JSONObject dataJSON) throws SyMException, DataAccessException, JSONException {
        String imei = dataJSON.optString("IMEI");
        final String serialNo = dataJSON.optString("SerialNumber");
        final String easID = dataJSON.optString("EASID", (String)null);
        final String udid = dataJSON.optString("UDID");
        final Long customerId = dataJSON.optLong("CustomerId");
        final int enrollmentType = dataJSON.getInt("template_type");
        if (MDMStringUtils.isEmpty(imei) && MDMStringUtils.isEmpty(serialNo) && MDMStringUtils.isEmpty(udid) && MDMStringUtils.isEmpty(easID)) {
            throw new SyMException(14020, "Either of IMEI / Serial number / UDID / Exchange ID must be specified", (Throwable)null);
        }
        if (customerId == null || customerId == 0L) {
            throw new SyMException(51200, "Customer ID must be specified", (Throwable)null);
        }
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
        sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        Criteria criteria = null;
        final Boolean allowDuplicateSerialNumber = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ALLOW_DUPLICATE_SERIAL_NUMBER");
        if (!MDMStringUtils.isEmpty(serialNo) && (enrollmentType == 10 || !allowDuplicateSerialNumber)) {
            criteria = new Criteria(new Column("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)serialNo, 0, false);
        }
        if (!MDMStringUtils.isEmpty(imei)) {
            imei = imei.replace(" ", "");
            if (criteria == null) {
                criteria = new Criteria(new Column("DeviceForEnrollment", "IMEI"), (Object)imei, 0, false);
            }
            else {
                criteria = criteria.or(new Criteria(new Column("DeviceForEnrollment", "IMEI"), (Object)imei, 0, false));
            }
        }
        if (!MDMStringUtils.isEmpty(udid)) {
            final Criteria udidCriteria = new Criteria(new Column("DeviceForEnrollment", "UDID"), (Object)udid, 0, false);
            if (criteria == null) {
                criteria = udidCriteria;
            }
            else {
                criteria = criteria.or(udidCriteria);
            }
        }
        if (!MDMStringUtils.isEmpty(easID)) {
            if (criteria == null) {
                criteria = new Criteria(new Column("DeviceForEnrollment", "EAS_DEVICE_IDENTIFIER"), (Object)easID, 0, false);
            }
            else {
                criteria = criteria.or(new Criteria(new Column("DeviceForEnrollment", "EAS_DEVICE_IDENTIFIER"), (Object)easID, 0, false));
            }
        }
        sQuery.setCriteria(criteria);
        DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            DO = (DataObject)new WritableDataObject();
            final Row row = new Row("DeviceForEnrollment");
            row.set("IMEI", (Object)imei);
            row.set("SERIAL_NUMBER", (Object)serialNo);
            row.set("UDID", (Object)udid);
            row.set("EAS_DEVICE_IDENTIFIER", (Object)easID);
            row.set("CUSTOMER_ID", (Object)customerId);
            row.set("ADDED_TIME", (Object)MDMUtil.getCurrentTime());
            row.set("UPDATED_TIME", (Object)MDMUtil.getCurrentTime());
            DO.addRow(row);
        }
        else {
            final Row row = DO.getFirstRow("DeviceForEnrollment");
            row.set("UPDATED_TIME", (Object)MDMUtil.getCurrentTime());
            DO.updateRow(row);
        }
        DO = MDMUtil.getPersistence().update(DO);
        return (Long)DO.getFirstValue("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID");
    }
    
    public void addOrUpdateUserForDevice(final Long deviceForEnrollmentId, final Long userId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentToUser"));
        final Criteria deviceIDCriteria = new Criteria(new Column("DeviceEnrollmentToUser", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0);
        sQuery.setCriteria(deviceIDCriteria);
        sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("DeviceEnrollmentToUser");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            row.set("MANAGED_USER_ID", (Object)userId);
            DO.addRow(row);
        }
        else {
            final Row row = DO.getFirstRow("DeviceEnrollmentToUser");
            row.set("MANAGED_USER_ID", (Object)userId);
            DO.updateRow(row);
        }
        MDMUtil.getPersistence().update(DO);
    }
    
    public void addOrUpdateGroupForDevice(final Long deviceForEnrollmentId, final List<Long> groupIdList) throws DataAccessException {
        if (groupIdList != null && groupIdList.size() > 0) {
            final List<Long> listToDelete = new ArrayList<Long>();
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentToGroup"));
            sQuery.addJoin(new Join("DeviceEnrollmentToGroup", "CustomGroup", new String[] { "ASSOCIATED_GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria deviceIDCriteria = new Criteria(new Column("DeviceEnrollmentToGroup", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0);
            final Criteria hiddenGroupCriteria = new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)8, 1);
            sQuery.setCriteria(deviceIDCriteria.and(hiddenGroupCriteria));
            sQuery.addSelectColumn(new Column("DeviceEnrollmentToGroup", "ASSOCIATED_GROUP_ID"));
            sQuery.addSelectColumn(new Column("DeviceEnrollmentToGroup", "ENROLLMENT_DEVICE_ID"));
            final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
            if (DO.isEmpty()) {
                for (final Long gid : groupIdList) {
                    final Row row = new Row("DeviceEnrollmentToGroup");
                    row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
                    row.set("ASSOCIATED_GROUP_ID", (Object)gid);
                    DO.addRow(row);
                }
            }
            else {
                final Iterator iterator = DO.getRows("DeviceEnrollmentToGroup");
                while (iterator.hasNext()) {
                    final Row row2 = iterator.next();
                    final Long gid2 = (Long)row2.get("ASSOCIATED_GROUP_ID");
                    if (groupIdList.contains(gid2)) {
                        groupIdList.remove(gid2);
                    }
                    else {
                        listToDelete.add(gid2);
                    }
                }
                final Iterator<Long> iterator3 = groupIdList.iterator();
                while (iterator3.hasNext()) {
                    final Long gid2 = iterator3.next();
                    final Row row3 = new Row("DeviceEnrollmentToGroup");
                    row3.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
                    row3.set("ASSOCIATED_GROUP_ID", (Object)gid2);
                    DO.addRow(row3);
                }
                if (!listToDelete.isEmpty()) {
                    DO.deleteRows("DeviceEnrollmentToGroup", new Criteria(Column.getColumn("DeviceEnrollmentToGroup", "ASSOCIATED_GROUP_ID"), (Object)listToDelete.toArray(), 8));
                }
            }
            MDMUtil.getPersistence().update(DO);
        }
        else {
            final DeleteQuery delQ = (DeleteQuery)new DeleteQueryImpl("DeviceEnrollmentToGroup");
            delQ.addJoin(new Join("DeviceEnrollmentToGroup", "CustomGroup", new String[] { "ASSOCIATED_GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria hiddenGroupCriteria2 = new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)8, 1);
            final Criteria deviceIDCriteria = new Criteria(new Column("DeviceEnrollmentToGroup", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0);
            delQ.setCriteria(hiddenGroupCriteria2.and(deviceIDCriteria));
            MDMUtil.getPersistence().delete(delQ);
        }
    }
    
    public void addOrUpdatePropsForDevice(final Long deviceForEnrollmentId, final JSONObject props) throws DataAccessException, JSONException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentProps"));
        final Criteria deviceIDCriteria = new Criteria(new Column("DeviceEnrollmentProps", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0);
        sQuery.setCriteria(deviceIDCriteria);
        sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("DeviceEnrollmentProps");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            row.set("ASSIGNED_DEVICE_NAME", (Object)String.valueOf(props.get("ASSIGNED_DEVICE_NAME")));
            DO.addRow(row);
        }
        else {
            final Row row = DO.getFirstRow("DeviceEnrollmentProps");
            row.set("ASSIGNED_DEVICE_NAME", (Object)String.valueOf(props.get("ASSIGNED_DEVICE_NAME")));
            DO.updateRow(row);
        }
        MDMUtil.getPersistence().update(DO);
    }
    
    public void addOrUpdateRequestForDevice(final Long deviceForEnrollmentId, final Long enrollmentRequestId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentToRequest"));
        final Criteria deviceIDCriteria = new Criteria(new Column("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0);
        sQuery.setCriteria(deviceIDCriteria);
        sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        if (DO.isEmpty()) {
            final Row row = new Row("DeviceEnrollmentToRequest");
            row.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            row.set("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestId);
            DO.addRow(row);
        }
        else {
            final Row row = DO.getFirstRow("DeviceEnrollmentToRequest");
            row.set("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestId);
            DO.updateRow(row);
        }
        MDMUtil.getPersistence().update(DO);
    }
    
    protected DataObject getExistingDO(final Set columnValues, final String criteriaColumn, final Long customerId) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
        sQuery.addJoin(new Join("DeviceForEnrollment", this.deviceForEnrollmentChildTableName, new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentProps", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        Criteria columnCriteria = new Criteria(new Column("DeviceForEnrollment", criteriaColumn), (Object)columnValues.toArray(), 8);
        if (customerId != null) {
            columnCriteria = columnCriteria.and(new Criteria(new Column("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerId, 0));
        }
        sQuery.setCriteria(columnCriteria);
        sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        return MDMUtil.getPersistence().get(sQuery);
    }
    
    protected DataObject getExistingManagedDeviceDOUsingSerialNumberOrUDID(final Set columnValues, final String criteriaColumn) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        Criteria columnCriteria = null;
        if (criteriaColumn.equalsIgnoreCase("SERIAL_NUMBER")) {
            columnCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)columnValues.toArray(), 8);
        }
        else if (criteriaColumn.equalsIgnoreCase("UDID")) {
            columnCriteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)columnValues.toArray(), 8);
        }
        else {
            columnCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "GENERIC_IDENTIFIER"), (Object)columnValues.toArray(), 8);
        }
        sQuery.setCriteria(columnCriteria);
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "GENERIC_IDENTIFIER"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
        final DataObject managedDeviceDO = MDMUtil.getPersistence().get(sQuery);
        return managedDeviceDO;
    }
    
    private void addOrUpdateDFEAndDEPDFE(final JSONArray deviceArray, final Long customerId, final Long tokenID, final boolean fetch) throws Exception {
        int added = 0;
        int updated = 0;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
        sQuery.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        Criteria tokenCriteria = new Criteria(new Column("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerId, 0);
        tokenCriteria = tokenCriteria.and(new Criteria(new Column("AppleDEPDeviceForEnrollment", "DEP_TOKEN_ID"), (Object)tokenID, 0));
        sQuery.setCriteria(tokenCriteria);
        sQuery.addSelectColumn(Column.getColumn("AppleDEPDeviceForEnrollment", "*"));
        sQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "*"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        for (int i = 0; i < deviceArray.length(); ++i) {
            final JSONObject deviceJson = deviceArray.getJSONObject(i);
            final String serialNum = deviceJson.optString("serial_number");
            Row dfeRow = null;
            if (serialNum == null || serialNum.isEmpty()) {
                throw new SyMException(14020, "Either of IMEI or Serial number must be specified", "dc.mdm.msg.inv.bulk_edit.no_imei_slno", (Throwable)null);
            }
            final Criteria cSerial = new Criteria(new Column("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)serialNum, 0);
            dfeRow = DO.getRow("DeviceForEnrollment", cSerial);
            Row depDfeRow = null;
            Long edid = null;
            if (dfeRow == null) {
                dfeRow = new Row("DeviceForEnrollment");
                dfeRow.set("CUSTOMER_ID", (Object)customerId);
                dfeRow.set("SERIAL_NUMBER", (Object)serialNum);
                dfeRow.set("ADDED_TIME", (Object)MDMUtil.getCurrentTime());
                if (fetch) {
                    dfeRow.set("UPDATED_TIME", (Object)MDMUtil.getCurrentTime());
                }
                DO.addRow(dfeRow);
                depDfeRow = new Row("AppleDEPDeviceForEnrollment");
                depDfeRow.set("ENROLLMENT_DEVICE_ID", dfeRow.get("ENROLLMENT_DEVICE_ID"));
                depDfeRow.set("DEP_TOKEN_ID", (Object)tokenID);
                if (!fetch) {
                    final String opDateStr = deviceJson.optString("op_date");
                    final Date opDate = MDMEnrollmentUtil.getInstance().getDateInStandardFormat(opDateStr);
                    depDfeRow.set("OP_DATE", (Object)opDate.getTime());
                }
                depDfeRow.set("PROFILE_UUID", (Object)"--");
                final boolean isAdd = true;
            }
            else {
                if (dfeRow.get("ENROLLMENT_DEVICE_ID") instanceof UniqueValueHolder) {
                    continue;
                }
                if (fetch) {
                    dfeRow.set("UPDATED_TIME", (Object)MDMUtil.getCurrentTime());
                    DO.updateRow(dfeRow);
                }
                edid = (Long)dfeRow.get("ENROLLMENT_DEVICE_ID");
                final Criteria cTemplate = new Criteria(new Column("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)edid, 0);
                depDfeRow = DO.getRow("AppleDEPDeviceForEnrollment", cTemplate);
                if (!fetch) {
                    final String opDateStr2 = deviceJson.optString("op_date");
                    final Date opDate2 = MDMEnrollmentUtil.getInstance().getDateInStandardFormat(opDateStr2);
                    final Long DBOpDateTime = (Long)depDfeRow.get("OP_DATE");
                    if (DBOpDateTime != -1L) {
                        final Date DBOpDate = new Date(DBOpDateTime);
                        if (opDate2.compareTo(DBOpDate) < 0) {
                            DeviceForEnrollmentHandler.logger.log(Level.INFO, "Skipping device {0} since it has already been updated", new Object[] { serialNum });
                            continue;
                        }
                    }
                    depDfeRow.set("OP_DATE", (Object)opDate2.getTime());
                }
                depDfeRow.set("PROFILE_UUID", (Object)deviceJson.optString("profile_uuid", "--"));
                final boolean isAdd = false;
            }
            depDfeRow.set("MODEL_NAME", (Object)deviceJson.optString("model"));
            depDfeRow.set("DESCRIPTION", (Object)deviceJson.optString("description"));
            depDfeRow.set("ASSERT_TAG", (Object)deviceJson.optString("assert_tag"));
            depDfeRow.set("ASSIGNED_USER", (Object)deviceJson.optString("device_assigned_by"));
            depDfeRow.set("PROFILE_STATUS", (Object)DEPEnrollmentUtil.getDeviceDEPProfileStatus(deviceJson.optString("profile_status", "--")));
            depDfeRow.set("DEVICE_MODEL", (Object)DEPEnrollmentUtil.getDeviceFamily(deviceJson.optString("device_family", "--")));
            final String assignedTimeStr = deviceJson.optString("device_assigned_date");
            final Date assignedTime = MDMEnrollmentUtil.getInstance().getDateInStandardFormat(assignedTimeStr);
            depDfeRow.set("ASSIGNED_TIME", (Object)assignedTime.getTime());
            final Object o;
            if (o != 0) {
                ++added;
                DO.addRow(depDfeRow);
            }
            else {
                ++updated;
                DO.updateRow(depDfeRow);
            }
        }
        DeviceForEnrollmentHandler.logger.log(Level.INFO, "Count of New Device in this sync: {0}", new Object[] { added });
        DeviceForEnrollmentHandler.logger.log(Level.INFO, "Count of Updated Device in this sync: {0}", new Object[] { updated });
        MDMUtil.getPersistence().update(DO);
    }
    
    public void deleteOnEnrollment(final Object criteriaValue, final String criteriaColumn) {
        try {
            DeviceForEnrollmentHandler.logger.log(Level.INFO, "Deleting deviceForEnrollment entry for {0}:{1}", new Object[] { criteriaColumn, criteriaValue });
            final Criteria criteria = new Criteria(new Column("DeviceForEnrollment", criteriaColumn), criteriaValue, 0);
            final DataObject DO = MDMUtil.getPersistenceLite().get("DeviceForEnrollment", criteria);
            DO.deleteRows("DeviceForEnrollment", criteria);
            MDMUtil.getPersistenceLite().update(DO);
        }
        catch (final Exception e) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception while deleteOnEnrollment ", e);
        }
    }
    
    public void deleteOnEnrollment(final Criteria criteria) throws DataAccessException {
        final DataObject DO = MDMUtil.getPersistenceLite().get("DeviceForEnrollment", criteria);
        DO.deleteRows("DeviceForEnrollment", criteria);
        MDMUtil.getPersistenceLite().update(DO);
    }
    
    public void deleteOnEnrollment(final Criteria criteria, final String deviceForEnrollmentChildTableName) throws DataAccessException {
        if (criteria != null) {
            final DeleteQuery dq = (DeleteQuery)new DeleteQueryImpl("DeviceForEnrollment");
            dq.addJoin(new Join("DeviceForEnrollment", deviceForEnrollmentChildTableName, new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            dq.setCriteria(criteria);
            MDMUtil.getPersistenceLite().delete(dq);
        }
    }
    
    public Long getAssociatedUserid(final String criteriaValue, final String criteriaColumn) {
        Long userId = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
            sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            sQuery.setCriteria(new Criteria(new Column("DeviceForEnrollment", criteriaColumn), (Object)criteriaValue, 0));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToUser", "*"));
            final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
            if (!DO.isEmpty()) {
                final Row userRow = DO.getFirstRow("DeviceEnrollmentToUser");
                userId = (Long)userRow.get("MANAGED_USER_ID");
            }
        }
        catch (final Exception e) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception while getAssociatedUserid ", e);
        }
        return userId;
    }
    
    public Long getAssociatedUserid(final Long deviceForEnrollmentId) {
        Long userId = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
            sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            sQuery.setCriteria(new Criteria(new Column("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToUser", "*"));
            final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
            if (!DO.isEmpty()) {
                final Row userRow = DO.getFirstRow("DeviceEnrollmentToUser");
                userId = (Long)userRow.get("MANAGED_USER_ID");
            }
        }
        catch (final Exception e) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception while getAssociatedUserid ", e);
        }
        return userId;
    }
    
    public Long getAssociatedDfeForRequest(final Long erid) {
        Long dfe = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
            sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            sQuery.setCriteria(new Criteria(new Column("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "*"));
            final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
            if (!DO.isEmpty()) {
                final Row userRow = DO.getFirstRow("DeviceEnrollmentToRequest");
                dfe = (Long)userRow.get("ENROLLMENT_DEVICE_ID");
            }
        }
        catch (final Exception e) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception while getAssociatedDfeForRequest ", e);
        }
        return dfe;
    }
    
    public Long getAssociatedEnrollmentRequestid(final Long deviceForEnrollmentId) {
        Long enrollReqId = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
            sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            sQuery.setCriteria(new Criteria(new Column("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "*"));
            final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
            if (!DO.isEmpty()) {
                final Row userRow = DO.getFirstRow("DeviceEnrollmentToRequest");
                enrollReqId = (Long)userRow.get("ENROLLMENT_REQUEST_ID");
            }
        }
        catch (final Exception e) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception while getAssociatedUserid ", e);
        }
        return enrollReqId;
    }
    
    public List<Long> getAssociatedGroupId(final Long deviceForEnrollmentId) {
        final List<Long> groupID = new ArrayList<Long>();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
            sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToGroup", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            sQuery.addJoin(new Join("DeviceEnrollmentToGroup", "CustomGroup", new String[] { "ASSOCIATED_GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria dfeIDCri = new Criteria(new Column("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0);
            final Criteria groupTypeID = new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)8, 1);
            sQuery.setCriteria(dfeIDCri.and(groupTypeID));
            sQuery.addSelectColumn(new Column("DeviceEnrollmentToGroup", "ASSOCIATED_GROUP_ID"));
            sQuery.addSelectColumn(new Column("DeviceEnrollmentToGroup", "ENROLLMENT_DEVICE_ID"));
            final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
            if (!DO.isEmpty()) {
                final Iterator iterator = DO.getRows("DeviceEnrollmentToGroup");
                while (iterator.hasNext()) {
                    final Row dfeToGroupRow = iterator.next();
                    groupID.add((Long)dfeToGroupRow.get("ASSOCIATED_GROUP_ID"));
                }
            }
        }
        catch (final Exception e) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception while getAssociatedGroupID ", e);
        }
        return groupID;
    }
    
    public Long getDeviceForEnrollmentId(final String serialNumber, final String imei, final String easID, final String udid) {
        final JSONObject wrapperJson = new JSONObject();
        try {
            wrapperJson.put("SerialNumber", (Object)serialNumber);
            wrapperJson.put("IMEI", (Object)imei);
            wrapperJson.put("EASID", (Object)easID);
            if (udid != null) {
                wrapperJson.put("UDID", (Object)udid);
            }
        }
        catch (final JSONException exp) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception in getDeviceForEnrollmentId method {0}", (Throwable)exp);
        }
        return this.getDeviceForEnrollmentId(wrapperJson);
    }
    
    public Long getDeviceForEnrollmentId(final String serialNumber, final String imei, final String easID, final int templateType) {
        final JSONObject wrapperJson = new JSONObject();
        try {
            wrapperJson.put("SerialNumber", (Object)serialNumber);
            wrapperJson.put("IMEI", (Object)imei);
            wrapperJson.put("EASID", (Object)easID);
            wrapperJson.put("template_type", templateType);
        }
        catch (final JSONException exp) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception in getDeviceForEnrollmentId method {0}", (Throwable)exp);
        }
        return this.getDeviceForEnrollmentId(wrapperJson);
    }
    
    public Long getDeviceForEnrollmentId(final JSONObject jsonObject) {
        final String imei = jsonObject.optString("IMEI", (String)null);
        final String serialNumber = jsonObject.optString("SerialNumber", (String)null);
        final String udid = jsonObject.optString("UDID", (String)null);
        final String easID = jsonObject.optString("EASID", (String)null);
        final String genericID = jsonObject.optString("GENERIC_ID", (String)null);
        final int templateType = jsonObject.optInt("template_type", -1);
        final boolean isSameSerialNumberAllowed = jsonObject.optBoolean("ALLOW_DUPLICATE_SERIAL_NUMBER", false);
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
            Criteria criteria = null;
            if (MDMStringUtils.isEmpty(imei) && MDMStringUtils.isEmpty(serialNumber) && MDMStringUtils.isEmpty(udid) && MDMStringUtils.isEmpty(easID)) {
                throw new SyMException(14020, "Either of IMEI / Serial number / UDID / Exchange ID must be specified", (Throwable)null);
            }
            if (!MDMStringUtils.isEmpty(serialNumber) && (templateType == 10 || !isSameSerialNumberAllowed)) {
                criteria = new Criteria(new Column("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)serialNumber, 0, false);
            }
            if (!MDMStringUtils.isEmpty(imei)) {
                final Criteria imeiCriteria = new Criteria(new Column("DeviceForEnrollment", "IMEI"), (Object)imei, 0, false);
                if (criteria != null) {
                    criteria = criteria.or(imeiCriteria);
                }
                else {
                    criteria = imeiCriteria;
                }
            }
            if (!MDMStringUtils.isEmpty(genericID)) {
                final Criteria genericCriteria = new Criteria(new Column("DeviceForEnrollment", "GENERIC_IDENTIFIER"), (Object)genericID, 0, false);
                if (criteria != null) {
                    criteria = criteria.or(genericCriteria);
                }
                else {
                    criteria = genericCriteria;
                }
            }
            if (!MDMStringUtils.isEmpty(udid)) {
                final Criteria udidCriteria = new Criteria(new Column("DeviceForEnrollment", "UDID"), (Object)udid, 0, false);
                if (criteria != null) {
                    criteria = criteria.or(udidCriteria);
                }
                else {
                    criteria = udidCriteria;
                }
            }
            if (!MDMStringUtils.isEmpty(easID)) {
                final Criteria easIDCriteria = new Criteria(new Column("DeviceForEnrollment", "EAS_DEVICE_IDENTIFIER"), (Object)easID, 0, false);
                if (criteria != null) {
                    criteria = criteria.or(easIDCriteria);
                }
                else {
                    criteria = easIDCriteria;
                }
            }
            sQuery.setCriteria(criteria);
            sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
            if (!DO.isEmpty()) {
                final Row row = DO.getFirstRow("DeviceForEnrollment");
                return (Long)row.get("ENROLLMENT_DEVICE_ID");
            }
        }
        catch (final SyMException e) {
            if (e.getErrorCode() == 14020) {
                DeviceForEnrollmentHandler.logger.warning("IMEI / Serial Number is needed for identifying Device For Enrollment");
            }
            else {
                DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception while getDeviceForEnrollmentId ", (Throwable)e);
            }
        }
        catch (final Exception e2) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception while getDeviceForEnrollmentId ", e2);
        }
        return null;
    }
    
    public JSONObject getDeviceForEnrollmentProps(final Long deviceForEnrollmentId) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentProps"));
        sQuery.setCriteria(new Criteria(new Column("DeviceEnrollmentProps", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentProps", "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final Row propsRow = dO.getRow("DeviceEnrollmentProps");
            final JSONObject propsJSON = new JSONObject();
            propsJSON.putOpt("ASSIGNED_DEVICE_NAME", propsRow.get("ASSIGNED_DEVICE_NAME"));
            return propsJSON;
        }
        return new JSONObject();
    }
    
    public void updateDeviceForEnrollmentProps(final String uniqueColumnName, final String uniqueColumnValue, final JSONObject devicePropsJSON) throws DataAccessException {
        final Criteria deviceForEnrollmentCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", uniqueColumnName), (Object)uniqueColumnValue, 0);
        final DataObject deviceForEnrollmentDAO = MDMUtil.getPersistence().get("DeviceForEnrollment", deviceForEnrollmentCriteria);
        if (!deviceForEnrollmentDAO.isEmpty()) {
            final Row deviceForEnrollmentRow = deviceForEnrollmentDAO.getFirstRow("DeviceForEnrollment");
            final List columnNames = deviceForEnrollmentRow.getColumns();
            for (final Object columnName : columnNames) {
                final String colName = String.valueOf(columnName);
                if (!colName.equalsIgnoreCase("CUSTOMER_ID") && !colName.equalsIgnoreCase("ENROLLMENT_DEVICE_ID") && !colName.equalsIgnoreCase("ADDED_TIME") && !colName.equalsIgnoreCase("UPDATED_TIME")) {
                    final String newValue = devicePropsJSON.optString(colName, (String)null);
                    if (MDMStringUtils.isEmpty(newValue)) {
                        continue;
                    }
                    deviceForEnrollmentRow.set(colName, (Object)newValue);
                    deviceForEnrollmentRow.set("UPDATED_TIME", (Object)MDMUtil.getCurrentTime());
                }
            }
            deviceForEnrollmentDAO.updateRow(deviceForEnrollmentRow);
            DeviceForEnrollmentHandler.logger.log(Level.INFO, "Updating DeviceForEnrollmentTable with criteria columnname={0} and columnvalue={1} with the row {2}", new Object[] { uniqueColumnName, uniqueColumnValue, deviceForEnrollmentRow });
            MDMUtil.getPersistence().update(deviceForEnrollmentDAO);
        }
    }
    
    public HashMap getDeviceForEnrollmentAssociatedUserDetails(final JSONObject deviceForEnrollmentJSON) {
        final Long deviceForEnrollmentID = this.getDeviceForEnrollmentId(deviceForEnrollmentJSON);
        final Long erid = this.getAssociatedEnrollmentRequestid(deviceForEnrollmentID);
        return ManagedUserHandler.getInstance().getManagedUserDetailsForRequest(erid);
    }
    
    public void deleteEnrolledDevice(final Long customerId, final Long currentTime) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
            sQuery.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            sQuery.addJoin(new Join("DeviceForEnrollment", "MdDeviceInfo", new String[] { "SERIAL_NUMBER" }, new String[] { "SERIAL_NUMBER" }, 2));
            sQuery.addJoin(new Join("MdDeviceInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.setCriteria(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
            sQuery.addSelectColumn(new Column("DeviceForEnrollment", "*"));
            final DataObject enrolledDO = MDMUtil.getPersistenceLite().get(sQuery);
            enrolledDO.deleteRows("DeviceForEnrollment", (Criteria)null);
            MDMUtil.getPersistenceLite().update(enrolledDO);
            final SelectQuery sQueryDeleted = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
            sQueryDeleted.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            sQueryDeleted.addSelectColumn(new Column("DeviceForEnrollment", "*"));
            final Criteria cSerialNumner = new Criteria(new Column("DeviceForEnrollment", "UPDATED_TIME"), (Object)currentTime, 7);
            final Criteria cCustomerId = new Criteria(new Column("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerId, 0);
            sQueryDeleted.setCriteria(cSerialNumner.and(cCustomerId));
            final DataObject DO = MDMUtil.getPersistenceLite().get(sQueryDeleted);
            DO.deleteRows("DeviceForEnrollment", cSerialNumner.and(cCustomerId));
            MDMUtil.getPersistenceLite().update(DO);
        }
        catch (final Exception e) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception while deleting enrolled devices ", e);
        }
    }
    
    public void addDeviceForEnrollmentToCustomGroup(final List<Long> deviceForEnrollmentList, final Long groupID) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentToGroup"));
            query.addJoin(new Join("DeviceEnrollmentToGroup", "CustomGroup", new String[] { "ASSOCIATED_GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addSelectColumn(new Column("DeviceEnrollmentToGroup", "ENROLLMENT_DEVICE_ID"));
            query.addSelectColumn(new Column("DeviceEnrollmentToGroup", "ASSOCIATED_GROUP_ID"));
            final Criteria customerCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)CustomerInfoUtil.getInstance().getCustomerId(), 0);
            final Criteria groupCriteria = new Criteria(new Column("DeviceEnrollmentToGroup", "ASSOCIATED_GROUP_ID"), (Object)groupID, 0);
            query.setCriteria(customerCriteria.and(groupCriteria));
            final DataObject resultDO = MDMUtil.getPersistence().get(query);
            final DataObject newDO = (DataObject)new WritableDataObject();
            for (final Long deviceFoEnrollMentID : deviceForEnrollmentList) {
                Row dfeToTRow = resultDO.getRow("DeviceEnrollmentToGroup", new Criteria(new Column("DeviceEnrollmentToGroup", "ENROLLMENT_DEVICE_ID"), (Object)deviceFoEnrollMentID, 0));
                if (dfeToTRow == null) {
                    dfeToTRow = new Row("DeviceEnrollmentToGroup");
                    dfeToTRow.set("ENROLLMENT_DEVICE_ID", (Object)deviceFoEnrollMentID);
                    dfeToTRow.set("ASSOCIATED_GROUP_ID", (Object)groupID);
                    newDO.addRow(dfeToTRow);
                }
            }
            if (!newDO.isEmpty()) {
                MDMUtil.getPersistence().update(newDO);
            }
        }
        catch (final Exception ex) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateTemplateToDeviceForEnrollment while updating", ex);
        }
    }
    
    public void addOrUpdateStatus(final JSONObject deviceForEnrollJson) throws Exception {
        final String udid = deviceForEnrollJson.optString("UDID", (String)null);
        final String serialNumber = deviceForEnrollJson.optString("SERIAL_NUMBER", (String)null);
        final String imei = deviceForEnrollJson.optString("IMEI", (String)null);
        final String easID = deviceForEnrollJson.optString("EAS_DEVICE_IDENTIFIER", (String)null);
        final Integer status = deviceForEnrollJson.getInt("STATUS");
        final String remarksDBString = this.getRemarksForStatusAndError(deviceForEnrollJson);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
        Criteria criteria = null;
        if (MDMStringUtils.isEmpty(imei) && MDMStringUtils.isEmpty(serialNumber) && MDMStringUtils.isEmpty(udid) && MDMStringUtils.isEmpty(easID)) {
            throw new SyMException(14020, "Either of IMEI / Serial number / UDID / Exchange ID must be specified", (Throwable)null);
        }
        if (!MDMStringUtils.isEmpty(serialNumber)) {
            criteria = new Criteria(new Column("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)serialNumber, 0, false);
        }
        if (!MDMStringUtils.isEmpty(imei)) {
            final Criteria imeiCriteria = new Criteria(new Column("DeviceForEnrollment", "IMEI"), (Object)imei, 0, false);
            if (criteria != null) {
                criteria = criteria.or(imeiCriteria);
            }
            else {
                criteria = imeiCriteria;
            }
        }
        if (!MDMStringUtils.isEmpty(udid)) {
            final Criteria udidCriteria = new Criteria(new Column("DeviceForEnrollment", "UDID"), (Object)udid, 0, false);
            if (criteria != null) {
                criteria = criteria.or(udidCriteria);
            }
            else {
                criteria = udidCriteria;
            }
        }
        if (!MDMStringUtils.isEmpty(easID)) {
            final Criteria easIDCriteria = new Criteria(new Column("DeviceForEnrollment", "EAS_DEVICE_IDENTIFIER"), (Object)easID, 0, false);
            if (criteria != null) {
                criteria = criteria.or(easIDCriteria);
            }
            else {
                criteria = easIDCriteria;
            }
        }
        sQuery.setCriteria(criteria);
        sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        if (!DO.isEmpty()) {
            final Row row = DO.getFirstRow("DeviceForEnrollment");
            row.set("STATUS", (Object)status);
            row.set("REMARKS", (Object)remarksDBString);
            DO.updateRow(row);
        }
        MDMUtil.getPersistence().update(DO);
        if (0 == status) {
            final JSONObject deviceProps = new JSONObject();
            deviceProps.put("SerialNumber", (Object)serialNumber);
            deviceProps.put("UDID", (Object)udid);
            final JSONObject assiginProps = new JSONObject();
            assiginProps.put("device_unique_props", (Object)deviceProps);
            if (deviceForEnrollJson.optString("NAME") != null) {
                final JSONObject userJSON = new JSONObject();
                userJSON.put("NAME", (Object)deviceForEnrollJson.optString("NAME"));
                userJSON.put("DOMAIN_NETBIOS_NAME", (Object)deviceForEnrollJson.optString("DOMAIN_NETBIOS_NAME"));
                assiginProps.put("user_details", (Object)userJSON);
                assiginProps.put("CUSTOMER_ID", deviceForEnrollJson.get("CUSTOMER_ID"));
                MDMApiFactoryProvider.getMDMUtilAPI().postDeviceUserDetails(assiginProps);
            }
        }
    }
    
    private String getRemarksForStatusAndError(final JSONObject deviceForEnrollJson) throws JSONException {
        String remarksDBString = "mdm.db.unknown_error_enroll";
        final Integer templateType = deviceForEnrollJson.getInt("TEMPLATE_TYPE");
        if (templateType.equals(31)) {
            final Integer status = deviceForEnrollJson.getInt("STATUS");
            Integer errorCode = null;
            if (status.equals(0)) {
                remarksDBString = "dc.mdm.db.agent.enroll.agent_enroll_finished";
            }
            else if (status.equals(1)) {
                remarksDBString = "mdm.enroll.remarks.awaiting_user_assignment";
            }
            else if (status.equals(2)) {
                remarksDBString = "mdm.db.user_assignment_completed";
            }
            else if (status.equals(301)) {
                remarksDBString = "mdm.db.user_assignment_received_in_agent";
            }
            else if (status.equals(302)) {
                remarksDBString = "mdm.win.db.awaiting_user_logon";
            }
            else if (status.equals(10)) {
                remarksDBString = "mdm.db.unknown_error_enroll";
            }
            else if (status.equals(9)) {
                errorCode = deviceForEnrollJson.getInt("ERROR_CODE");
                remarksDBString = WindowsLaptopEnrollmentHandler.ERROR_CODE_TO_REMARKS.get(errorCode);
            }
        }
        return remarksDBString;
    }
    
    private DataObject autoAssignAdminEnrollmentDevices(final DataObject dataObject, final Long deviceForEnrollmentId, final Long managedUserID) throws DataAccessException {
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AutoAssignAppleConfigDevices") || MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AutoAssignAndroidEMMTokenDevices")) {
            DeviceForEnrollmentHandler.logger.log(Level.FINE, "Feature Param Enabled to Auto Assign User , deviceID: {0}", deviceForEnrollmentId);
            if (!dataObject.containsTable("DeviceEnrollmentToUser")) {
                if (managedUserID != -1L) {
                    final Row deviceToUserRow = new Row("DeviceEnrollmentToUser");
                    deviceToUserRow.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
                    deviceToUserRow.set("MANAGED_USER_ID", (Object)managedUserID);
                    dataObject.addRow(deviceToUserRow);
                    DeviceForEnrollmentHandler.logger.log(Level.INFO, "Mapping {0} to user {1}", new Object[] { deviceForEnrollmentId, managedUserID });
                }
                else {
                    DeviceForEnrollmentHandler.logger.log(Level.INFO, "User ID for Template is received as {0} , so not proceed to map to device{1}", new Object[] { managedUserID, deviceForEnrollmentId });
                }
            }
            else {
                DeviceForEnrollmentHandler.logger.log(Level.INFO, "{0}User is already Mapped to{1}", new Object[] { managedUserID, deviceForEnrollmentId });
            }
        }
        return dataObject;
    }
    
    public Criteria getDeviceForEnrollmentCriteria(final JSONArray resourceProps) throws Exception {
        Criteria criteria = null;
        final List serialNumberList = new ArrayList();
        final List imeiList = new ArrayList();
        final List udidList = new ArrayList();
        final List easList = new ArrayList();
        final List genericist = new ArrayList();
        for (int i = 0; i < resourceProps.length(); ++i) {
            final JSONObject resourceProp = resourceProps.getJSONObject(i);
            final String serialNumber = resourceProp.optString("SerialNumber", (String)null);
            final String imei = resourceProp.optString("IMEI", (String)null);
            final String udid = resourceProp.optString("UDID", (String)null);
            final String easID = resourceProp.optString("EASID", (String)null);
            final String genericID = resourceProp.optString("GENERIC_ID", (String)null);
            if (serialNumber != null) {
                serialNumberList.add(serialNumber);
            }
            if (udid != null) {
                udidList.add(udid);
            }
            if (easID != null) {
                easList.add(easID);
            }
            if (imei != null) {
                imeiList.add(imei);
            }
            if (genericID != null) {
                genericist.add(genericID);
            }
        }
        final Boolean allowDuplicateSerialNumber = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ALLOW_DUPLICATE_SERIAL_NUMBER");
        if (serialNumberList.size() > 0 && !allowDuplicateSerialNumber) {
            criteria = new Criteria(new Column("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)serialNumberList.toArray(), 8, false);
        }
        if (imeiList.size() > 0) {
            if (criteria == null) {
                criteria = new Criteria(new Column("DeviceForEnrollment", "IMEI"), (Object)imeiList.toArray(), 8, false);
            }
            else {
                criteria = criteria.or(new Criteria(new Column("DeviceForEnrollment", "IMEI"), (Object)imeiList.toArray(), 8, false));
            }
        }
        if (genericist.size() > 0) {
            if (criteria == null) {
                criteria = new Criteria(new Column("DeviceForEnrollment", "GENERIC_IDENTIFIER"), (Object)genericist.toArray(), 8, false);
            }
            else {
                criteria = criteria.or(new Criteria(new Column("DeviceForEnrollment", "GENERIC_IDENTIFIER"), (Object)genericist.toArray(), 8, false));
            }
        }
        if (udidList.size() > 0) {
            final Criteria udidCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "UDID"), (Object)udidList.toArray(), 8, false);
            if (criteria == null) {
                criteria = udidCriteria;
            }
            else {
                criteria = criteria.or(udidCriteria);
            }
        }
        if (easList.size() > 0) {
            if (criteria == null) {
                criteria = new Criteria(new Column("DeviceForEnrollment", "EAS_DEVICE_IDENTIFIER"), (Object)easList.toArray(), 8, false);
            }
            else {
                criteria = criteria.or(new Criteria(new Column("DeviceForEnrollment", "EAS_DEVICE_IDENTIFIER"), (Object)easList.toArray(), 8, false));
            }
        }
        return criteria;
    }
    
    protected void addModelTypeToDataJSON(final JSONObject dataJSON, final int type) {
        final JSONObject additionalContext = new JSONObject();
        additionalContext.put("device_model", type);
        dataJSON.put("additional_context", (Object)additionalContext);
    }
    
    public Long getERIDFromGenericID(final String genericID, final Long customerID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        final Criteria customerCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria genericCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "GENERIC_IDENTIFIER"), (Object)genericID, 0);
        selectQuery.setCriteria(customerCriteria.and(genericCriteria));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        Long erid = null;
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("DeviceEnrollmentToRequest");
            erid = (Long)row.get("ENROLLMENT_REQUEST_ID");
        }
        return erid;
    }
    
    public String getGenericIDFromERID(final Long erid, final Long customerID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        final Criteria customerCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria genericCriteria = new Criteria(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0);
        selectQuery.setCriteria(customerCriteria.and(genericCriteria));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "GENERIC_IDENTIFIER"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        String genericID = null;
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("DeviceForEnrollment");
            genericID = (String)row.get("GENERIC_IDENTIFIER");
        }
        return genericID;
    }
    
    public void applyAssignUserRulesForPendingDevices() throws Exception {
        DeviceForEnrollmentHandler.logger.log(Level.INFO, "Going to assign user to pending devices after user assignment rules");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "MacModernMgmtDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentToUser", "MANAGED_USER_ID"), (Object)null, 0));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        Iterator iterator = dataObject.getRows("DeviceForEnrollment");
        final HashMap customerTodeviceArray = new HashMap();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long customerID = (Long)row.get("CUSTOMER_ID");
            final String serialNumber = (String)row.get("SERIAL_NUMBER");
            final JSONObject device = new JSONObject();
            device.put("SerialNumber", (Object)serialNumber);
            this.addModelTypeToDataJSON(device, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MODERN_DEVICE);
            JSONArray jsonArray = customerTodeviceArray.get(customerID);
            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }
            jsonArray.put((Object)device);
            customerTodeviceArray.put(customerID, jsonArray);
        }
        DeviceForEnrollmentHandler.logger.log(Level.INFO, "List of device going to assign user {0}", customerTodeviceArray);
        iterator = customerTodeviceArray.keySet().iterator();
        while (iterator.hasNext()) {
            final Long customerID2 = iterator.next();
            final JSONObject template = EnrollmentTemplateHandler.getModenMacMgmtEnrollmentTemplateDetailsForCustomer(customerID2);
            final String tempalteToken = template.optString("TEMPLATE_TOKEN");
            new UserAssignmentRuleHandler().applyAssignUserRules(customerTodeviceArray.get(customerID2), tempalteToken);
        }
        DeviceForEnrollmentHandler.logger.log(Level.INFO, " assign user to pending devices after user assignment rules is completed");
    }
    
    public Long getDeviceForEnrollmentUserId(final JSONObject jsonObject) {
        final Long dfeId = this.getDeviceForEnrollmentId(jsonObject);
        return this.getDeviceForEnrollmentUserId(dfeId);
    }
    
    public Long getDeviceForEnrollmentUserId(final Long dfeId) {
        try {
            return (Long)DBUtil.getValueFromDB("DeviceEnrollmentToUser", "ENROLLMENT_DEVICE_ID", (Object)dfeId, "MANAGED_USER_ID");
        }
        catch (final Exception e) {
            DeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception in getDeviceForEnrollmentUserId", e);
            return null;
        }
    }
    
    static {
        DeviceForEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
