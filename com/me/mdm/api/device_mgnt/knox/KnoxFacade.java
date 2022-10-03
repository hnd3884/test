package com.me.mdm.api.device_mgnt.knox;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.mdm.server.device.DeviceFacade;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.HashMap;
import org.json.JSONArray;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Map;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.me.mdm.server.android.knox.enroll.KnoxLicenseHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class KnoxFacade
{
    private static final Logger LOGGER;
    private static KnoxFacade inst;
    
    private KnoxFacade() {
    }
    
    public static KnoxFacade getInstance() {
        return KnoxFacade.inst;
    }
    
    public JSONObject getLicenseDetails(final JSONObject apiRequest) throws Exception {
        final Long customerID = APIUtil.getCustomerID(apiRequest);
        final Map license = KnoxLicenseHandler.getInstance().getKnoxCustomerLicense(customerID);
        if (license.size() > 0) {
            final JSONObject body = new JSONObject();
            body.put("key", (Object)license.get("LICENSE_DATA"));
            body.put("purchased_device_count", license.get("MAX_COUNT"));
            body.put("used_device_count", KnoxUtil.getInstance().getUsedLicenseCount(customerID));
            final SimpleDateFormat format = new SimpleDateFormat(ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeFormat(), ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale());
            final long time = format.parse(license.get("EXPIRY_DATE")).getTime();
            body.put("exp_date", time);
            body.put("email_to_notify", license.get("EMAIL_TO_NOTIFY"));
            return body;
        }
        return null;
    }
    
    public void setLicenseDetails(final JSONObject apiRequest) throws Exception {
        final JSONObject requestJSON = apiRequest.getJSONObject("msg_body");
        final Long customerID = APIUtil.getCustomerID(apiRequest);
        final org.json.simple.JSONObject licData = new org.json.simple.JSONObject();
        licData.put((Object)"CUSTOMER_ID", (Object)customerID);
        final Map license = KnoxLicenseHandler.getInstance().getKnoxCustomerLicense(customerID);
        if (license.size() > 0) {
            if (requestJSON.has("key")) {
                licData.put((Object)"LICENSE_DATA", (Object)String.valueOf(requestJSON.get("key")));
            }
            else {
                licData.put((Object)"LICENSE_DATA", (Object)license.get("LICENSE_DATA"));
            }
            if (requestJSON.has("purchased_device_count")) {
                licData.put((Object)"MAX_COUNT", (Object)requestJSON.getInt("purchased_device_count"));
            }
            else {
                licData.put((Object)"MAX_COUNT", (Object)license.get("MAX_COUNT"));
            }
            if (requestJSON.has("exp_date")) {
                licData.put((Object)"EXPIRY_DATE", (Object)requestJSON.getLong("exp_date"));
            }
            else {
                final SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy HH:mm a");
                final long time = format.parse(license.get("EXPIRY_DATE")).getTime();
                licData.put((Object)"EXPIRY_DATE", (Object)time);
            }
            if (requestJSON.has("email_to_notify")) {
                licData.put((Object)"EMAIL_TO_NOTIFY", (Object)String.valueOf(requestJSON.get("email_to_notify")));
            }
            else {
                licData.put((Object)"EMAIL_TO_NOTIFY", (Object)license.get("EMAIL_TO_NOTIFY"));
            }
        }
        else {
            if (!requestJSON.has("key") || MDMStringUtils.isEmpty(String.valueOf(requestJSON.get("key")))) {
                throw new APIHTTPException("COM0005", new Object[] { "key" });
            }
            if (!requestJSON.has("purchased_device_count") || requestJSON.optInt("purchased_device_count", 0) <= 0) {
                throw new APIHTTPException("COM0005", new Object[] { "purchased_device_count" });
            }
            if (!requestJSON.has("exp_date") || requestJSON.optLong("exp_date") <= 0L) {
                throw new APIHTTPException("COM0005", new Object[] { "exp_date" });
            }
            if (!requestJSON.has("email_to_notify") || MDMStringUtils.isEmpty(String.valueOf(requestJSON.get("email_to_notify")))) {
                throw new APIHTTPException("COM0005", new Object[] { "email_to_notify" });
            }
            licData.put((Object)"LICENSE_DATA", (Object)String.valueOf(requestJSON.get("key")));
            licData.put((Object)"MAX_COUNT", (Object)requestJSON.getInt("purchased_device_count"));
            licData.put((Object)"EXPIRY_DATE", (Object)requestJSON.getLong("exp_date"));
            licData.put((Object)"EMAIL_TO_NOTIFY", (Object)String.valueOf(requestJSON.get("email_to_notify")));
        }
        KnoxLicenseHandler.getInstance().addOrUpdateKnoxLicenseData(licData);
    }
    
    public JSONObject getDistribDetails(final JSONObject apiRequest) throws Exception {
        final HashMap knoxDS = KnoxLicenseHandler.getInstance().getKnoxCustomerDS(APIUtil.getCustomerID(apiRequest));
        final JSONObject res = new JSONObject();
        if (knoxDS.get("dsOption") == 2) {
            res.put("distribution", (Object)"manual");
        }
        else {
            res.put("distribution", (Object)"auto");
            if (knoxDS.get("dsToGroup")) {
                res.put("apply_to", (Object)"groups");
                final String groupIDs = knoxDS.get("dsGroupID");
                final String groupNames = knoxDS.get("dsGroupName");
                final String[] groupIDArray = groupIDs.split(",");
                final String[] groupNameArray = groupNames.split(",");
                final JSONArray arr = new JSONArray();
                for (int i = 0; i < groupIDArray.length; ++i) {
                    final JSONObject obj = new JSONObject();
                    obj.put("id", (Object)groupIDArray[i]);
                    obj.put("name", (Object)groupNameArray[i]);
                    arr.put((Object)obj);
                }
                res.put("groups", (Object)arr);
            }
            else {
                res.put("apply_to", (Object)"all");
            }
        }
        res.put("overwrite_existing", knoxDS.get("overwriteExistingContainer"));
        return res;
    }
    
    public void setManualDistribDetails(final JSONObject apiRequest) throws Exception {
        final JSONObject requestJSON = apiRequest.getJSONObject("msg_body");
        final org.json.simple.JSONObject dat = new org.json.simple.JSONObject();
        final JSONObject obj = this.getDistribDetails(apiRequest);
        final Long customerID = APIUtil.getCustomerID(apiRequest);
        dat.put((Object)"CUSTOMER_ID", (Object)customerID);
        dat.put((Object)"OVERWRITE_EXISTING_CONTAINER", (Object)(requestJSON.has("overwrite_existing") ? requestJSON.getBoolean("overwrite_existing") : obj.getBoolean("overwrite_existing")));
        dat.put((Object)"KNOXSETTINGS_OPTION", (Object)2);
        dat.put((Object)"KNOXSETTINGS_TOGROUPONLY", (Object)false);
        final String sEventLogRemarks = "dc.mdm.android.knox.event_log.update_ds";
        final String userName = APIUtil.getUserName(apiRequest);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2064, null, userName, sEventLogRemarks, "", customerID);
        KnoxLicenseHandler.getInstance().addOrUpdateKnoxLicenseDS(dat);
    }
    
    public void setAutoDistribDetails(final JSONObject apiRequest) throws Exception {
        final JSONObject requestJSON = apiRequest.getJSONObject("msg_body");
        final org.json.simple.JSONObject dat = new org.json.simple.JSONObject();
        final JSONObject obj = this.getDistribDetails(apiRequest);
        final Long customerID = APIUtil.getCustomerID(apiRequest);
        dat.put((Object)"CUSTOMER_ID", (Object)customerID);
        dat.put((Object)"OVERWRITE_EXISTING_CONTAINER", (Object)(requestJSON.has("overwrite_existing") ? requestJSON.getBoolean("overwrite_existing") : obj.getBoolean("overwrite_existing")));
        dat.put((Object)"KNOXSETTINGS_OPTION", (Object)1);
        if (requestJSON.has("apply_to")) {
            if (String.valueOf(requestJSON.get("apply_to")).equals("groups")) {
                if (!requestJSON.has("groups")) {
                    throw new APIHTTPException("COM0005", new Object[] { "groups" });
                }
                dat.put((Object)"KNOXSETTINGS_TOGROUPONLY", (Object)true);
                final JSONArray array = requestJSON.getJSONArray("groups");
                final org.json.simple.JSONArray sarray = new org.json.simple.JSONArray();
                for (int i = 0; i < array.length(); ++i) {
                    sarray.add(i, (Object)String.valueOf(array.get(i)));
                }
                final org.json.simple.JSONArray groups = KnoxUtil.getInstance().getGroupListWithKnoxCount();
                if (groups.size() == 0) {
                    throw new APIHTTPException("KN001", new Object[] { sarray.get(0) });
                }
                for (int j = 0; j < sarray.size(); ++j) {
                    int k = -1;
                    for (int l = 0; l < groups.size(); ++l) {
                        final org.json.simple.JSONObject o = (org.json.simple.JSONObject)groups.get(l);
                        if (sarray.get(j).equals(String.valueOf(o.get((Object)"groupId")))) {
                            k = l;
                            break;
                        }
                    }
                    if (k == -1) {
                        throw new APIHTTPException("KN001", new Object[] { sarray.get(j) });
                    }
                }
                dat.put((Object)"KnoxLicenseDSToGroupRel", (Object)sarray);
            }
            else {
                dat.put((Object)"KNOXSETTINGS_TOGROUPONLY", (Object)false);
            }
            KnoxLicenseHandler.getInstance().addOrUpdateKnoxLicenseDS(dat);
            final String sEventLogRemarks = "dc.mdm.android.knox.event_log.update_ds";
            final String userName = APIUtil.getUserName(apiRequest);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2064, null, userName, sEventLogRemarks, "", customerID);
            return;
        }
        throw new APIHTTPException("COM0005", new Object[] { "apply_to" });
    }
    
    public boolean isLicenseExpired(final Long customerID) {
        final int days = KnoxLicenseHandler.getInstance().knoxLicenseDaysToExpire(customerID);
        return days < 0;
    }
    
    public boolean isConfigured(final Long customerID) {
        final Map license = KnoxLicenseHandler.getInstance().getKnoxCustomerLicense(customerID);
        return license.size() > 0;
    }
    
    public void createKnox(final JSONObject apiRequest) throws Exception {
        this.knoxAction(apiRequest, "ActivateKnox", 2051, "dc.mdm.android.knox.event_log_container_created");
    }
    
    public void removeKnox(final JSONObject apiRequest) throws Exception {
        this.knoxAction(apiRequest, "DeactivateKnox", 2064, "dc.mdm.android.knox.eventlog.container_removed");
    }
    
    private void knoxAction(final JSONObject apiRequest, final String command, final int iEventConstant, final String sEventLogRemarks) throws Exception {
        final JSONObject requestJSON = apiRequest.getJSONObject("msg_body");
        final JSONArray deviceIDs = requestJSON.getJSONArray("device_list");
        final Long customerID = APIUtil.getCustomerID(apiRequest);
        final Long userId = APIUtil.getUserID(apiRequest);
        final DeviceFacade deviceFacade = new DeviceFacade();
        for (int i = 0; i < deviceIDs.length(); ++i) {
            deviceFacade.validateIfDeviceExists(deviceIDs.getLong(i), APIUtil.getCustomerID(apiRequest));
        }
        for (int i = 0; i < deviceIDs.length(); ++i) {
            final Long resID = deviceIDs.getLong(i);
            final DeviceDetails deviceDetails = new DeviceDetails(resID);
            DeviceInvCommandHandler.getInstance().SendCommandToContainer(deviceDetails, command, userId);
            final String userName = APIUtil.getUserName(apiRequest);
            final String remarksArgs = ManagedDeviceHandler.getInstance().getDeviceName(resID);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(iEventConstant, resID, userName, sEventLogRemarks, remarksArgs, customerID);
        }
    }
    
    public void removeKnoxLicense(final JSONObject apiRequest) throws APIHTTPException {
        try {
            final Long customerID = APIUtil.getCustomerID(apiRequest);
            final SelectQuery licenseQuery = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDetail"));
            licenseQuery.addSelectColumn(new Column("KnoxLicenseDetail", "*"));
            licenseQuery.setCriteria(new Criteria(new Column("KnoxLicenseDetail", "CUSTOMER_ID"), (Object)customerID, 0));
            final DataObject dO = SyMUtil.getPersistence().get(licenseQuery);
            if (dO.isEmpty()) {
                throw new APIHTTPException("KN003", new Object[0]);
            }
            final int knoxUsedDeviceCount = KnoxUtil.getInstance().getUsedLicenseCount(customerID);
            if (knoxUsedDeviceCount > 0) {
                KnoxFacade.LOGGER.log(Level.INFO, "not deleting license because license still being used...");
                throw new APIHTTPException("KN004", new Object[0]);
            }
            final Row licenseRow = dO.getFirstRow("KnoxLicenseDetail");
            final String oldLicenseData = (String)licenseRow.get("LICENSE_DATA");
            dO.deleteRow(licenseRow);
            final String sEventLogRemarks = "dc.mdm.android.knox.event_log.remove_container_license";
            KnoxFacade.LOGGER.log(Level.INFO, "Method : removeKnoxLicense KNOX license deleted data {0}", oldLicenseData.toString());
            SyMUtil.getPersistence().update(dO);
            final Long currentlyLoggedInUserLoginId = APIUtil.getLoginID(apiRequest);
            final String userName = DMUserHandler.getDCUser(currentlyLoggedInUserLoginId);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2064, null, userName, sEventLogRemarks, "", customerID);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            KnoxFacade.LOGGER.log(Level.SEVERE, "exception in removeKnoxLicense", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
        KnoxFacade.inst = new KnoxFacade();
    }
}
