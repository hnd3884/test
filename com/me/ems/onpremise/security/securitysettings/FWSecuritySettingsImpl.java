package com.me.ems.onpremise.security.securitysettings;

import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorAction;
import com.me.ems.onpremise.server.core.EnforceSecurePortUtil;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.settings.SettingsUtil;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import com.me.ems.onpremise.server.util.NotifyUpdatesUtil;
import com.me.devicemanagement.onpremise.server.service.DCServerBuildHistoryProvider;
import com.me.devicemanagement.onpremise.server.util.UpdatesParamUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.ems.framework.securitysettings.api.v1.model.SecuritySettingsModel;
import com.me.ems.framework.securitysettings.api.core.SecuritySettingsAPI;

public class FWSecuritySettingsImpl implements SecuritySettingsAPI
{
    private SecuritySettingsUtil opSecuritySettingsUtil;
    
    public FWSecuritySettingsImpl() {
        this.opSecuritySettingsUtil = SecuritySettingsUtil.getInstance();
    }
    
    public void getSecuritySettings(final SecuritySettingsModel securitySettingsModel) throws Exception {
        this.setSecureCommunicationEnabled(securitySettingsModel);
        this.setDefaultAdminDisabled(securitySettingsModel);
        this.setThirdPartSSLCertificateUsed(securitySettingsModel);
        this.setTwoFactorEnabled(securitySettingsModel);
        if (!SyMUtil.isProbeServer() && !SyMUtil.isSummaryServer()) {
            this.setCentralServerUpgrade(securitySettingsModel);
        }
        securitySettingsModel.setExtraVal("noOfTechBuy", (Object)LicenseProvider.getInstance().getNoOfTechnicians());
    }
    
    private void setCentralServerUpgrade(final SecuritySettingsModel securitySettingsModel) {
        securitySettingsModel.incrementBasicSettingTotalScore(1.0);
        String latestVersion = UpdatesParamUtil.getUpdParameter("LATEST_BUILD_VERSION");
        boolean isUpToDate = true;
        boolean hasSecurityUpdate = false;
        if (latestVersion != null) {
            latestVersion = latestVersion.replaceAll("\\.", "");
            if (DCServerBuildHistoryProvider.getInstance().getCurrentBuildNumberFromDB() < Integer.parseInt(latestVersion)) {
                isUpToDate = false;
            }
        }
        final String updateMsgPriority = UpdatesParamUtil.getUpdParameter("UPDATE_MESSAGE_PRIORITY");
        if (!isUpToDate && updateMsgPriority != null && updateMsgPriority.equals("1")) {
            hasSecurityUpdate = true;
        }
        final String eolDate = UpdatesParamUtil.getUpdParameter("EOL_DATE");
        if ((eolDate != null && NotifyUpdatesUtil.getInstance().getEOSState(6L) != 0) || hasSecurityUpdate) {
            securitySettingsModel.setBasicSettings("isCentralServerUpgraded", (Object)false);
            securitySettingsModel.setBasicSettings("isEOSAnnounced", (Object)(NotifyUpdatesUtil.getInstance().getEOSState(6L) != 0));
            securitySettingsModel.setBasicSettings("isSecurityFixAvailable", (Object)hasSecurityUpdate);
            securitySettingsModel.setBasicSettings("servicePackUrl", (Object)UpdatesParamUtil.getUpdParameter("UPDATE_DOWNLOAD_URL"));
        }
        else {
            securitySettingsModel.incrementBasicSettingSecureScore(1.0);
            securitySettingsModel.setBasicSettings("isCentralServerUpgraded", (Object)true);
        }
    }
    
    public void saveSecuritySettings(final Map securitySettingsDetails, final String userName, final Long customerId) throws APIException {
    }
    
    public void getSecuritySettingsAlertDetails(final SecuritySettingsModel securitySettingsModel) throws Exception {
        if (SettingsUtil.isNATConfigured()) {
            securitySettingsModel.setRedirectionNeed(true);
            return;
        }
        if (ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
            securitySettingsModel.setRedirectionNeed(true);
            return;
        }
        if (DBUtil.getRecordCount("IntegrationService", "NAME", (Criteria)null) > 0) {
            securitySettingsModel.setRedirectionNeed(true);
        }
    }
    
    public Map getSecurityEnforceDetails(final SecuritySettingsModel securitySettingsModel) throws Exception {
        return null;
    }
    
    private void setSecureCommunicationEnabled(final SecuritySettingsModel securitySettingsModel) throws Exception {
        securitySettingsModel.incrementBasicSettingTotalScore(1.0);
        if (EnforceSecurePortUtil.isInsecureCommDisabled()) {
            securitySettingsModel.setBasicSettings("isInsecureCommunicationDisabled", (Object)Boolean.TRUE);
            securitySettingsModel.incrementBasicSettingSecureScore(1.0);
        }
        else {
            securitySettingsModel.setBasicSettings("isInsecureCommunicationDisabled", (Object)Boolean.FALSE);
            if (this.opSecuritySettingsUtil.isSecureCommunicationEnabled()) {
                securitySettingsModel.setBasicSettings("isHttpsLoginEnabled", (Object)Boolean.TRUE);
            }
            else {
                securitySettingsModel.setBasicSettings("isHttpsLoginEnabled", (Object)Boolean.FALSE);
            }
        }
    }
    
    private void setDefaultAdminDisabled(final SecuritySettingsModel securitySettingsModel) throws Exception {
        securitySettingsModel.incrementBasicSettingTotalScore(1.0);
        if (this.opSecuritySettingsUtil.isDefaultAdminDisabled()) {
            securitySettingsModel.setBasicSettings("isDefaultAdminDisabled", (Object)Boolean.TRUE);
            securitySettingsModel.incrementBasicSettingSecureScore(1.0);
        }
        else {
            securitySettingsModel.setBasicSettings("isDefaultAdminDisabled", (Object)Boolean.FALSE);
        }
    }
    
    private void setThirdPartSSLCertificateUsed(final SecuritySettingsModel securitySettingsModel) throws Exception {
        securitySettingsModel.incrementAdvancedSettingTotalScore(1.0);
        if (this.opSecuritySettingsUtil.isThirdPartSSLCertificateUsed()) {
            securitySettingsModel.setAdvancedSettings("isThirdPartyCertificateUsed", (Object)Boolean.TRUE);
            securitySettingsModel.incrementAdvancedSettingSecureScore(1.0);
        }
        else {
            securitySettingsModel.setAdvancedSettings("isThirdPartyCertificateUsed", (Object)Boolean.FALSE);
        }
    }
    
    private void setTwoFactorEnabled(final SecuritySettingsModel securitySettingsModel) throws Exception {
        securitySettingsModel.incrementBasicSettingTotalScore(1.0);
        if (TwoFactorAction.isTwoFactorEnabledGlobaly()) {
            securitySettingsModel.setBasicSettings("isTwoFactorEnabled", (Object)Boolean.TRUE);
            securitySettingsModel.incrementBasicSettingSecureScore(1.0);
        }
        else {
            final Integer tfaStatus = ApiFactoryProvider.getTFAService().getTFAEnforcementDetails().get("tfaStatus");
            securitySettingsModel.setTfaBannerNeed(tfaStatus.equals(1) || tfaStatus.equals(2));
            securitySettingsModel.setBasicSettings("isTwoFactorEnabled", (Object)Boolean.FALSE);
        }
    }
}
