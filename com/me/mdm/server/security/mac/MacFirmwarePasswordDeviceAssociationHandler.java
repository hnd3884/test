package com.me.mdm.server.security.mac;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.security.passcode.MDMManagedPasswordDeviceHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MacFirmwarePasswordDeviceAssociationHandler
{
    private static final Logger SECURITY_LOGGER;
    
    public static JSONObject getFirmwarePasswordCommonParams(final Long resourceID) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("newPasswordID", (Object)getPasswordDetailsForVerification(resourceID));
        responseJSON.put("existingPasswordID", (Object)getCurrentlySetFirmwarePassword(resourceID));
        return responseJSON;
    }
    
    public static void addFirmwarePasswordToDevice(final boolean isSiliconMac, final JSONObject requestJSON) throws Exception {
        int passwordType = 1;
        if (isSiliconMac) {
            passwordType = 2;
        }
        requestJSON.put("MANAGED_PASSWORD_TYPE", passwordType);
        MDMManagedPasswordDeviceHandler.addOrUpdateMDMDeviceManagedPasswordDetails(requestJSON);
    }
    
    public static void updateDevicePasswordAfterSucessfullVerification(final Long verifiedPasswordID, final Long resourceID) throws Exception {
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("RESOURCE_ID", (Object)resourceID);
        requestJSON.put("MANAGED_PASSWORD_STATUS", 2);
        requestJSON.put("MANAGED_PASSWORD_ID", (Object)verifiedPasswordID);
        int passwordType = 1;
        if (MDMUtil.isSiliconMac(resourceID)) {
            passwordType = 2;
        }
        requestJSON.put("MANAGED_PASSWORD_TYPE", passwordType);
        MDMManagedPasswordDeviceHandler.addOrUpdateMDMDeviceManagedPasswordDetails(requestJSON);
    }
    
    public static void updateDevicePasswordAfterSucessfullPasswordSet(final Long verifiedPasswordID, final Long resourceID) throws Exception {
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("RESOURCE_ID", (Object)resourceID);
        requestJSON.put("MANAGED_PASSWORD_STATUS", 3);
        requestJSON.put("MANAGED_PASSWORD_ID", (Object)verifiedPasswordID);
        int passwordType = 1;
        if (MDMUtil.isSiliconMac(resourceID)) {
            passwordType = 2;
        }
        requestJSON.put("MANAGED_PASSWORD_TYPE", passwordType);
        MDMManagedPasswordDeviceHandler.addOrUpdateMDMDeviceManagedPasswordDetails(requestJSON);
    }
    
    public static Long getCurrentlySetFirmwarePassword(final Long resourceID) throws Exception {
        return getFirmwarePasswordAssociatedToDevice(resourceID);
    }
    
    public static Long getPasswordDetailsForVerification(final Long resourceID) throws Exception {
        return getDevicePasswordDetails(resourceID, 1);
    }
    
    public static Long getDevicePasswordDetails(final Long resourceID, final int status) throws Exception {
        final Criteria resourceIDCri = new Criteria(new Column("MDMDeviceManagedPasswordDetails", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria passwordStatusCri = new Criteria(new Column("MDMDeviceManagedPasswordDetails", "MANAGED_PASSWORD_STATUS"), (Object)status, 0);
        int passwordType = 1;
        if (MDMUtil.isSiliconMac(resourceID)) {
            passwordType = 2;
        }
        final Criteria passwordTypeCri = new Criteria(new Column("MDMDeviceManagedPasswordDetails", "MANAGED_PASSWORD_TYPE"), (Object)passwordType, 0);
        final JSONArray passwordArray = MDMManagedPasswordDeviceHandler.getDeviceManagedPasswordDetails(resourceIDCri.and(passwordStatusCri).and(passwordTypeCri));
        if (passwordArray.length() >= 1) {
            final JSONObject passwordMappingJSON = (JSONObject)passwordArray.get(0);
            return passwordMappingJSON.getLong("MANAGED_PASSWORD_ID");
        }
        return null;
    }
    
    public static boolean removeExistingFirmwarePasswordFromDeviceTables(final Long resourceID, final Long passwordID) {
        final Criteria resourceIDCri = new Criteria(new Column("MDMDeviceManagedPasswordDetails", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria passwordIDCriteria = new Criteria(new Column("MDMDeviceManagedPasswordDetails", "MANAGED_PASSWORD_ID"), (Object)passwordID, 0);
        int passwordType = 1;
        try {
            if (MDMUtil.isSiliconMac(resourceID)) {
                passwordType = 2;
            }
        }
        catch (final Exception e) {
            MacFirmwarePasswordDeviceAssociationHandler.SECURITY_LOGGER.log(Level.SEVERE, e, () -> "Exception while checking whether the given resource is a silicon mac: " + n);
        }
        final Criteria passwordTypeCri = new Criteria(new Column("MDMDeviceManagedPasswordDetails", "MANAGED_PASSWORD_TYPE"), (Object)passwordType, 0);
        final Criteria passwordStatus = new Criteria(new Column("MDMDeviceManagedPasswordDetails", "MANAGED_PASSWORD_STATUS"), (Object)3, 1);
        MacFirmwarePasswordDeviceAssociationHandler.SECURITY_LOGGER.log(Level.INFO, "DeletePasswordAssociatedToDevice passwordType:{0} deviceID:{1} passwordID:{2} passwordStatus:OtherThanPasswordsNotInHistory", new Object[] { passwordType, resourceID, passwordID });
        return MDMManagedPasswordDeviceHandler.deleteManagedPasswordForResource(resourceIDCri.and(passwordIDCriteria).and(passwordTypeCri).and(passwordStatus));
    }
    
    public static void addOrUpdateInventoryFirmwareDevice(final Long resourceID, final Long passwordID) {
        try {
            final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("MDMDeviceFirmwareInfo");
            uq.setUpdateColumn("MANAGED_PASSWORD_ID", (Object)passwordID);
            uq.setCriteria(new Criteria(new Column("MDMDeviceFirmwareInfo", "RESOURCE_ID"), (Object)resourceID, 0));
            MDMUtil.getPersistence().update(uq);
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in addOrUpdateInventoryFirmwareDevice", ex);
        }
    }
    
    public static Long getFirmwarePasswordAssociatedToDevice(final Long resourceID) {
        try {
            return (Long)DBUtil.getValueFromDB("MDMDeviceFirmwareInfo", "RESOURCE_ID", (Object)resourceID, "MANAGED_PASSWORD_ID");
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in getFirmwarePasswordAssociatedToDevice", ex);
            return null;
        }
    }
    
    static {
        SECURITY_LOGGER = Logger.getLogger("MDMDeviceSecurityLogger");
    }
    
    public static final class FirmwarePasswordDeviceStatus
    {
        public static final int FIRMWARE_PASSWORD_NOT_VERIFIED = 1;
        public static final int FIRMWARE_PASSWORD_VERIFIED = 2;
        public static final int FIRMWARE_PASSWORD_HISTORY = 3;
    }
}
