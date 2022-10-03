package com.me.emsalerts.sms.api.v1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.net.SocketException;
import java.io.IOException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.emsalerts.common.tracking.AlertsTrackingParamUtil;
import org.json.JSONObject;
import com.me.emsalerts.sms.core.CustomSMSProvider;
import com.me.emsalerts.sms.core.SMSUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import javax.ws.rs.WebApplicationException;
import com.me.ems.framework.common.api.response.APIResponse;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.LinkedHashMap;
import java.util.Hashtable;
import java.util.HashMap;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;
import com.me.emsalerts.sms.factory.SMSService;

public class SMSServiceImpl implements SMSService
{
    protected static Logger logger;
    
    @Override
    public Response updateSMSSettings(final User user, final Map smsConfigProperties, final HttpServletRequest httpServletRequest) {
        HashMap response = new HashMap();
        try {
            final Hashtable smsConfigHashTable = new Hashtable();
            smsConfigHashTable.putAll(smsConfigProperties);
            boolean testSMSStatus = false;
            final LinkedHashMap requestParams = smsConfigHashTable.get("requestParams");
            final String url = requestParams.get("url");
            final Boolean isMandateHttpsConnection = Boolean.parseBoolean(SyMUtil.getSyMParameter("mandateHttpsConnection"));
            if (isMandateHttpsConnection && !url.startsWith("https")) {
                throw new WebApplicationException(APIResponse.errorResponse(new APIException(Response.Status.EXPECTATION_FAILED, "10003", "dc.common.sms.invalidurl_https_error")));
            }
            if (!isMandateHttpsConnection && !url.startsWith("http") && !url.startsWith("https")) {
                throw new WebApplicationException(APIResponse.errorResponse(new APIException(Response.Status.EXPECTATION_FAILED, "10003", "dc.common.sms.invalidurl_http_error")));
            }
            if (smsConfigHashTable.containsKey("testNumber") && smsConfigHashTable.get("testNumber") instanceof String) {
                final String productName = ProductUrlLoader.getInstance().getValue("displayname");
                String testMessage = I18N.getMsg("emsalerts.sms.test_message", new Object[] { productName });
                final String testPhoneNumber = smsConfigHashTable.get("testNumber");
                if (smsConfigHashTable.containsKey("unicode") && smsConfigHashTable.get("unicode") instanceof LinkedHashMap) {
                    final LinkedHashMap unicode = smsConfigHashTable.get("unicode");
                    final boolean unicodeEnabled = unicode.get("isConfigured") != null && unicode.get("isConfigured");
                    if (unicodeEnabled) {
                        testMessage = testMessage.replaceAll("\\\\t", " ");
                        testMessage = testMessage.replaceAll("\\\\n", "\n");
                        testMessage = SMSUtil.convertToUnicode(testMessage);
                    }
                }
                final JSONObject sendSMSResponse = new CustomSMSProvider().sendMessage(smsConfigHashTable, testPhoneNumber, testMessage);
                if (!sendSMSResponse.get("smsStatus").toString().equalsIgnoreCase("success")) {
                    throw new WebApplicationException(APIResponse.errorResponse(new APIException(Response.Status.EXPECTATION_FAILED, "10003", "emsalerts.sms.unable_to_connect")));
                }
                testSMSStatus = true;
            }
            else {
                testSMSStatus = true;
            }
            if (testSMSStatus) {
                final JSONObject smsConfigurations = new JSONObject(smsConfigProperties);
                final CustomSMSProvider customSMSProvider = new CustomSMSProvider();
                response = customSMSProvider.setSMSConfigurationSettings(smsConfigurations);
                AlertsTrackingParamUtil.incrementTrackingParam("SMS_SUCCESS_COUNT");
                if (AlertsTrackingParamUtil.getAlertsTrackingParam("SMS_CONFIGURED_TIME").isEmpty()) {
                    AlertsTrackingParamUtil.addOrUpdateTrackingParam("SMS_CONFIGURED_TIME", String.valueOf(System.currentTimeMillis()));
                }
                DCEventLogUtil.getInstance().addEvent(100000, user.getName(), (HashMap)null, "emsalerts.sms.configured_success", (Object)null, false);
            }
            else {
                AlertsTrackingParamUtil.incrementTrackingParam("SMS_FAILURE_COUNT");
            }
        }
        catch (final Exception e) {
            AlertsTrackingParamUtil.incrementTrackingParam("SMS_FAILURE_COUNT");
            SMSServiceImpl.logger.log(Level.WARNING, "Exception occured while saving SMS details ", e);
            if (e instanceof WebApplicationException) {
                throw (WebApplicationException)e;
            }
            if (e instanceof IOException || e instanceof SocketException) {
                throw new WebApplicationException(APIResponse.errorResponse(new APIException(Response.Status.EXPECTATION_FAILED, "10003", "emsalerts.sms.unable_to_connect")));
            }
            throw new WebApplicationException(APIResponse.errorResponse(new APIException(Response.Status.EXPECTATION_FAILED, "10003", "emsalerts.sms.unable_to_connect")));
        }
        if (response.containsKey("status") & response.get("status").equals("success")) {
            return Response.ok().build();
        }
        return Response.notModified().build();
    }
    
