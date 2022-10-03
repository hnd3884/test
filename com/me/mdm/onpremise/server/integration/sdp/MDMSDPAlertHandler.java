package com.me.mdm.onpremise.server.integration.sdp;

import java.util.Hashtable;
import com.me.mdm.server.alerts.MDMAlertConstants;
import com.me.mdm.onpremise.server.integration.MDMIntegrationUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Iterator;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.JSONObject;
import com.me.devicemanagement.onpremise.server.mesolutions.notification.SDPNotificationUtil;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import java.util.logging.Level;
import java.net.URLDecoder;
import com.me.devicemanagement.framework.server.alerts.AlertsUtil;
import com.me.devicemanagement.framework.server.alerts.AlertMailGeneratorUtil;
import java.util.Properties;
import java.util.logging.Logger;

public class MDMSDPAlertHandler
{
    private static final Logger SDPINTEGLOGGER;
    private static MDMSDPAlertHandler integHandler;
    private static final String ADD_REQ_SDP_URL = "/sdpapi/request?";
    private static final String ADD_REQ_SDP_V3_URL = "/api/v3/requests";
    
    public static MDMSDPAlertHandler getInstance() {
        if (MDMSDPAlertHandler.integHandler == null) {
            MDMSDPAlertHandler.integHandler = new MDMSDPAlertHandler();
        }
        return MDMSDPAlertHandler.integHandler;
    }
    
    private void postSDPAlert(final Long customerId, final Properties prop, final Long alertConst) {
        try {
            Properties subDescProp = null;
            final AlertMailGeneratorUtil alertMailUtil = new AlertMailGeneratorUtil();
            subDescProp = AlertsUtil.getInstance().getCustomerKeyDescription((long)customerId, alertConst);
            String subject = subDescProp.getProperty("subject");
            subject = URLDecoder.decode(subject, "UTF-8");
            String description = subDescProp.getProperty("description");
            description = URLDecoder.decode(description, "UTF-8");
            subject = alertMailUtil.expandedSubDescription(prop, subject);
            description = alertMailUtil.expandedSubDescription(prop, description);
            ((Hashtable<String, String>)prop).put("subject", subject);
            ((Hashtable<String, String>)prop).put("DESCRIPTION", description);
            this.addRequest(prop, "ADD_ALERT", true, false);
        }
        catch (final Exception ex) {
            MDMSDPAlertHandler.SDPINTEGLOGGER.log(Level.WARNING, "Exception while generating SDP ticket content", ex);
        }
    }
    
