package com.me.ems.framework.securitysettings.api.v1.service;

import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.ems.framework.securitysettings.api.v1.model.SecuritySettingsModel;
import java.util.logging.Level;
import java.util.Map;
import com.me.ems.framework.securitysettings.api.util.SecuritySettingsUtil;
import java.util.logging.Logger;
import com.me.ems.framework.securitysettings.api.core.SecuritySettingsService;

public class SecuritySettingsServiceImpl implements SecuritySettingsService
{
    protected static Logger logger;
    SecuritySettingsUtil securitySettingsUtil;
    
    public SecuritySettingsServiceImpl() {
        this.securitySettingsUtil = SecuritySettingsUtil.getInstance();
    }
    
    @Override
    public Map getSecuritySettingsDetails(final Long customerId) throws APIException {
        SecuritySettingsServiceImpl.logger.log(Level.INFO, "Entered getSecuritySettingsDetails() with customer id:{0} ", new Object[] { customerId });
        try {
            final SecuritySettingsModel securitySettingsModel = new SecuritySettingsModel();
            securitySettingsModel.setCustomerId(customerId);
            ApiFactoryProvider.getFwSecuritySettingsApi().getSecuritySettings(securitySettingsModel);
            ApiFactoryProvider.getProductSecuritySettingsApi().getSecuritySettings(securitySettingsModel);
            SecuritySettingsServiceImpl.logger.log(Level.INFO, "getSecuritySettingsDetails():- data is :{0} ", new Object[] { securitySettingsModel.toString() });
            return securitySettingsModel.getSettings();
        }
        catch (final Exception ex) {
            SecuritySettingsServiceImpl.logger.log(Level.SEVERE, "getSecuritySettingsDetails():- Exception occurred while retrieving security settings ", ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Long validateCustomer(final String customerIdStr) throws APIException {
        SecuritySettingsServiceImpl.logger.log(Level.INFO, "Entered validateCustomer() with customer id string :{0} ", new Object[] { customerIdStr });
        Long customerID = null;
        if (!CustomerInfoUtil.getInstance().isMSP()) {
            customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            return customerID;
        }
        if (customerIdStr != null && !customerIdStr.isEmpty()) {
            if (customerIdStr.equalsIgnoreCase("all")) {
                try {
                    customerID = CustomerInfoUtil.getInstance().getCustomerIDForLoginUser();
                }
                catch (final Exception ex) {
                    if (!ex.getMessage().equalsIgnoreCase("Customer ID Not Available.")) {
                        throw new APIException("dc.rest.api_internal_error");
                    }
                }
            }
            else {
                customerID = Long.parseLong(customerIdStr);
            }
            return customerID;
        }
        throw new APIException("GENERIC0004");
    }
    
    @Override
    public Map saveSecuritySettings(final Map securitySettingsDetails, final User user, final Long customerId, final HttpServletRequest httpServletRequest) throws APIException {
        SecuritySettingsServiceImpl.logger.log(Level.INFO, "Entered saveSecuritySettings() with security setting details :{0} & loginId:{1} & customerId:{2}", new Object[] { securitySettingsDetails, user.getLoginID(), customerId });
        try {
            if (securitySettingsDetails == null || securitySettingsDetails.isEmpty()) {
                SecuritySettingsServiceImpl.logger.log(Level.SEVERE, "No parameters found while saving security settings");
                throw new APIException(Response.Status.PRECONDITION_FAILED, "GENERIC0003", "ems.rest.api.param.missing");
            }
            final SecuritySettingsModel securitySettingsModel = new SecuritySettingsModel();
            securitySettingsDetails.put("showRestartMessage", false);
            securitySettingsDetails.put("statusCode", 0);
            ApiFactoryProvider.getFwSecuritySettingsApi().saveSecuritySettings(securitySettingsDetails, user.getName(), customerId);
            ApiFactoryProvider.getProductSecuritySettingsApi().saveSecuritySettings(securitySettingsDetails, user.getName(), customerId);
            final JSONObject oneLineData = new JSONObject(securitySettingsDetails);
            oneLineData.put("userName", (Object)user.getName());
            oneLineData.put("customerId", (Object)customerId);
            SecurityOneLineLogger.log("SecuritySettings", "Modify", oneLineData.toString(), Level.INFO);
            this.securitySettingsUtil.addOrDeleteBasicSettingsConfiguredDate(securitySettingsModel.isAllBasicSettingsConfigured());
            final Map secureSettings = this.getSecuritySettingsDetails(customerId);
            securitySettingsDetails.put("securePercentage", secureSettings.get("securePercentage"));
        }
        catch (final APIException apiException) {
            throw apiException;
        }
        catch (final Exception exception) {
            SecuritySettingsServiceImpl.logger.log(Level.SEVERE, "saveSecuritySettings():- Exception occurred saving security setting", exception);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
        return securitySettingsDetails;
    }
    
    @Override
    public Map getSecuritySettingAlertDetails(final User user, final Long customerId) throws APIException {
        SecuritySettingsServiceImpl.logger.log(Level.INFO, "Entered getSecuritySettingAlertDetails() with LoginId: {0} & customerId", new Object[] { user.getLoginID(), customerId });
        final Map securityActions = new HashMap();
        try {
            final SecuritySettingsModel securitySettingsModel = new SecuritySettingsModel();
            securitySettingsModel.setCustomerId(customerId);
            securitySettingsModel.setUser(user);
            ApiFactoryProvider.getFwSecuritySettingsApi().getSecuritySettings(securitySettingsModel);
            ApiFactoryProvider.getProductSecuritySettingsApi().getSecuritySettings(securitySettingsModel);
            securityActions.put("securePercentage", securitySettingsModel.getSecurePercentage());
            this.securitySettingsUtil.addOrDeleteBasicSettingsConfiguredDate(securitySettingsModel.isAllBasicSettingsConfigured());
            this.securitySettingsUtil.getSecuritySettingAlertDetails(securitySettingsModel);
            securityActions.put("isRedirectionNeeded", securitySettingsModel.isRedirectionNeed() && !ApiFactoryProvider.getDemoUtilAPI().isDemoMode());
            securityActions.put("isSecurityIconNeeded", securitySettingsModel.isSecurityIconNeed());
            securityActions.put("isSecurityMsgNeeded", securitySettingsModel.isSecurityMsgNeed());
            securityActions.put("securityAdvice", securitySettingsModel.getSecurityAdvice());
            SecuritySettingsServiceImpl.logger.log(Level.INFO, "getSecuritySettingsDetails():- alert details are :{0} ", new Object[] { securitySettingsModel.toString() });
        }
        catch (final Exception ex) {
            SecuritySettingsServiceImpl.logger.log(Level.SEVERE, "getSecurityConcern():- Exception occurred while getting security actions for Home page API :- ", ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
        return securityActions;
    }
    
    @Override
    public void updateSecurityRedirectionTime(final Boolean isRedirected) {
        SecuritySettingsServiceImpl.logger.log(Level.INFO, "Entered updateSecurityRedirectionTime() with isRedirected :{0}", new Object[] { isRedirected });
        if (isRedirected) {
            SecurityUtil.updateSecurityParameter("lastSecurityRedirection", SyMUtil.getCurrentTimeWithDate());
        }
    }
    
    @Override
    public Map getSecurityEnforceDetails() throws APIException {
        final SecuritySettingsModel securitySettingsModel = new SecuritySettingsModel();
        try {
            return ApiFactoryProvider.getProductSecuritySettingsApi().getSecurityEnforceDetails(securitySettingsModel);
        }
        catch (final Exception e) {
            SecuritySettingsServiceImpl.logger.log(Level.SEVERE, "getSecurityEnforceDetails():- Exception occurred while getting security enforce details :- ", e);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    static {
        SecuritySettingsServiceImpl.logger = Logger.getLogger("SecurityLogger");
    }
}