    @Override
    public Hashtable getSMSConfigurationSettings() {
        final HashMap smssettings = new HashMap();
        Hashtable smsDetailsTable = new Hashtable();
        try {
            final CustomSMSProvider customSMSProvider = new CustomSMSProvider();
            if (customSMSProvider.isSMSSettingsConfigured()) {
                final Properties smsProps = customSMSProvider.getSMSConfigurationSettings();
                if (smssettings != null & smssettings.containsKey("status")) {
                    throw new WebApplicationException(APIResponse.errorResponse(new APIException(Response.Status.INTERNAL_SERVER_ERROR, "10001", (String)smssettings.get("message"))));
                }
                final String serviceName = smsProps.getProperty("serviceName");
                if (smsProps.size() != 0 && smsProps.containsKey("actions")) {
                    smsDetailsTable = ((Hashtable<K, Hashtable>)smsProps).get("actions");
                }
                smsDetailsTable.put("isSMSSettingsConfigured", true);
                smsDetailsTable.put("isSMSSettingsEnabled", SMSUtil.getInstance().isSMSSettingsEnabled(serviceName));
                smsDetailsTable.put("defaultCountryCode", "");
                final Boolean isMandateHttpsConnection = Boolean.parseBoolean(SyMUtil.getSyMParameter("mandateHttpsConnection"));
                final LinkedHashMap requestParams = smsDetailsTable.get("requestParams");
                final String url = requestParams.get("url");
                if (!isMandateHttpsConnection || !url.startsWith("https")) {
                    smsDetailsTable.put("mandateHttpsConnection", false);
                }
                else {
                    smsDetailsTable.put("mandateHttpsConnection", true);
                }
                return smsDetailsTable;
            }
            else {
                final Boolean isMandateHttpsConnection2 = Boolean.parseBoolean(SyMUtil.getSyMParameter("mandateHttpsConnection"));
                smsDetailsTable.put("mandateHttpsConnection", isMandateHttpsConnection2);
                smsDetailsTable.put("isSMSSettingsConfigured", false);
                if (!CustomerInfoUtil.isSAS()) {
                    smsDetailsTable.put("defaultCountryCode", "");
                }
                SMSServiceImpl.logger.log(Level.SEVERE, "SMS Gateway settings are not configured.Can't fetch the SMS settings from DB");
            }
        }
        catch (final Exception e) {
            SMSServiceImpl.logger.log(Level.WARNING, "Exception occured while getting the SMS Configuration ", e);
            throw new WebApplicationException(APIResponse.errorResponse(new APIException(Response.Status.INTERNAL_SERVER_ERROR, "10001", "Internal Server Error")));
        }
        return smsDetailsTable;
    }
    
    @Override
    public List getDialingCodes() {
        final List dialingCodeList = new ArrayList();
        return dialingCodeList;
    }
    
    @Override
    public void enableSMSSettings(final User user) {
        try {
            final String serviceName = "custom";
            SMSUtil.getInstance().updateSMSStatus(serviceName, 1);
            DCEventLogUtil.getInstance().addEvent(100001, user.getName(), (HashMap)null, "emsalerts.sms.enabled_success", (Object)null, false);
        }
        catch (final Exception e) {
            SMSServiceImpl.logger.log(Level.WARNING, "Exception occured while enabling the SMS Settings ", e);
            throw new WebApplicationException(APIResponse.errorResponse(new APIException(Response.Status.INTERNAL_SERVER_ERROR, "10001", "Internal Server Error")));
        }
    }
    
    @Override
    public void disableSMSSettings(final User user) {
        try {
            final String serviceName = "custom";
            SMSUtil.getInstance().updateSMSStatus(serviceName, 0);
            DCEventLogUtil.getInstance().addEvent(100002, user.getName(), (HashMap)null, "emsalerts.sms.disabled_success", (Object)null, false);
        }
        catch (final Exception e) {
            SMSServiceImpl.logger.log(Level.WARNING, "Exception occured while disabling the SMS Settings ", e);
            throw new WebApplicationException(APIResponse.errorResponse(new APIException(Response.Status.INTERNAL_SERVER_ERROR, "10001", "Internal Server Error")));
        }
    }
    
    @Override
    public boolean isSMSConfigured() {
        final CustomSMSProvider customSMSProvider = new CustomSMSProvider();
        return customSMSProvider.isSMSSettingsConfigured();
    }
    
    static {
        SMSServiceImpl.logger = Logger.getLogger("EMSAlertsLogger");
    }
}