    private String addRequest(final Properties propRequest, final String typeOfRequest, final boolean isNotTrial, final boolean isWorkLogEnabled) {
        final String sdpStatus = "";
        String responsefromSdp = "";
        final boolean isSDPIntegrationEnabled = MDMSDPIntegrationUtil.getInstance().isSDPIntegrationEnabled();
        if (isSDPIntegrationEnabled) {
            try {
                final String authenticationKey = SolutionUtil.getInstance().getServerSettings("HelpDesk").getProperty("AUTHENDICATION_KEY");
                SDPNotificationUtil.getInstance();
                String apiurl = SDPNotificationUtil.getServiceDeskBaseURL();
                final String sdpBuildNumber = MDMSDPIntegrationUtil.getInstance().getSDPBuildNumber();
                final int sdpBN = Integer.parseInt(sdpBuildNumber);
                if (sdpBN < 11112) {
                    apiurl = apiurl.concat("/sdpapi/request?");
                    MDMSDPAlertHandler.SDPINTEGLOGGER.log(Level.INFO, "---URL to post data-addRequest---{0}", apiurl);
                    final URLConnection conn = MDMSDPIntegrationUtil.getInstance().createSDPURLConnection(apiurl, "NO_CONTENT_TYPE", true, true, true);
                    final Iterator itr = propRequest.entrySet().iterator();
                    JSONObject addRequestJSON = new JSONObject();
                    addRequestJSON = MDMSDPIntegrationUtil.getInstance().appendJSONElement(itr, "ADD_REQUEST");
                    final Properties addRequestProp = new Properties();
                    addRequestProp.setProperty("AUTHENTICATION_KEY", authenticationKey);
                    addRequestProp.setProperty("OPERATION_NAME", "ADD_REQUEST");
                    addRequestProp.setProperty("TYPE_OF_OPERATION", typeOfRequest);
                    addRequestProp.setProperty("IS_NOT_TRAIL", String.valueOf(isNotTrial));
                    addRequestProp.setProperty("IS_WORKLOG_ENABLED", String.valueOf(isWorkLogEnabled));
                    responsefromSdp = MDMSDPIntegrationUtil.getInstance().requestSDP(conn, addRequestJSON, addRequestProp);
                }
                else {
                    apiurl = apiurl.concat("/api/v3/requests");
                    final JSONObject dataJson = this.getJsonFormatValueForv3(propRequest);
                    final String str = URLEncoder.encode(dataJson.toString(), "UTF-8");
                    apiurl = apiurl.concat("?input_data=").concat(str);
                    MDMSDPAlertHandler.SDPINTEGLOGGER.log(Level.INFO, "URL to post data-addRequest - v3 - {0}", apiurl);
                    final String responsefromSDP = MDMSDPIntegrationUtil.getInstance().requestSDP(apiurl, "POST");
                    MDMSDPAlertHandler.SDPINTEGLOGGER.log(Level.INFO, "Response from SDP v3{0}", responsefromSDP);
                }
                MDMSDPAlertHandler.SDPINTEGLOGGER.log(Level.INFO, "Response from SDP-addRequest - {0}", responsefromSdp);
            }
            catch (final Exception e) {
                MDMSDPAlertHandler.SDPINTEGLOGGER.log(Level.WARNING, "addRequest Exception", e);
            }
            propRequest.setProperty("feature", "ADD_REQUEST");
        }
        return responsefromSdp;
    }
    
