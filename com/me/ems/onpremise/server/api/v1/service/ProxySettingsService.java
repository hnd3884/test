package com.me.ems.onpremise.server.api.v1.service;

import java.util.Hashtable;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.onpremise.server.common.ProxyConfiguredHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.FrameworkStatusCodes;
import com.me.devicemanagement.onpremise.webclient.settings.SettingsUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.util.Encoder;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import javax.ws.rs.core.Response;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import com.me.ems.onpremise.server.core.ProxySettingsUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ProxySettingsService
{
    protected static Logger logger;
    private Logger actionLogger;
    
    public ProxySettingsService() {
        this.actionLogger = Logger.getLogger("PatchActionLogger");
    }
    
    public Map loadProxy() throws APIException {
        final Map proxyDetailsMap = new HashMap();
        try {
            ProxySettingsUtil.loadProxy(proxyDetailsMap);
        }
        catch (final Exception e) {
            ProxySettingsService.logger.log(Level.WARNING, "Exception in ConfigureProxyAction while executing loadProxy() :", e);
            throw new APIException("GENERIC0005", "dc.rest.api_internal_error", new String[0]);
        }
        return proxyDetailsMap;
    }
    
    public Response validateProxy(final Map proxyData) throws APIException {
        try {
            String errorStr = "";
            SyMUtil.deleteSyMParameter("proxy_failed");
            final String password = Encoder.convertFromBase((String)proxyData.get("password"));
            final JSONObject propJson = new JSONObject();
            propJson.put("proxyType", proxyData.get("proxyType"));
            propJson.put("userName", proxyData.get("userName"));
            propJson.put("password", (Object)password);
            propJson.put("proxyScript", proxyData.get("proxyScript"));
            propJson.put("proxyPort", proxyData.get("proxyPort"));
            propJson.put("proxyHost", proxyData.get("proxyHost"));
            final int resultCode = SettingsUtil.validateProxy(propJson);
            if (resultCode != 1504) {
                errorStr = I18N.getMsg(FrameworkStatusCodes.getErrorMessageByCode(Integer.valueOf(resultCode)), new Object[0]);
                throw new APIException(Response.Status.BAD_REQUEST, "SERVER301", errorStr);
            }
        }
        catch (final APIException e) {
            ProxySettingsService.logger.log(Level.WARNING, "Exception in validateProxy", (Throwable)e);
            throw e;
        }
        catch (final Exception ex) {
            ProxySettingsService.logger.log(Level.WARNING, "Exception in validateProxy", ex);
            throw new APIException("GENERIC0005", "dc.rest.api_internal_error", new String[0]);
        }
        return Response.status(Response.Status.CREATED).build();
    }
    
    public Response addProxyConfig(final Map proxyDetailsMap, final String owner) throws APIException {
        String message = null;
        try {
            ProxySettingsService.logger.log(Level.INFO, "Going to validate the proxy settings before saving ...");
            final String encodedPass = proxyDetailsMap.get("password");
            if (encodedPass != null) {
                final String plainPass = SyMUtil.decodeAsUTF16LE(encodedPass);
                proxyDetailsMap.put("password", Encoder.convertToNewBase(plainPass));
            }
            ProxySettingsUtil.setUsedDetailsIfNeeded(proxyDetailsMap);
            this.validateProxy(proxyDetailsMap);
        }
        catch (final APIException e) {
            throw e;
        }
        catch (final SyMException e2) {
            ProxySettingsService.logger.log(Level.WARNING, "Exception while setting the proxy user details ...", (Throwable)e2);
            throw new APIException("GENERIC0005", "dc.rest.api_internal_error", new String[0]);
        }
        try {
            ProxySettingsService.logger.log(Level.INFO, "Going to save proxy settings");
            this.actionLogger.log(Level.INFO, "user name :: " + owner + ", is trying to update proxy details");
            proxyDetailsMap.putIfAbsent("ftp_same_as_http", false);
            ProxySettingsUtil.saveProxyConfig(proxyDetailsMap);
            this.actionLogger.log(Level.INFO, "Proxy deails are updated");
            final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
            ProxyConfiguredHandler.getInstance().invokeProxyListeners(proxyDetails);
            ProxySettingsService.logger.log(Level.INFO, "proxy settings saved successfully");
            if (Boolean.parseBoolean(SyMUtil.getSyMParameter("proxy_defined"))) {
                MessageProvider.getInstance().hideMessage("NO_PROXY_ALERT");
            }
        }
        catch (final Exception e3) {
            ProxySettingsService.logger.log(Level.WARNING, "Exception in addProxyConfig of ConfigureProxyAction :", e3);
            try {
                message = I18N.getMsg("dc.admin.proxy.PROXY_CONFIG_FAIL", new Object[0]);
            }
            catch (final Exception ex) {
                ProxySettingsService.logger.log(Level.WARNING, "Exception in get I18N", e3);
            }
            throw new APIException("SERVER301", message, new String[0]);
        }
        return Response.status(Response.Status.CREATED).build();
    }
    
    public Map getDomainValidationStatus() throws APIException {
        final Map returnMap = new HashMap();
        try {
            String patchDomainValidation = "not_done";
            String invDomainValidation = "not_done";
            String mdmDomainValidation = "not_done";
            if (SyMUtil.getSyMParameter("patch_domain_validation") != null) {
                patchDomainValidation = SyMUtil.getSyMParameter("patch_domain_validation");
            }
            if (SyMUtil.getSyMParameter("inv_domain_validation") != null) {
                invDomainValidation = SyMUtil.getSyMParameter("inv_domain_validation");
            }
            if (SyMUtil.getSyMParameter("mdm_domain_validation") != null) {
                mdmDomainValidation = SyMUtil.getSyMParameter("mdm_domain_validation");
            }
            returnMap.put("patchDomainValidation", proxyStatusProvider(patchDomainValidation));
            returnMap.put("invDomainValidation", proxyStatusProvider(invDomainValidation));
            returnMap.put("mdmDomainValidation", proxyStatusProvider(mdmDomainValidation));
        }
        catch (final Exception ee) {
            ProxySettingsService.logger.log(Level.WARNING, "Exception in getDomainValidationStatus", ee);
            throw new APIException("GENERIC0005", "dc.rest.api_internal_error", new String[0]);
        }
        return returnMap;
    }
    
    public static Map proxyStatusProvider(final String status) {
        final Map statusMap = new HashMap();
        final Map returnMap = new HashMap();
        final String statusMsg = "statusMsg";
        final String statusCode = "StatusCode";
        if (statusMap.get(status) == null) {
            statusMap.put("not_done", "SERVER305");
            statusMap.put("failed", "SERVER301");
            statusMap.put("success", "SERVER304");
            statusMap.put("in_progress", "SERVER303");
            statusMap.put("started", "SERVER302");
        }
        returnMap.put("statusMsg", status);
        returnMap.put("StatusCode", statusMap.get(status));
        return returnMap;
    }
    
    public Map getProxyDefinedStatus() {
        final Map returnMap = new HashMap();
        final String proxyStatus = SyMUtil.getSyMParameter("proxy_defined");
        if (proxyStatus != null && !proxyStatus.isEmpty()) {
            returnMap.put("isProxyDefined", Boolean.parseBoolean(proxyStatus));
        }
        else {
            returnMap.put("isProxyDefined", proxyStatus);
        }
        return returnMap;
    }
    
    public Map validateDomains(final String urlType) {
        final String statusMsg = "statusMsg";
        final String statusCode = "statusCode";
        final Map returnMap = new HashMap();
        try {
            final String patchDomainValidation = SyMUtil.getSyMParameter("patch_domain_validation");
            final String invDomainValidation = SyMUtil.getSyMParameter("inv_domain_validation");
            final String mdmDomainValidation = SyMUtil.getSyMParameter("mdm_domain_validation");
            boolean startValidation = Boolean.TRUE;
            if (patchDomainValidation != null && patchDomainValidation.equals("in_progress")) {
                startValidation = Boolean.FALSE;
            }
            if (invDomainValidation != null && invDomainValidation.equals("in_progress")) {
                startValidation = Boolean.FALSE;
            }
            if (mdmDomainValidation != null && mdmDomainValidation.equals("in_progress")) {
                startValidation = Boolean.FALSE;
            }
            if (startValidation) {
                final HashMap taskInfoMap = new HashMap();
                taskInfoMap.put("taskName", "DomainValidationTask");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                final Properties userProps = new Properties();
                if (urlType != null) {
                    ((Hashtable<String, String>)userProps).put("urlType", urlType);
                }
                ((Hashtable<String, String>)userProps).put("DBUpdateValidation", "false");
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.devicemanagement.onpremise.server.util.DomainValidator", taskInfoMap, userProps);
                returnMap.put("statusMsg", "started");
                returnMap.put("statusCode", "SERVER302");
            }
            else {
                returnMap.put("statusMsg", "in_progress");
                returnMap.put("statusCode", "SERVER303");
            }
        }
        catch (final Exception ee) {
            ProxySettingsService.logger.log(Level.WARNING, "Exception in validateDomains", ee);
            returnMap.put("statusMsg", "failed");
            returnMap.put("statusCode", "SERVER301");
        }
        return returnMap;
    }
    
    static {
        ProxySettingsService.logger = Logger.getLogger(ProxySettingsService.class.getName());
    }
}
