package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.Properties;
import org.json.JSONException;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class VPPResponseProcessor
{
    private static VPPResponseProcessor processor;
    public Logger logger;
    
    public VPPResponseProcessor() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    public static VPPResponseProcessor getInstance() {
        if (VPPResponseProcessor.processor == null) {
            VPPResponseProcessor.processor = new VPPResponseProcessor();
        }
        return VPPResponseProcessor.processor;
    }
    
    public Object processResponse(final JSONObject responseJSON, final String vppService) throws JSONException {
        final HashMap responseMap = JSONUtil.getInstance().ConvertJSONObjectToHash(responseJSON);
        if (vppService.equalsIgnoreCase("registerUserSrvUrl")) {
            return this.processRegisterUserResponse(responseMap);
        }
        if (vppService.equalsIgnoreCase("retireUserSrvUrl")) {
            return this.processRetireUserResponse(responseMap);
        }
        if (vppService.equalsIgnoreCase("getUsersSrvUrl")) {
            return this.processGetVPPUsersResponse(responseMap);
        }
        if (vppService.equalsIgnoreCase("getUserSrvUrl")) {
            return this.processGetVPPUserResponse(responseMap);
        }
        if (vppService.equalsIgnoreCase("getVPPLicensesSrvForAdamId")) {
            return this.processGetVPPLicenseForAdamIdResponse(responseMap);
        }
        if (vppService.equalsIgnoreCase("getLicensesSrvUrl")) {
            return this.processGetVPPLicenseResponse(responseMap);
        }
        if (vppService.equalsIgnoreCase("manageVPPLicensesByAdamIdSrvUrl")) {
            return this.processAssociateDisassociateAdamIdResponse(responseMap);
        }
        if (vppService.equalsIgnoreCase("getVPPAssetsSrvUrl")) {
            return this.processVPPAssetsResponse(responseMap);
        }
        if (vppService.equalsIgnoreCase("clientConfigSrvUrl")) {
            return this.processVPPClientConfigResponse(responseMap);
        }
        return null;
    }
    
    private Properties processRegisterUserResponse(final HashMap responseMap) throws JSONException {
        final Properties prop = new Properties();
        final Boolean isError = this.checkIfError(responseMap);
        if (isError) {
            ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 4);
            ((Hashtable<String, Object>)prop).put("REMARKS", responseMap.get("errorMessage"));
        }
        else {
            final JSONObject userJson = new JSONObject((String)responseMap.get("user"));
            final HashMap userMap = JSONUtil.getInstance().ConvertJSONObjectToHash(userJson);
            ((Hashtable<String, String>)prop).put("VPP_USER_ID", userMap.get("userId"));
            ((Hashtable<String, String>)prop).put("clientUserIdStr", userMap.get("clientUserIdStr").toString());
            ((Hashtable<String, Object>)prop).put("INVITATION_CODE", userMap.get("inviteCode"));
            ((Hashtable<String, Object>)prop).put("INVITATION_URL", userMap.get("inviteUrl"));
            ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 1);
            ((Hashtable<String, String>)prop).put("REMARKS", "--");
        }
        return prop;
    }
    
    private boolean checkIfError(final HashMap responseMap) {
        final int errorStatus = Integer.parseInt(responseMap.get("status").toString());
        if (errorStatus == 0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    private Properties processRetireUserResponse(final HashMap responseMap) throws JSONException {
        final Properties prop = new Properties();
        final Boolean isError = this.checkIfError(responseMap);
        if (isError) {
            final Integer errorNum = Integer.parseInt(responseMap.get("errorNumber"));
            if (errorNum == 9618) {
                ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 3);
                ((Hashtable<String, Object>)prop).put("REMARKS", responseMap.get("errorMessage"));
            }
            else {
                ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 5);
                ((Hashtable<String, Object>)prop).put("REMARKS", responseMap.get("errorMessage"));
            }
        }
        else {
            ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 3);
            ((Hashtable<String, String>)prop).put("REMARKS", "dc.mdm.app.retier_vpp.remarks");
        }
        return prop;
    }
    
    private HashMap<String, Object> processGetVPPUsersResponse(final HashMap responseMap) throws JSONException {
        final HashMap<String, Object> usersResponseMap = new HashMap<String, Object>();
        final HashMap<Long, Properties> userdetailsMap = new HashMap<Long, Properties>();
        final Boolean isError = this.checkIfError(responseMap);
        if (!isError) {
            final String sinceModifiedToken = responseMap.get("sinceModifiedToken");
            if (sinceModifiedToken != null) {
                usersResponseMap.put("sinceModifiedToken", sinceModifiedToken);
            }
            final String batchToken = responseMap.get("batchToken");
            if (batchToken != null) {
                usersResponseMap.put("batchToken", batchToken);
            }
            final String userArrayStr = responseMap.get("users");
            if (userArrayStr != null) {
                final JSONArray userArray = new JSONArray(userArrayStr);
                for (int i = 0; i < userArray.length(); ++i) {
                    final JSONObject userJSON = userArray.getJSONObject(i);
                    final HashMap userMap = JSONUtil.getInstance().ConvertJSONObjectToHash(userJSON);
                    final Properties prop = new Properties();
                    final Long userId = Long.parseLong(userMap.get("userId"));
                    ((Hashtable<String, Long>)prop).put("VPP_USER_ID", userId);
                    final String statusStr = userMap.get("status");
                    final int status = this.getUserStatus(statusStr);
                    if (status == 1) {
                        ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 1);
                        ((Hashtable<String, String>)prop).put("INVITATION_CODE", userMap.get("inviteCode"));
                        ((Hashtable<String, String>)prop).put("INVITATION_URL", userMap.get("inviteUrl"));
                        ((Hashtable<String, String>)prop).put("REMARKS", "dc.mdm.vpp.registered_remarks");
                    }
                    else if (status == 2) {
                        ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 2);
                        ((Hashtable<String, String>)prop).put("REMARKS", "dc.mdm.vpp.associated_remarks");
                    }
                    else if (status == 3) {
                        ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 3);
                        ((Hashtable<String, String>)prop).put("REMARKS", "dc.mdm.vpp.associated_remarks");
                    }
                    userdetailsMap.put(userId, prop);
                }
            }
            usersResponseMap.put("DETAILS_MAP", userdetailsMap);
        }
        return usersResponseMap;
    }
    
    private Properties processGetVPPUserResponse(final HashMap responseMap) throws JSONException {
        final Boolean isError = this.checkIfError(responseMap);
        final Properties prop = new Properties();
        if (!isError) {
            final JSONObject userJSON = new JSONObject((String)responseMap.get("user"));
            final HashMap userMap = JSONUtil.getInstance().ConvertJSONObjectToHash(userJSON);
            final Long userId = Long.parseLong(userMap.get("userId"));
            ((Hashtable<String, Long>)prop).put("VPP_USER_ID", userId);
            final String statusStr = userMap.get("status");
            final int status = this.getUserStatus(statusStr);
            if (status == 1) {
                ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 1);
                ((Hashtable<String, String>)prop).put("INVITATION_CODE", userMap.get("inviteCode"));
                ((Hashtable<String, String>)prop).put("INVITATION_URL", userMap.get("inviteUrl"));
            }
            else if (status == 2) {
                ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 2);
                ((Hashtable<String, String>)prop).put("ITS_ID_HASH", userMap.get("itsIdHash"));
            }
            else if (status == 3) {
                ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 3);
            }
        }
        return prop;
    }
    
    private HashMap<String, Object> processGetVPPLicenseForAdamIdResponse(final HashMap responseMap) throws JSONException {
        final HashMap<String, Object> licensesResponseMap = new HashMap<String, Object>();
        final ArrayList<String> userList = new ArrayList<String>();
        final ArrayList<String> deviceList = new ArrayList<String>();
        final Boolean isError = this.checkIfError(responseMap);
        if (!isError) {
            final String sinceModifiedToken = responseMap.get("sinceModifiedToken");
            if (sinceModifiedToken != null) {
                licensesResponseMap.put("sinceModifiedToken", sinceModifiedToken);
            }
            final String batchToken = responseMap.get("batchToken");
            if (batchToken != null) {
                licensesResponseMap.put("batchToken", batchToken);
            }
            final String licenseArrayStr = responseMap.get("licenses");
            if (licenseArrayStr != null) {
                final JSONArray licenseArray = new JSONArray(licenseArrayStr);
                for (int i = 0; i < licenseArray.length(); ++i) {
                    final JSONObject licenseJSON = licenseArray.getJSONObject(i);
                    final HashMap licenseMap = JSONUtil.getInstance().ConvertJSONObjectToHash(licenseJSON);
                    final String userId = licenseMap.get("clientUserIdStr");
                    if (userId != null) {
                        userList.add(userId);
                    }
                    final String serialNo = licenseMap.get("serialNumber");
                    if (serialNo != null) {
                        deviceList.add(serialNo);
                    }
                }
            }
            licensesResponseMap.put("user", userList);
            licensesResponseMap.put("device", deviceList);
        }
        else {
            licensesResponseMap.put("errorNumber", Integer.parseInt(responseMap.get("errorNumber").toString()));
            licensesResponseMap.put("errorMessage", responseMap.get("errorMessage").toString());
        }
        return licensesResponseMap;
    }
    
    private int getUserStatus(final String statusStr) {
        if (statusStr.equalsIgnoreCase("Registered")) {
            return 1;
        }
        if (statusStr.equalsIgnoreCase("Associated")) {
            return 2;
        }
        if (statusStr.equalsIgnoreCase("Retired")) {
            return 3;
        }
        return 0;
    }
    
    private HashMap processGetVPPLicenseResponse(final HashMap responseMap) throws JSONException {
        final HashMap<String, Object> licenseDetails = new HashMap<String, Object>();
        final Boolean isError = this.checkIfError(responseMap);
        if (!isError) {
            final String sinceModifiedToken = responseMap.get("sinceModifiedToken");
            if (sinceModifiedToken != null) {
                licenseDetails.put("sinceModifiedToken", sinceModifiedToken);
            }
            final String batchToken = responseMap.get("batchToken");
            if (batchToken != null) {
                licenseDetails.put("batchToken", batchToken);
            }
            String adamId = null;
            final String licenseArrayStr = responseMap.get("licenses");
            if (licenseArrayStr != null) {
                final JSONArray licenseArray = new JSONArray(licenseArrayStr);
                for (int i = 0; i < licenseArray.length(); ++i) {
                    final JSONObject licenseJSON = licenseArray.getJSONObject(i);
                    final HashMap licenseMap = JSONUtil.getInstance().ConvertJSONObjectToHash(licenseJSON);
                    adamId = licenseMap.get("adamId");
                    final String userId = licenseMap.get("clientUserIdStr");
                    HashMap<String, Object> tempAppLicenseDetails = licenseDetails.getOrDefault(adamId, null);
                    if (tempAppLicenseDetails == null) {
                        tempAppLicenseDetails = new HashMap<String, Object>();
                    }
                    List userList = tempAppLicenseDetails.getOrDefault("user", null);
                    List serialNoList = tempAppLicenseDetails.getOrDefault("user", null);
                    if (userId != null) {
                        if (userList == null) {
                            userList = new ArrayList();
                        }
                        userList.add(userId);
                    }
                    final String serialNo = licenseMap.get("serialNumber");
                    if (serialNo != null) {
                        if (serialNoList == null) {
                            serialNoList = new ArrayList();
                        }
                        serialNoList.add(serialNo);
                    }
                    tempAppLicenseDetails.put("user", userList);
                    tempAppLicenseDetails.put("device", serialNoList);
                    licenseDetails.put(adamId, tempAppLicenseDetails);
                }
            }
        }
        else {
            licenseDetails.put("errorNumber", Integer.parseInt(responseMap.get("errorNumber").toString()));
            licenseDetails.put("errorMessage", responseMap.get("errorMessage").toString());
        }
        return licenseDetails;
    }
    
    private Properties processAssociateDisassociateAdamIdResponse(final HashMap responseMap) throws JSONException {
        Properties prop = null;
        final int status = Integer.parseInt(responseMap.get("status").toString());
        if (status == -1 && !responseMap.containsKey("associations") && !responseMap.containsKey("disassociations")) {
            prop = new Properties();
            ((Hashtable<String, Integer>)prop).put("errorNumber", Integer.parseInt(responseMap.get("errorNumber").toString()));
            ((Hashtable<String, String>)prop).put("errorMessage", responseMap.get("errorMessage").toString());
            return prop;
        }
        if (responseMap.containsKey("associations")) {
            prop = this.processResponseDetails(responseMap, "associations");
        }
        else if (responseMap.containsKey("disassociations")) {
            prop = this.processResponseDetails(responseMap, "disassociations");
        }
        return prop;
    }
    
    private Properties processResponseDetails(final HashMap responseMap, final String process) throws JSONException {
        final Properties prop = new Properties();
        if (responseMap.containsKey("adamIdStr")) {
            ((Hashtable<String, String>)prop).put("adamIdStr", responseMap.get("adamIdStr").toString());
            ((Hashtable<String, String>)prop).put("pricingParam", responseMap.get("pricingParam").toString());
            ((Hashtable<String, Integer>)prop).put("productTypeId", Integer.parseInt(responseMap.get("productTypeId").toString()));
            ((Hashtable<String, String>)prop).put("productTypeName", responseMap.get("productTypeName").toString());
            ((Hashtable<String, Boolean>)prop).put("isIrrevocable", Boolean.parseBoolean(responseMap.get("isIrrevocable").toString()));
            ((Hashtable<String, Integer>)prop).put("status", Integer.parseInt(responseMap.get("status").toString()));
        }
        String operationsKey = null;
        JSONArray operationsArray = null;
        if (process.equals("associations")) {
            operationsArray = new JSONArray(responseMap.get("associations").toString());
            operationsKey = "associations";
        }
        else if (process.equals("disassociations")) {
            operationsArray = new JSONArray(responseMap.get("disassociations").toString());
            operationsKey = "disassociations";
        }
        final List<Properties> propList = new ArrayList<Properties>();
        for (int i = 0; i < operationsArray.length(); ++i) {
            final Properties operationProps = new Properties();
            final JSONObject operationsObject = operationsArray.getJSONObject(i);
            if (operationsObject.has("clientUserIdStr")) {
                ((Hashtable<String, String>)operationProps).put("clientUserIdStr", operationsObject.get("clientUserIdStr").toString());
            }
            if (operationsObject.has("licenseIdStr")) {
                ((Hashtable<String, String>)operationProps).put("licenseIdStr", operationsObject.get("licenseIdStr").toString());
            }
            if (operationsObject.has("serialNumber")) {
                ((Hashtable<String, String>)operationProps).put("serialNumber", operationsObject.get("serialNumber").toString());
            }
            if (operationsObject.has("errorMessage")) {
                ((Hashtable<String, String>)operationProps).put("errorMessage", operationsObject.get("errorMessage").toString());
            }
            if (operationsObject.has("errorCode")) {
                ((Hashtable<String, Integer>)operationProps).put("errorCode", Integer.parseInt(operationsObject.get("errorCode").toString()));
            }
            if (operationsObject.has("errorNumber")) {
                ((Hashtable<String, Integer>)operationProps).put("errorNumber", Integer.parseInt(operationsObject.get("errorNumber").toString()));
            }
            propList.add(operationProps);
        }
        ((Hashtable<String, List<Properties>>)prop).put(operationsKey, propList);
        return prop;
    }
    
    private HashMap processVPPAssetsResponse(final HashMap responseMap) throws JSONException {
        final HashMap<String, Properties> appDetailsMap = new HashMap<String, Properties>();
        final Boolean isError = this.checkIfError(responseMap);
        if (!isError) {
            final String appDetailsStr = responseMap.get("assets");
            if (appDetailsStr != null) {
                final JSONArray appDetailsArray = new JSONArray(appDetailsStr);
                for (int i = 0; i < appDetailsArray.length(); ++i) {
                    final Properties prop = new Properties();
                    final JSONObject appDetailsJSON = appDetailsArray.getJSONObject(i);
                    final String adamId = appDetailsJSON.get("adamIdStr").toString();
                    final Integer assignedAppCount = Integer.parseInt(appDetailsJSON.get("assignedCount").toString());
                    final Integer availableAppCount = Integer.parseInt(appDetailsJSON.get("availableCount").toString());
                    final Integer retiredCount = Integer.parseInt(appDetailsJSON.get("retiredCount").toString());
                    final Integer productTypeId = Integer.parseInt(appDetailsJSON.get("productTypeId").toString());
                    final String pricingParam = appDetailsJSON.get("pricingParam").toString();
                    final Integer quality = pricingParam.equals("STDQ") ? new Integer(1) : new Integer(1);
                    final Boolean deviceAssignable = Boolean.parseBoolean(appDetailsJSON.get("deviceAssignable").toString());
                    final Integer totalAppCount = Integer.parseInt(appDetailsJSON.get("totalCount").toString());
                    final Boolean isIrrevocable = Boolean.parseBoolean(appDetailsJSON.get("isIrrevocable").toString());
                    ((Hashtable<String, Boolean>)prop).put("IS_IRREVOCABLE", isIrrevocable);
                    ((Hashtable<String, Integer>)prop).put("ASSIGNED_LICENSE_COUNT", assignedAppCount);
                    ((Hashtable<String, Integer>)prop).put("AVAILABLE_LICENSE_COUNT", availableAppCount);
                    ((Hashtable<String, Integer>)prop).put("RETIRED_COUNT", retiredCount);
                    ((Hashtable<String, Integer>)prop).put("PRICING_PARAM", quality);
                    ((Hashtable<String, Boolean>)prop).put("IS_DEVICE_ASSIGNABLE", deviceAssignable);
                    ((Hashtable<String, Integer>)prop).put("TOTAL_LICENSE", totalAppCount);
                    ((Hashtable<String, Integer>)prop).put("ASSET_TYPE", productTypeId);
                    appDetailsMap.put(adamId, prop);
                }
            }
        }
        else {
            final Properties prop2 = new Properties();
            ((Hashtable<String, Integer>)prop2).put("errorNumber", Integer.parseInt(responseMap.get("errorNumber").toString()));
            ((Hashtable<String, String>)prop2).put("errorMessage", responseMap.get("errorMessage").toString());
            appDetailsMap.put("error", prop2);
        }
        return appDetailsMap;
    }
    
    private Properties processVPPClientConfigResponse(final HashMap responseMap) throws JSONException {
        final Properties prop = new Properties();
        final Boolean isError = this.checkIfError(responseMap);
        String clientContext = "";
        if (!isError) {
            final String vppCountryCode = responseMap.get("countryCode");
            final String appleId = responseMap.get("appleId");
            final String mailId = responseMap.get("email");
            final String uniqueId = responseMap.get("uId");
            final String orgId = responseMap.get("organizationId");
            final String defaultPlatform = responseMap.get("defaultPlatform");
            final String orgIDHash = responseMap.get("organizationIdHash");
            if (vppCountryCode != null) {
                ((Hashtable<String, String>)prop).put("COUNTRY_CODE", vppCountryCode.toString());
            }
            if (appleId != null) {
                ((Hashtable<String, String>)prop).put("APPLEID", appleId.toString());
            }
            if (mailId != null) {
                ((Hashtable<String, String>)prop).put("EMAIL", mailId.toString());
            }
            if (uniqueId != null) {
                ((Hashtable<String, String>)prop).put("UNIQUE_ID", uniqueId);
            }
            if (orgId != null) {
                ((Hashtable<String, String>)prop).put("ORGANIZATION_ID", orgId);
            }
            if (defaultPlatform != null) {
                if (defaultPlatform.equalsIgnoreCase("enterprisestore")) {
                    ((Hashtable<String, Integer>)prop).put("DEFAULT_PLATFORM", 0);
                }
                else {
                    ((Hashtable<String, Integer>)prop).put("DEFAULT_PLATFORM", 0);
                }
            }
            if (orgIDHash != null) {
                ((Hashtable<String, String>)prop).put("ORG_ID_HASH", orgIDHash);
            }
            this.setLocationDetainsInProp(prop, responseMap);
            final Object clientContextObj = responseMap.get("clientContext");
            if (clientContextObj != null) {
                clientContext = clientContextObj.toString();
            }
            ((Hashtable<String, String>)prop).put("clientContext", clientContext);
        }
        else {
            ((Hashtable<String, Integer>)prop).put("errorNumber", Integer.parseInt(responseMap.get("errorNumber").toString()));
            ((Hashtable<String, String>)prop).put("errorMessage", responseMap.get("errorMessage").toString());
        }
        return prop;
    }
    
    private Properties setLocationDetainsInProp(final Properties prop, final HashMap responseMap) {
        try {
            if (responseMap.get("location") != null) {
                final JSONObject json = new JSONObject(responseMap.get("location").toString());
                ((Hashtable<String, Object>)prop).put("LOCATION_NAME", json.get("locationName"));
                ((Hashtable<String, String>)prop).put("LOCATION_ID", json.get("locationId").toString());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "VPPResponseProcessor: Exception in setLocationDetainsInProp() ", ex);
        }
        return prop;
    }
    
    public JSONObject getNewVPPClientContextJSON(final JSONObject vppResponse) {
        final JSONObject clientContextJSON = new JSONObject();
        try {
            if (vppResponse != null && vppResponse.has("mdmInfo")) {
                final JSONObject mdmInfo = vppResponse.getJSONObject("mdmInfo");
                this.logger.log(Level.INFO, "MDMInfo: {0}", new Object[] { mdmInfo });
                if (mdmInfo.has("id")) {
                    clientContextJSON.put("guid", (Object)mdmInfo.getString("id"));
                    if (mdmInfo.has("name")) {
                        clientContextJSON.put("hostname", (Object)mdmInfo.getString("name"));
                    }
                }
                else {
                    this.logger.log(Level.INFO, "MDMInfo has no ID");
                }
            }
            else {
                this.logger.log(Level.INFO, "No MDMInfo available");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception in getNewVPPClientContextJSON", e);
        }
        return clientContextJSON;
    }
    
    static {
        VPPResponseProcessor.processor = null;
    }
}
