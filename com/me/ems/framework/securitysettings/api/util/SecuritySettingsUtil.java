package com.me.ems.framework.securitysettings.api.util;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.logging.Level;
import com.me.ems.framework.securitysettings.api.v1.model.SecuritySettingsModel;
import java.util.logging.Logger;

public class SecuritySettingsUtil
{
    private static Logger logger;
    private static SecuritySettingsUtil securitySettingsUtil;
    
    private SecuritySettingsUtil() {
    }
    
    public static SecuritySettingsUtil getInstance() {
        return (SecuritySettingsUtil.securitySettingsUtil == null) ? new SecuritySettingsUtil() : SecuritySettingsUtil.securitySettingsUtil;
    }
    
    public void getSecuritySettingAlertDetails(final SecuritySettingsModel securitySettingsModel) throws Exception {
        SecuritySettingsUtil.logger.log(Level.INFO, "Entered getSecuritySettingAlertDetails() with data :{0}", new Object[] { securitySettingsModel.toString() });
        try {
            if (securitySettingsModel.getSecurePercentage() == 100L) {
                return;
            }
            if (!securitySettingsModel.getSecurityAdvice().isEmpty()) {
                return;
            }
            if (DMUserHandler.isUserInRole(securitySettingsModel.getUser().getLoginID(), "All_Managed_Computer")) {
                final boolean isTrialUser = LicenseProvider.getInstance().getLicenseType().equals("T");
                final long noOfWeeksFromInstalledDate = this.getNoOfWeeksFromInstalledDate();
                final long noOfWeeksBeforeSecurityAction = this.getNoOfWeeksBeforeSecurityAction();
                SecuritySettingsUtil.logger.log(Level.INFO, "getSecuritySettingAlertDetails():- isTrailUer :{0} & noOfWeeksFromInstalledDate :{1} & noOfWeeksFromInstalledDate :{2} ", new Object[] { isTrialUser, noOfWeeksFromInstalledDate, noOfWeeksFromInstalledDate });
                if (!isTrialUser) {
                    securitySettingsModel.setSecurityIconNeed(true);
                    securitySettingsModel.setSecurityAdvice("adviceTitle", I18N.getMsg("ems.security.settings.advice_title", new Object[0]));
                    securitySettingsModel.setSecurityAdvice("adviceMsg", securitySettingsModel.getBasicSecurityAdvice());
                    SecurityUtil.addSecurityParamIfDoesNotExist("securityIconAddedDate", SyMUtil.getCurrentTimeWithDate());
                    if (!securitySettingsModel.isTfaBannerNeed()) {
                        securitySettingsModel.setSecurityMsgNeed(!securitySettingsModel.isAllBasicSettingsConfigured());
                        if (!securitySettingsModel.isAllBasicSettingsConfigured() && (noOfWeeksBeforeSecurityAction == -1L || noOfWeeksBeforeSecurityAction >= 2L)) {
                            securitySettingsModel.setRedirectionNeed(true);
                        }
                    }
                }
                else if (isTrialUser && noOfWeeksFromInstalledDate >= 3L) {
                    securitySettingsModel.setSecurityIconNeed(true);
                    securitySettingsModel.setSecurityAdvice("adviceTitle", I18N.getMsg("ems.security.settings.advice_title", new Object[0]));
                    securitySettingsModel.setSecurityAdvice("adviceMsg", securitySettingsModel.getBasicSecurityAdvice());
                    SecurityUtil.addSecurityParamIfDoesNotExist("securityIconAddedDate", SyMUtil.getCurrentTimeWithDate());
                    if (!securitySettingsModel.isTfaBannerNeed()) {
                        securitySettingsModel.setSecurityMsgNeed(!securitySettingsModel.isAllBasicSettingsConfigured());
                        if (!securitySettingsModel.isAllBasicSettingsConfigured() && noOfWeeksBeforeSecurityAction == -1L) {
                            ApiFactoryProvider.getFwSecuritySettingsApi().getSecuritySettingsAlertDetails(securitySettingsModel);
                            if (!securitySettingsModel.isRedirectionNeed()) {
                                ApiFactoryProvider.getProductSecuritySettingsApi().getSecuritySettingsAlertDetails(securitySettingsModel);
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception exception) {
            SecuritySettingsUtil.logger.log(Level.SEVERE, "getSecuritySettingAlertDetails():- Exception is" + exception);
            throw exception;
        }
    }
    
    private long getNoOfWeeksBeforeSecurityAction() throws Exception {
        long noOfWeeksBeforeSecurityAction = 0L;
        try {
            final String lastSecurityAction = SecurityUtil.getSecurityParameter("lastSecurityRedirection");
            if (lastSecurityAction == null) {
                noOfWeeksBeforeSecurityAction = -1L;
                return noOfWeeksBeforeSecurityAction;
            }
            final SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy hh:mm a");
            final Date date1 = formatter.parse(lastSecurityAction);
            final Date date2 = Calendar.getInstance().getTime();
            noOfWeeksBeforeSecurityAction = TimeUnit.DAYS.convert(Math.abs(date2.getTime() - date1.getTime()), TimeUnit.MILLISECONDS) / 7L;
            return noOfWeeksBeforeSecurityAction;
        }
        catch (final Exception exception) {
            SecuritySettingsUtil.logger.log(Level.SEVERE, "getNoOfWeeksBeforeSecurityAction():- Exception is" + exception);
            throw exception;
        }
    }
    
    private long getNoOfWeeksFromInstalledDate() {
        try {
            final String it = SyMUtil.getInstallationProperty("it");
            if (it != null && !it.isEmpty()) {
                return TimeUnit.DAYS.convert(Math.abs(SyMUtil.getCurrentTimeInMillis() - Long.parseLong(it)), TimeUnit.MILLISECONDS) / 7L;
            }
        }
        catch (final Exception ex) {
            SecuritySettingsUtil.logger.log(Level.SEVERE, "Exception while converting Installation time for alerts API ", ex);
        }
        SecuritySettingsUtil.logger.log(Level.WARNING, "Installation Time from install conf returned null or zero, hence proceeding with default value in security/alerts ");
        return 0L;
    }
    
    public void addOrDeleteBasicSettingsConfiguredDate(final boolean isBasicSettingsConfigured) {
        if (isBasicSettingsConfigured) {
            SecurityUtil.addSecurityParamIfDoesNotExist("basicSettingsConfiguredDate", SyMUtil.getCurrentTimeWithDate());
        }
        else {
            SecurityUtil.deleteSecurityParameter("basicSettingsConfiguredDate");
        }
    }
    
    public Map getSecuritySettingsMeTrackerDetails() {
        final Map securitySettingsDetail = new HashMap();
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            final SecuritySettingsModel securitySettingsModel = new SecuritySettingsModel();
            securitySettingsModel.setCustomerId(customerId);
            ApiFactoryProvider.getFwSecuritySettingsApi().getSecuritySettings(securitySettingsModel);
            ApiFactoryProvider.getProductSecuritySettingsApi().getSecuritySettings(securitySettingsModel);
            securitySettingsDetail.putAll(securitySettingsModel.getBasicSettings());
            securitySettingsDetail.putAll(securitySettingsModel.getAdvancedSettings());
            final boolean isBasicConfigured = securitySettingsModel.isAllBasicSettingsConfigured();
            securitySettingsDetail.put("isBasicSettingsConfigured", isBasicConfigured);
            long noOfWeeksBeforeSecurityAction = -1L;
            if (isBasicConfigured) {
                final Map securityParams = this.getSecuritySettingMETrackerValues(new String[] { "securityIconAddedDate", "basicSettingsConfiguredDate" });
                final SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy hh:mm a");
                try {
                    final Date iconAddedDate = formatter.parse(securityParams.get("securityIconAddedDate"));
                    final Date basicSettingConfDate = formatter.parse(securityParams.get("basicSettingsConfiguredDate"));
                    noOfWeeksBeforeSecurityAction = TimeUnit.DAYS.convert(Math.abs(basicSettingConfDate.getTime() - iconAddedDate.getTime()), TimeUnit.MILLISECONDS);
                }
                catch (final Exception exception) {
                    SecuritySettingsUtil.logger.log(Level.SEVERE, "getSecuritySettingsMeTrackerDetails:- Exception while getting security Icon or Configured date", exception);
                }
            }
            securitySettingsDetail.put("noOfDaysToConfigureBasicSettings", noOfWeeksBeforeSecurityAction);
        }
        catch (final Exception e) {
            SecuritySettingsUtil.logger.log(Level.SEVERE, "securitySettingsMeTrackerDetails():- ", e);
        }
        return securitySettingsDetail;
    }
    
    private Map getSecuritySettingMETrackerValues(final String[] paramKeys) {
        final Map securityParams = new HashMap();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("SecurityParams", "PARAM_NAME"), (Object)paramKeys, 8);
            final DataObject securityParamsDO = DataAccess.get("SecurityParams", criteria);
            final Iterator itr = securityParamsDO.getRows("SecurityParams");
            while (itr.hasNext()) {
                final Row securityParamRow = itr.next();
                securityParams.put(securityParamRow.get("PARAM_NAME").toString(), securityParamRow.get("PARAM_VALUE"));
            }
        }
        catch (final Exception ex) {
            SecuritySettingsUtil.logger.log(Level.SEVERE, "getSecurityParameters:- ", ex);
        }
        return securityParams;
    }
    
    static {
        SecuritySettingsUtil.logger = Logger.getLogger("SecurityLogger");
        SecuritySettingsUtil.securitySettingsUtil = null;
    }
}
