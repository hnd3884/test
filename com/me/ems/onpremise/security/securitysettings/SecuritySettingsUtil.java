package com.me.ems.onpremise.security.securitysettings;

import com.me.ems.framework.securitysettings.api.v1.model.SecuritySettingsModel;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import java.util.Map;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.onpremise.webclient.admin.certificate.CertificateUtil;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorAction;
import java.util.logging.Logger;

public class SecuritySettingsUtil
{
    private static Logger logger;
    private static SecuritySettingsUtil opSecuritySettingsUtil;
    
    private SecuritySettingsUtil() {
    }
    
    public static SecuritySettingsUtil getInstance() {
        return (SecuritySettingsUtil.opSecuritySettingsUtil == null) ? new SecuritySettingsUtil() : SecuritySettingsUtil.opSecuritySettingsUtil;
    }
    
    public String getSecurityAdvisoryCodeAfterAddedAnyUser() {
        String advisoryCode = "SAC000";
        try {
            if (!TwoFactorAction.isTwoFactorEnabledGlobaly()) {
                advisoryCode = "SAC001";
                METrackerUtil.incrementMETrackParams("enableTfaAlertCount");
            }
            else if (!this.isDefaultAdminDisabled()) {
                advisoryCode = "SAC002";
                METrackerUtil.incrementMETrackParams("removeDefaultAdminAlertCount");
            }
            else if (!this.isSecureCommunicationEnabled()) {
                advisoryCode = "SAC003";
                METrackerUtil.incrementMETrackParams("secureLoginAlertCount");
            }
            SecuritySettingsUtil.logger.log(Level.INFO, "getSecurityAdvisoryCodeAfterAddedAnyUser():- code is :{0}", new Object[] { advisoryCode });
        }
        catch (final Exception e) {
            SecuritySettingsUtil.logger.log(Level.SEVERE, "getSecurityAdvisoryCodeAfterAddedAnyUser() :- Caught an Exception : ", e);
        }
        return advisoryCode;
    }
    
    public String getSecurityAdvisoryCodeAfterSavedNatSetting() {
        String advisoryCode = "SAC000";
        try {
            if (!this.isThirdPartSSLCertificateUsed()) {
                advisoryCode = "SAC004";
                METrackerUtil.incrementMETrackParams("sslCertAlertCount");
            }
            SecuritySettingsUtil.logger.log(Level.INFO, "getSecurityAdvisoryCodeAfterSavedNatSetting():- code is :{0}", new Object[] { advisoryCode });
        }
        catch (final Exception e) {
            SecuritySettingsUtil.logger.log(Level.SEVERE, "getSecurityAdvisoryCodeAfterSavedNatSetting() :- Caught an Exception : ", e);
        }
        return advisoryCode;
    }
    
    public Boolean isSecureCommunicationEnabled() throws Exception {
        return Boolean.parseBoolean(SyMUtil.getSyMParameter("ENABLE_HTTPS"));
    }
    
    public Boolean isDefaultAdminDisabled() throws DataAccessException {
        final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
        return DMOnPremiseUserUtil.isDefaultAdminDisabled(defaultAdminUVHLoginID);
    }
    
    public Boolean isThirdPartSSLCertificateUsed() throws Exception {
        if (CertificateUtil.getInstance().getServerCertificateWebSettingsFilePath() != null) {
            return SSLCertificateUtil.getInstance().isThirdPartySSLInstalled();
        }
        return false;
    }
    
    public Map getSecuritySettingPopupMETrackerValues() {
        final Map meTracker = new HashMap();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("METrackParams", "PARAM_NAME"), (Object)new String[] { "securityIconCount", "securityAdviceCount", "enableTfaAlertCount", "removeDefaultAdminAlertCount", "secureLoginAlertCount", "sslCertAlertCount", "nwShareAccessAlertCount", "isTfaEnabledViaAlert", "isDefaultAdminDeletedViaAlert", "isSecureLoginEnabledViaAlert", "isSslCertAddedViaAlert", "isNwShareSavedViaAlert" }, 8);
            final DataObject securityParamsDO = DataAccess.get("METrackParams", criteria);
            final Iterator itr = securityParamsDO.getRows("METrackParams");
            meTracker.put("securityIconCount", 0);
            meTracker.put("securityAdviceCount", 0);
            meTracker.put("enableTfaAlertCount", 0);
            meTracker.put("removeDefaultAdminAlertCount", 0);
            meTracker.put("secureLoginAlertCount", 0);
            meTracker.put("sslCertAlertCount", 0);
            meTracker.put("nwShareAccessAlertCount", 0);
            meTracker.put("isTfaEnabledViaAlert", "false");
            meTracker.put("isDefaultAdminDeletedViaAlert", "false");
            meTracker.put("isSecureLoginEnabledViaAlert", "false");
            meTracker.put("isSslCertAddedViaAlert", "false");
            meTracker.put("isNwShareSavedViaAlert", "false");
            while (itr.hasNext()) {
                final Row meTrackerParamsRow = itr.next();
                meTracker.put(meTrackerParamsRow.get("PARAM_NAME").toString(), meTrackerParamsRow.get("PARAM_VALUE"));
            }
        }
        catch (final Exception ex) {
            SecuritySettingsUtil.logger.log(Level.SEVERE, "getSecuritySettingPopupMETrackerValues:- ", ex);
        }
        return meTracker;
    }
    
    private Boolean isSecureGatewayServerUsed() {
        return Boolean.parseBoolean(SyMUtil.getSyMParameter("forwarding_server_config"));
    }
    
    public void setSecureGatewayServerUsed(final SecuritySettingsModel securitySettingsModel) throws Exception {
        securitySettingsModel.incrementAdvancedSettingTotalScore(1.0);
        if (this.isSecureGatewayServerUsed()) {
            securitySettingsModel.setAdvancedSettings("isSecureGatewayServerUsed", (Object)Boolean.TRUE);
            securitySettingsModel.incrementAdvancedSettingSecureScore(1.0);
        }
        else {
            securitySettingsModel.setAdvancedSettings("isSecureGatewayServerUsed", (Object)Boolean.FALSE);
        }
    }
    
    static {
        SecuritySettingsUtil.logger = Logger.getLogger("SecurityLogger");
        SecuritySettingsUtil.opSecuritySettingsUtil = null;
    }
}
