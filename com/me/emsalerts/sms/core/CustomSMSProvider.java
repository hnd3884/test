package com.me.emsalerts.sms.core;

import java.net.HttpURLConnection;
import java.util.Iterator;
import sun.misc.BASE64Encoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Properties;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.alerts.sms.SMSAPI;

public class CustomSMSProvider implements SMSAPI
{
    private static Logger logger;
    
    public boolean isSMSSettingsConfigured() {
        boolean isCustomSMSGatewayConfigured = false;
        try {
            final HashMap smsConfiguration = SMSUtil.checkSMSConfig();
            if (smsConfiguration != null && !smsConfiguration.isEmpty()) {
                final Long smsConfigID = smsConfiguration.get("smsConfigID");
                final String smsConfigType = smsConfiguration.get("smsConfigType");
                if (smsConfigID != null && smsConfigType != null && smsConfigType.equalsIgnoreCase("smsGateway")) {
                    final HashMap smsServices = SMSUtil.getServiceDetails(smsConfigID);
                    final Long serviceID = smsServices.get("serviceID");
                    final String serviceName = smsServices.get("serviceName");
                    if (serviceID != null && serviceName.equalsIgnoreCase("custom")) {
                        isCustomSMSGatewayConfigured = true;
                    }
                }
            }
        }
        catch (final Exception ex) {
            CustomSMSProvider.logger.log(Level.WARNING, "Exception occured in isSMSSettingsConfigured() method", ex);
        }
        return isCustomSMSGatewayConfigured;
    }
    
    public Properties getSMSConfigurationSettings() {
        final Properties response = new Properties();
        final Properties smsConfigurationSettings = new Properties();
        try {
            final HashMap smsConfiguration = SMSUtil.checkSMSConfig();
            if (smsConfiguration != null) {
                final String smsConfigType = smsConfiguration.get("smsConfigType");
                final Long smsConfigID = smsConfiguration.get("smsConfigID");
                final HashMap smsServices = SMSUtil.getServiceDetails(smsConfigID);
                final Long serviceID = smsServices.get("serviceID");
                final String serviceName = smsServices.get("serviceName");
                final Long actionId = SMSUtil.getActionID(serviceID, "sendsms");
                ((Hashtable<String, String>)smsConfigurationSettings).put("configType", smsConfigType);
                ((Hashtable<String, String>)smsConfigurationSettings).put("serviceName", serviceName);
                ((Hashtable<String, Hashtable>)smsConfigurationSettings).put("actions", SMSUtil.getSMSActionConfigDetails(actionId));
            }
        }
        catch (final Exception e) {
            CustomSMSProvider.logger.log(Level.WARNING, "Exception while getting SMS configurations Settings ", e);
            ((Hashtable<String, String>)response).put("status", "error");
            ((Hashtable<String, String>)response).put("message", e.getMessage());
        }
        return smsConfigurationSettings;
    }
    