    private JSONObject getJsonFormatValueForv3(final Properties propRequest) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final JSONObject dataJson = new JSONObject();
            dataJson.put("subject", (Object)propRequest.getProperty("subject"));
            if (propRequest.containsKey("DESCRIPTION")) {
                String desc = propRequest.getProperty("DESCRIPTION");
                desc = desc.replaceAll("\\n", "<br>");
                desc = desc.replaceAll("\\r", " ");
                dataJson.put("description", (Object)desc);
            }
            final JSONObject requesterJSON = new JSONObject();
            String requester = propRequest.getProperty("requester");
            if (requester == null || requester.isEmpty()) {
                requester = SolutionUtil.getInstance().getIntegrationParamsValue("INTEGRATED_SDPUSER");
                if (requester == null || requester.isEmpty()) {
                    requester = this.getTechnicianNamefromSDP();
                }
            }
            if (requester != null && !requester.isEmpty()) {
                requesterJSON.put("name", (Object)requester);
            }
            requesterJSON.put("email_id", (Object)propRequest.getProperty("requesterEmail"));
            if (propRequest.containsKey("reqTemplate") && propRequest.getProperty("reqTemplate") != null && propRequest.getProperty("reqTemplate") != "" && !propRequest.getProperty("reqTemplate").isEmpty()) {
                final JSONObject templateJson = new JSONObject();
                templateJson.put("name", (Object)propRequest.getProperty("reqTemplate"));
                dataJson.put("template", (Object)templateJson);
            }
            final Boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            if (isMSP) {
                final String accountName = propRequest.getProperty("account");
                final JSONObject accountJSON = new JSONObject();
                accountJSON.put("name", (Object)accountName);
                dataJson.put("account", (Object)accountJSON);
                final String siteName = propRequest.getProperty("site");
                final JSONObject siteJSON = new JSONObject();
                siteJSON.put("name", (Object)siteName);
                dataJson.put("site", (Object)siteJSON);
            }
            dataJson.put("requester", (Object)requesterJSON);
            jsonObject.put("request", (Object)dataJson);
        }
        catch (final Exception e) {
            MDMSDPAlertHandler.SDPINTEGLOGGER.log(Level.WARNING, "addRequest Exception", e);
        }
        return jsonObject;
    }
    
    private String getTechnicianNamefromSDP() {
        String requester = "";
        try {
            final String sdpBuildNumber = MDMSDPIntegrationUtil.getInstance().getSDPBuildNumber();
            final int sdpBN = Integer.parseInt(sdpBuildNumber);
            if (sdpBN > 11112) {
                final StringBuilder sb = new StringBuilder();
                SDPNotificationUtil.getInstance();
                String apiurl = sb.append(SDPNotificationUtil.getServiceDeskBaseURL()).append("/api/v3/technicians/loggedin_user_details?").toString();
                MDMSDPAlertHandler.SDPINTEGLOGGER.log(Level.INFO, "URL to post get the Techinician details v3{0}", apiurl);
                apiurl += "&PORTALID=1";
                final String responseFromSDP = MDMSDPIntegrationUtil.getInstance().requestSDP(apiurl, "GET");
                MDMSDPAlertHandler.SDPINTEGLOGGER.log(Level.INFO, "Response from SDP v3{0}", responseFromSDP);
                if (responseFromSDP != null && responseFromSDP.contains("response_status")) {
                    final JSONObject responseJSON = new JSONObject(responseFromSDP);
                    final JSONObject statusJSON = responseJSON.getJSONObject("response_status");
                    final String sdpStatus = statusJSON.getString("status");
                    if (sdpStatus != null && sdpStatus.equalsIgnoreCase("success")) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject = responseJSON.getJSONObject("technician");
                        requester = jsonObject.getString("name");
                        SolutionUtil.getInstance().updateIntegrationParameter("INTEGRATED_SDP_USER", requester);
                    }
                    MDMSDPAlertHandler.SDPINTEGLOGGER.log(Level.INFO, " Response status from sdp : {0}", sdpStatus);
                }
            }
        }
        catch (final Exception e) {
            MDMSDPAlertHandler.SDPINTEGLOGGER.log(Level.WARNING, "getTechnicianNamefromSDP Exception", e);
        }
        return requester;
    }
    
    public void handleSDPNewAppAlert(final Properties prop) {
        final Long customerId = ((Hashtable<K, Long>)prop).get("CUSTOMER_ID");
        final String isSDPAlertEnabledStr = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_HELPDESK_ALERT");
        final String isSDPNewAppAlertEnabledStr = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_HELPDESK_NEW_APP_ALERT");
        if (isSDPAlertEnabledStr != null && !isSDPAlertEnabledStr.equalsIgnoreCase("false") && isSDPNewAppAlertEnabledStr != null && !isSDPNewAppAlertEnabledStr.equalsIgnoreCase("false")) {
            this.postSDPAlert(customerId, prop, MDMAlertConstants.SDP_NEW_APP_DISCOVERED_ALERT);
        }
    }
    
    public void handleSDPBlackListAppAlert(final Properties prop) {
        final Long customerId = ((Hashtable<K, Long>)prop).get("CUSTOMER_ID");
        final String isSDPAlertEnabledStr = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_HELPDESK_ALERT");
        final String isSDPBlackListAppEnabledStr = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_HELPDESK_BLACKLIST_APP_ALERT");
        if (isSDPAlertEnabledStr != null && !isSDPAlertEnabledStr.equalsIgnoreCase("false") && isSDPBlackListAppEnabledStr != null && !isSDPBlackListAppEnabledStr.equalsIgnoreCase("false")) {
            this.postSDPAlert(customerId, prop, MDMAlertConstants.SDP_ADMIN_BLACK_LIST_ALERT);
        }
    }
    
    public void handleSDPCertificateAlert(final Properties prop) {
        final Long customerId = ((Hashtable<K, Long>)prop).get("CUSTOMER_ID");
        final String isSDPCertificateAlertEnabledStr = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_HELPDESK_UNMANAGED_CERTIFICATE_ALERT");
        if (isSDPCertificateAlertEnabledStr != null && !isSDPCertificateAlertEnabledStr.equalsIgnoreCase("false")) {
            this.postSDPAlert(customerId, prop, MDMAlertConstants.SDP_UNMANAGED_CERTIFICATE_ALERT);
        }
    }
    
    static {
        SDPINTEGLOGGER = Logger.getLogger("MDMSDPIntegrationLog");
        MDMSDPAlertHandler.integHandler = null;
    }
}
