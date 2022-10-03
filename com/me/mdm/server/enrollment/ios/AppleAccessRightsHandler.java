package com.me.mdm.server.enrollment.ios;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Level;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import java.util.HashMap;
import java.util.logging.Logger;

public class AppleAccessRightsHandler
{
    public static Logger logger;
    public static final String CONFIG_PROFILE_READ = "CONFIG_PROFILE_READ";
    public static final String CONFIG_PROFILE_WRITE = "CONFIG_PROFILE_WRITE";
    public static final String DEVICE_LOCK_AND_PASSCODE_REMOVAL = "DEVICE_LOCK_AND_PASSCODE_REMOVAL";
    public static final String DEVICE_ERASE = "DEVICE_ERASE";
    public static final String DEVICE_INFO = "DEVICE_INFO";
    public static final String NETWORK_INFO = "NETWORK_INFO";
    public static final String PROVISIONAL_PROFILE_READ = "PROVISIONAL_PROFILE_READ";
    public static final String PROVISIONAL_PROFILE_WRITE = "PROVISIONAL_PROFILE_WRITE";
    public static final String INSTALLED_APPS_READ = "INSTALLED_APPS_READ";
    public static final String RESTRICTION_QUERIES = "RESTRICTION_QUERIES";
    public static final String SECURITY_QUERIES = "SECURITY_QUERIES";
    public static final String SETTINGS_MANIPULATION = "SETTINGS_MANIPULATION";
    public static final String APP_MANAGEMENT = "APP_MANAGEMENT";
    public static final int COMPLETE_ACCESS_V4 = 2047;
    public static final int COMPLETE_ACCESS = 8191;
    private static final HashMap<String, Integer> ACCESS_RIGHTS_VALUES_MAP;
    
    public static AppleAccessRightsHandler getInstance() {
        return new AppleAccessRightsHandler();
    }
    
    private int getAccessRightsForPrivacySettings(final HashMap privacySettings) {
        int accessRights = 8191;
        final int wipeAccess = Integer.parseInt(privacySettings.get("disable_wipe").toString());
        if (wipeAccess == 2) {
            accessRights -= AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.get("DEVICE_ERASE");
        }
        return accessRights;
    }
    
    public int generateAccessRights(final Long erid, final int osVersion) {
        int accessRights = 8191;
        try {
            final HashMap privacySettings = new PrivacySettingsHandler().getPrivacySettingsForEnrollmentRquest(erid);
            accessRights = this.getAccessRightsForPrivacySettings(privacySettings);
            accessRights = ((osVersion == 4) ? (accessRights & 0x7FF) : accessRights);
            this.addAccessRightForErid(erid, accessRights);
        }
        catch (final Exception e) {
            AppleAccessRightsHandler.logger.log(Level.SEVERE, "Exception in IOS generate AccessRights.. ", e);
        }
        return accessRights;
    }
    
    private void addAccessRightForErid(final Long erid, final int accessRight) throws Exception {
        try {
            DataObject dataObject = DBUtil.getDataObjectFromDB("AppleAccessRightToDeviceRequest", "ENROLLMENT_REQUEST_ID", (Object)erid);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("AppleAccessRightToDeviceRequest");
                row.set("ACCESS_RIGHT", (Object)accessRight);
                dataObject.updateRow(row);
            }
            else {
                dataObject = (DataObject)new WritableDataObject();
                final Row row = new Row("AppleAccessRightToDeviceRequest");
                row.set("ENROLLMENT_REQUEST_ID", (Object)erid);
                row.set("ACCESS_RIGHT", (Object)accessRight);
                dataObject.addRow(row);
            }
            MDMUtil.getPersistence().update(dataObject);
            AppleAccessRightsHandler.logger.log(Level.INFO, "Updated access rights for EnrollmentRequestID - {0}, AppleAccessRights - {1}", new Object[] { erid, accessRight });
        }
        catch (final Exception e) {
            AppleAccessRightsHandler.logger.log(Level.SEVERE, "Exception in IOS set AccessRight For Erid.. ", e);
            throw e;
        }
    }
    
    public Integer getAccessRightsForResourceId(final Long rescId) {
        Integer accessRight = null;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AppleAccessRightToDeviceRequest"));
            sq.addJoin(new Join("AppleAccessRightToDeviceRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            sq.setCriteria(new Criteria(new Column("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)rescId, 0));
            sq.addSelectColumn(new Column("AppleAccessRightToDeviceRequest", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                accessRight = (int)dataObject.getFirstValue("AppleAccessRightToDeviceRequest", "ACCESS_RIGHT");
            }
            else {
                AppleAccessRightsHandler.logger.log(Level.INFO, "IOS Get AccessRights.. No value in DB, returning NULL for resource {0}", rescId);
            }
        }
        catch (final Exception e) {
            AppleAccessRightsHandler.logger.log(Level.SEVERE, "Exception in IOS Get AccessRights For ResourceId.. ", e);
        }
        return accessRight;
    }
    
    public Integer getAccessRightsForErid(final Long erid) {
        Integer accessRight = null;
        try {
            final Object temp = DBUtil.getValueFromDB("AppleAccessRightToDeviceRequest", "ENROLLMENT_REQUEST_ID", (Object)erid, "ACCESS_RIGHT");
            accessRight = ((temp == null) ? null : ((Integer)temp));
        }
        catch (final Exception e) {
            AppleAccessRightsHandler.logger.log(Level.SEVERE, "Exception in IOS Get AccessRights For Erid.. ", e);
        }
        return accessRight;
    }
    
    public static boolean isAccessRightProvided(final String access, final Integer accessRights) {
        return accessRights == null || (accessRights & AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.get(access)) == AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.get(access);
    }
    
    static {
        AppleAccessRightsHandler.logger = Logger.getLogger("MDMEnrollment");
        (ACCESS_RIGHTS_VALUES_MAP = new HashMap<String, Integer>()).put("CONFIG_PROFILE_READ", 1);
        AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.put("CONFIG_PROFILE_WRITE", 2);
        AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.put("DEVICE_LOCK_AND_PASSCODE_REMOVAL", 4);
        AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.put("DEVICE_ERASE", 8);
        AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.put("DEVICE_INFO", 16);
        AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.put("NETWORK_INFO", 32);
        AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.put("PROVISIONAL_PROFILE_READ", 64);
        AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.put("PROVISIONAL_PROFILE_WRITE", 128);
        AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.put("INSTALLED_APPS_READ", 256);
        AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.put("RESTRICTION_QUERIES", 512);
        AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.put("SECURITY_QUERIES", 1024);
        AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.put("SETTINGS_MANIPULATION", 2048);
        AppleAccessRightsHandler.ACCESS_RIGHTS_VALUES_MAP.put("APP_MANAGEMENT", 4096);
    }
}