    public HashMap setSMSConfigurationSettings(final JSONObject smsConfigurations) {
        final HashMap response = new HashMap();
        try {
            if (this.isSMSSettingsConfigured()) {
                CustomSMSProvider.logger.log(Level.INFO, "SMS Gateway settings are already configured, going to update the configuration....");
                final HashMap smsConfiguration = SMSUtil.checkSMSConfig();
                final Long smsConfigID = smsConfiguration.get("smsConfigID");
                final HashMap smsServices = SMSUtil.getServiceDetails(smsConfigID);
                if (smsServices != null) {
                    final Long serviceID = smsServices.get("serviceID");
                    final Long actionID = SMSUtil.getActionID(serviceID, "sendsms");
                    SMSUtil.saveServiceActionReqConfigDetails(actionID, smsConfigurations);
                    response.put("status", "success");
                    response.put("message", "SMS Settings updated successfully");
                }
            }
            else {
                CustomSMSProvider.logger.log(Level.INFO, "SMS Gateway is not configured.Going to add SMS Configurations in DB...");
                final String config_type = "smsGateway";
                final String serviceName = "custom";
                SMSUtil.saveSmsConfigDetails(config_type);
                final HashMap smsConfiguration2 = SMSUtil.checkSMSConfig();
                final Long smsConfigID2 = smsConfiguration2.get("smsConfigID");
                SMSUtil.saveServiceDetails(smsConfigID2, serviceName);
                final HashMap smsServices2 = SMSUtil.getServiceDetails(smsConfigID2);
                final Long serviceID2 = smsServices2.get("serviceID");
                SMSUtil.saveServiceActionDetails("sendsms", serviceID2);
                final Long actionID2 = SMSUtil.getActionID(serviceID2, "sendsms");
                SMSUtil.saveServiceActionReqConfigDetails(actionID2, smsConfigurations);
                response.put("status", "success");
                response.put("message", "SMS Settings updated successfully");
            }
        }
        catch (final Exception e) {
            CustomSMSProvider.logger.log(Level.WARNING, "Exception  occured while updating SMS Settings ", e);
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    public JSONObject sendHTTPToSMS(final Properties smsProperties) {
        final JSONArray sendSMSResponses = new JSONArray();
        final JSONObject response = new JSONObject();
        try {
            final HashMap smsConfiguration = SMSUtil.checkSMSConfig();
            final Long smsConfigID = smsConfiguration.get("smsConfigID");
            final HashMap smsServices = SMSUtil.getServiceDetails(smsConfigID);
            if (smsServices != null) {
                final Long serviceID = smsServices.get("serviceID");
                final Long actionID = SMSUtil.getActionID(serviceID, "sendsms");
                final Hashtable smsActionConfig = SMSUtil.getSMSActionConfigDetails(actionID);
                final String numbers = ((Hashtable<K, String>)smsProperties).get("recipient");
                final String[] mobNumber = numbers.split(",");
                String message = ((Hashtable<K, String>)smsProperties).get("message");
                if (smsActionConfig.get("unicode") instanceof Hashtable) {
                    final Hashtable unicode = smsActionConfig.get("unicode");
                    final boolean unicodeEnabled = unicode.get("isConfigured") != null && unicode.get("isConfigured");
                    if (unicodeEnabled) {
                        message = message.replaceAll("\\\\t", " ");
                        message = message.replaceAll("\\\\n", "\n");
                        message = SMSUtil.convertToUnicode(message);
                    }
                }
                for (int i = 0; i < mobNumber.length; ++i) {
                    final JSONObject sendSMSResponse = this.sendMessage(smsActionConfig, mobNumber[i], message);
                    sendSMSResponses.put((Object)sendSMSResponse);
                }
                response.put("successResponse", (Object)sendSMSResponses);
            }
        }
        catch (final Exception e) {
            CustomSMSProvider.logger.log(Level.WARNING, "Exception Ocuured while sending SMS", e);
        }
        return response;
    }
    
    public int getRemainingCredits() {
        return 0;
    }
    
    public JSONObject sendMessage(final Hashtable smsActionConfig, final String mobNumber, final String messages) throws Exception {
        final JSONObject resultJson = new JSONObject();
        final JSONObject inputProperties = new JSONObject();
        try {
            inputProperties.put("recipient", (Object)mobNumber);
            inputProperties.put("message", (Object)messages);
            List queryParamsList = new ArrayList();
            if (smsActionConfig.containsKey("queryParams") && smsActionConfig.get("queryParams") instanceof List) {
                queryParamsList = smsActionConfig.get("queryParams");
            }
            final LinkedHashMap requestParams = smsActionConfig.get("requestParams");
            String url = requestParams.get("url");
            final String requestType = requestParams.containsKey("httpMethod") ? requestParams.get("httpMethod") : "GET";
            JSONObject requestHeadersObj = new JSONObject();
            if (smsActionConfig.containsKey("requestHeaders") && smsActionConfig.get("requestHeaders") instanceof List) {
                final List requestHeadersList = smsActionConfig.get("requestHeaders");
                requestHeadersObj = SMSUtil.getInstance().constructHeaderObjFromList(requestHeadersList, inputProperties);
            }
            LinkedHashMap authDetails = new LinkedHashMap();
            if (smsActionConfig.containsKey("authDetails") && smsActionConfig.get("authDetails") instanceof LinkedHashMap) {
                authDetails = smsActionConfig.get("authDetails");
            }
            LinkedHashMap responseFormat = new LinkedHashMap();
            url = SMSUtil.getInstance().setQueryParamsInConnection(queryParamsList, inputProperties, url);
            final HashMap connectionDetails = new HashMap();
            connectionDetails.put("url", url);
            connectionDetails.put("requestType", requestType);
            connectionDetails.put("headers", requestHeadersObj);
            boolean isHttps = false;
            if (url.toLowerCase().startsWith("https:")) {
                isHttps = true;
            }
            if (requestType.equalsIgnoreCase("post") && smsActionConfig.containsKey("requestPayload") && smsActionConfig.get("requestPayload") instanceof LinkedHashMap) {
                final LinkedHashMap requestPayload = smsActionConfig.get("requestPayload");
                final String type = requestPayload.get("type");
                final String postData = SMSUtil.replacePlaceHolders(requestPayload.get("data").toString(), inputProperties);
                if (type.equals("RAW_JSON")) {
                    connectionDetails.put("Content-Type", "application/json;charset=UTF-8");
                    connectionDetails.put("content", postData);
                }
                if (type.equals("FORM_ENCODE")) {
                    String content = "";
                    final JSONObject dataJson = new JSONObject(postData);
                    final Iterator<String> urlParamItr = dataJson.keys();
                    while (urlParamItr.hasNext()) {
                        final String key = urlParamItr.next();
                        if (dataJson.get(key) instanceof String) {
                            final String value = dataJson.getString(key);
                            content = content + "&" + key + "=" + URLEncoder.encode(value, "UTF-8");
                        }
                        else {
                            final String value = String.valueOf(dataJson.get(key));
                            content = content + "&" + key + "=" + URLEncoder.encode(value, "UTF-8");
                        }
                    }
                    connectionDetails.put("content", content);
                }
            }
            final String authMechanism = authDetails.get("authMechanism");
            if (!authMechanism.isEmpty() && authMechanism.equalsIgnoreCase("Basic")) {
                final String userName = authDetails.get("username");
                final String passWord = authDetails.get("password");
                final String userpassword = userName + ":" + passWord;
                final String encodedAuthorization = new BASE64Encoder().encode(userpassword.getBytes());
                final String authorizationData = "Basic " + encodedAuthorization;
                connectionDetails.put("authorization", authorizationData);
            }
            else {
                CustomSMSProvider.logger.log(Level.FINE, "Auth mechanism other than 'Basic' yet to be handled");
            }
            final HttpURLConnection connection = SMSUtil.getResponseAsConnection(connectionDetails, isHttps);
            String responseData = SMSUtil.getResponseFromConnection(connection);
            final String responseMessage = connection.getResponseMessage();
            final int responseCode = connection.getResponseCode();
            final String contentEncodingType = connection.getContentEncoding();
            final String contentType = connection.getContentType();
            resultJson.put("number", (Object)mobNumber);
            String successsResponse = null;
            if (smsActionConfig.get("responseFormat") instanceof LinkedHashMap) {
                responseFormat = smsActionConfig.get("responseFormat");
                successsResponse = ((responseFormat.get("response") != null) ? responseFormat.get("response").toString() : "SUCCESS");
            }
            final String status = this.getSendSMSStatusMessage(responseData, responseCode, successsResponse);
            resultJson.put("smsStatus", (Object)status);
            try {
                resultJson.put("apiResponse", (Object)new JSONObject(responseData));
            }
            catch (final Exception e) {
                try {
                    responseData = responseData.replaceAll("\\\\", "");
                    resultJson.put("apiResponse", (Object)new JSONObject(responseData));
                }
                catch (final Exception je) {
                    resultJson.put("apiResponse", (Object)responseData);
                }
            }
            finally {
                connection.disconnect();
            }
            resultJson.put("httpStatusMessage", (Object)responseMessage);
            resultJson.put("httpStatusCode", responseCode);
            resultJson.put("contentType", (Object)contentType);
            resultJson.put("encodingType", (Object)contentEncodingType);
        }
        catch (final Exception e2) {
            CustomSMSProvider.logger.log(Level.INFO, "Exception occured while sending SMS", e2);
            throw e2;
        }
        return resultJson;
    }
    
    public String getSendSMSStatusMessage(String responseMessage, final int httpResponseCode, String successResp) {
        String status = null;
        try {
            CustomSMSProvider.logger.log(Level.INFO, "SEND SMS HTTPResponse Code : " + httpResponseCode);
            CustomSMSProvider.logger.log(Level.INFO, "Custom SMS Provider SEND SMS API Response : " + responseMessage);
            responseMessage = responseMessage.toLowerCase();
            successResp = successResp.toLowerCase();
            if (successResp != null && responseMessage.indexOf(successResp) != -1) {
                CustomSMSProvider.logger.log(Level.INFO, "CustomSMSProvider SMS:SMS is send successfully.");
                status = "success";
            }
            else {
                status = "failure";
            }
        }
        catch (final Exception e) {
            CustomSMSProvider.logger.log(Level.WARNING, "Exception occured while finding the status of SMS delivery", e);
        }
        return status;
    }
    
    static {
        CustomSMSProvider.logger = Logger.getLogger("EMSAlertsLogger");
    }
}
