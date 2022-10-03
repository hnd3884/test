package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeKioskPayload;
import com.adventnet.persistence.DataObject;

public class Do2ChromeKioskPolicyPayload implements DO2ChromePayload
{
    @Override
    public ChromeKioskPayload createPayload(final DataObject dataObject) {
        ChromeKioskPayload kioskPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("ChromeKioskPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                kioskPayload = new ChromeKioskPayload("1.0", "Kiosk", "Kiosk");
                final int kioskMode = (int)row.get("KIOSK_MODE");
                final Iterator appIterator = dataObject.getRows("MdAppGroupDetails");
                kioskPayload.setMode(kioskMode == 0);
                if (kioskMode == 0) {
                    kioskPayload.setBailOut((Boolean)row.get("BAILOUT_ENABLED"));
                    kioskPayload.setPromptNetwork((Boolean)row.get("PROMPT_NETWORK"));
                    kioskPayload.setHealthMonitoring((Boolean)row.get("HEALTH_MONITOR_ENABLED"));
                    kioskPayload.setLogsEnabled((Boolean)row.get("LOG_UPLOAD_ENABLED"));
                    kioskPayload.setOSUpdatePermission((Boolean)row.get(7));
                    kioskPayload = this.setAlertDetails(kioskPayload, row);
                }
                kioskPayload.setApps(this.getKioskAppDetails(dataObject));
            }
        }
        catch (final Exception ex) {
            Do2ChromeKioskPolicyPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return kioskPayload;
    }
    
    private JSONArray getKioskAppDetails(final DataObject dO) throws JSONException {
        try {
            final JSONArray apps = new JSONArray();
            final Iterator appGroupItr = dO.getRows("MdAppGroupDetails");
            while (appGroupItr.hasNext()) {
                final JSONObject appDetails = new JSONObject();
                final Row appGroupRow = appGroupItr.next();
                final Long appGroupID = (Long)appGroupRow.get("APP_GROUP_ID");
                final int platform = (int)appGroupRow.get("PLATFORM_TYPE");
                final String identifier = (String)appGroupRow.get("IDENTIFIER");
                if (platform == 2) {
                    appDetails.put("AppId", (Object)("app:" + identifier));
                }
                else {
                    final Row packToAppRow = dO.getRow("MdPackageToAppData", new Criteria(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"), (Object)appGroupID, 0));
                    if (packToAppRow != null) {
                        final Row packToAppGroup = dO.getRow("MdPackageToAppGroup", new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupID, 0));
                        final int packageType = (int)packToAppGroup.get("PACKAGE_TYPE");
                        if (packageType == 2) {
                            final String location = (String)packToAppRow.get("APP_FILE_LOC");
                            appDetails.put("AppUrl", (Object)location);
                        }
                        appDetails.put("AppId", (Object)identifier);
                    }
                }
                appDetails.put("AppRestrictions", (Object)("%managedconfig." + appGroupID + "%"));
                apps.put((Object)appDetails);
            }
            return apps;
        }
        catch (final DataAccessException ex) {
            Do2ChromeKioskPolicyPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", (Throwable)ex);
            return null;
        }
    }
    
    private ChromeKioskPayload setAlertDetails(final ChromeKioskPayload kioskPayload, final Row row) throws JSONException {
        kioskPayload.initAlertDetails();
        final String mode = (String)row.get("STATUS_ALERT_MODE");
        kioskPayload.setAlertType(mode);
        if (mode.equalsIgnoreCase("EMAIL") || mode.equalsIgnoreCase("BOTH")) {
            kioskPayload.setEmailIDs((String)row.get("STATUS_ALERT_EMAIL"));
        }
        if (mode.equalsIgnoreCase("SMS") || mode.equalsIgnoreCase("BOTH")) {
            kioskPayload.setPhoneNumbers((String)row.get("STATUS_ALERT_PHNO"));
        }
        return kioskPayload;
    }
}
