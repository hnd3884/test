package com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.zerotrust;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ZeroTrustAPIHandler
{
    private static ZeroTrustAPIHandler zeroTrustAPIHandler;
    private static final Logger LOGGER;
    
    public static ZeroTrustAPIHandler getInstance() {
        if (ZeroTrustAPIHandler.zeroTrustAPIHandler == null) {
            ZeroTrustAPIHandler.zeroTrustAPIHandler = new ZeroTrustAPIHandler();
        }
        return ZeroTrustAPIHandler.zeroTrustAPIHandler;
    }
    
    public JSONObject getSANandPasswordFromZeroTrust(final Long deviceID, final Long associatedUserID) throws Exception {
        final JSONObject scepDetails = new JSONObject();
        final int responseCode = -1;
        final String zerotrustURL = MDMUtil.getInstance().getMdUserConfigParams(associatedUserID, "zerotrust_url");
        final String authTokenKey = MDMUtil.getInstance().getMdUserConfigParams(associatedUserID, "authtoken_key");
        final String authTokenValue = MDMUtil.getInstance().getMdUserConfigParams(associatedUserID, "authtoken_value");
        if (!zerotrustURL.isEmpty() && !authTokenKey.isEmpty() && !authTokenValue.isEmpty()) {
            ZeroTrustAPIHandler.LOGGER.log(Level.INFO, "Necessary credentials are available for the user");
            final JSONObject header = this.getRequestHeader();
            header.put(authTokenKey, (Object)authTokenValue);
            final JSONObject requestBody = this.getRequestBodyForDevice(deviceID);
            final DMHttpRequest dmHttpRequest = new DMHttpRequest();
            dmHttpRequest.url = zerotrustURL;
            dmHttpRequest.method = "POST";
            dmHttpRequest.headers = header;
            dmHttpRequest.data = requestBody.toString().getBytes();
            ZeroTrustAPIHandler.LOGGER.log(Level.INFO, "Executing the API call to Zerotrust");
            final DMHttpResponse dmHttpResponse = SyMUtil.executeDMHttpRequest(dmHttpRequest);
            final String responseString = dmHttpResponse.responseBodyAsString;
            final JSONObject receivedResponse = new JSONObject(responseString);
            ZeroTrustAPIHandler.LOGGER.log(Level.INFO, "Zerotrust SCEP profile: API response status: {0}", new Object[] { dmHttpResponse.status });
            if (!responseString.isEmpty() && receivedResponse.getInt("http_response_code") == 200) {
                ZeroTrustAPIHandler.LOGGER.log(Level.INFO, "Zerotrust SCEP profile: San and Password obtained for device: {0}", new Object[] { deviceID });
                scepDetails.put("ZEROTRUST_SAN", (Object)receivedResponse.getString("san"));
                scepDetails.put("ZEROTRUST_PASSWORD", (Object)receivedResponse.getString("password"));
                scepDetails.put("http_response_code", receivedResponse.getInt("http_response_code"));
                return scepDetails;
            }
            ZeroTrustAPIHandler.LOGGER.log(Level.INFO, "Zerotrust SCEP profile: API request failed: Response obtained: {0}", new Object[] { responseString });
        }
        else {
            ZeroTrustAPIHandler.LOGGER.log(Level.INFO, "Zerotrust SCEP profile: Zerotrust credentials not found for this user: {0}", new Object[] { associatedUserID });
        }
        ZeroTrustAPIHandler.LOGGER.log(Level.INFO, "Zerotrust SCEP profile: User ID: {0}, Device ID: {1}", new Object[] { associatedUserID, deviceID });
        scepDetails.put("http_response_code", responseCode);
        return scepDetails;
    }
    
    private JSONObject getRequestHeader() {
        final JSONObject header = new JSONObject();
        header.put("X-HTTP-Method-Override", (Object)"ADD_DEVICE_AND_ENROLL");
        return header;
    }
    
    private JSONObject getRequestBodyForDevice(final Long deviceID) throws Exception {
        ZeroTrustAPIHandler.LOGGER.log(Level.INFO, "Getting details for Device ID: {0}", new Object[] { deviceID });
        final DMDataSetWrapper detailsSet = this.getDetailsForDevice(deviceID);
        final JSONObject deviceBody = new JSONObject();
        while (detailsSet.next()) {
            ZeroTrustAPIHandler.LOGGER.log(Level.INFO, "Device details available");
            final String emailAddress = (String)detailsSet.getValue("EMAIL_ADDRESS");
            final String deviceName = (String)detailsSet.getValue("NAME");
            final Integer deviceType = (Integer)detailsSet.getValue("RESOURCE_TYPE");
            final String deviceUDID = (String)detailsSet.getValue("UDID");
            final String manufacturer = (String)detailsSet.getValue("MANUFACTURER");
            final String modelName = (String)detailsSet.getValue("MODEL_NAME");
            final JSONObject device = new JSONObject();
            device.put("device_name", (Object)deviceName);
            device.put("device_model", (Object)modelName);
            device.put("device_manufacturer", (Object)manufacturer);
            device.put("device_uuid", (Object)deviceUDID);
            if (deviceType == 120) {
                device.put("mobile", (Object)new JSONObject());
            }
            else if (deviceType == 121) {
                device.put("laptop", (Object)new JSONObject());
            }
            deviceBody.put("device", (Object)device);
            deviceBody.put("emailId", (Object)emailAddress);
        }
        return deviceBody;
    }
    
    private DMDataSetWrapper getDetailsForDevice(final Long deviceID) throws Exception {
        final SelectQuery deviceDetailsQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        final Join resourceToManagedDeviceJoin = new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join managedDeviceToManagedUsertoDeviceJoin = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join managedUsertoDeviceToManagedUserJoin = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
        final Join managedDeviceToMdDeviceInfoJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join mdDeviceInfoToMdModelInfoJoin = new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2);
        final Criteria deviceIDCriteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)deviceID, 0);
        deviceDetailsQuery.addJoin(resourceToManagedDeviceJoin);
        deviceDetailsQuery.addJoin(managedDeviceToManagedUsertoDeviceJoin);
        deviceDetailsQuery.addJoin(managedUsertoDeviceToManagedUserJoin);
        deviceDetailsQuery.addJoin(managedDeviceToMdDeviceInfoJoin);
        deviceDetailsQuery.addJoin(mdDeviceInfoToMdModelInfoJoin);
        deviceDetailsQuery.setCriteria(deviceIDCriteria);
        deviceDetailsQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        deviceDetailsQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_TYPE"));
        deviceDetailsQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        deviceDetailsQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        deviceDetailsQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_NAME"));
        deviceDetailsQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MANUFACTURER"));
        ZeroTrustAPIHandler.LOGGER.log(Level.INFO, "Getting the necessary device details");
        return DMDataSetWrapper.executeQuery((Object)deviceDetailsQuery);
    }
    
    static {
        ZeroTrustAPIHandler.zeroTrustAPIHandler = null;
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
