package com.me.mdm.webclient.message;

import java.util.Hashtable;
import java.util.List;
import java.security.cert.X509Certificate;
import com.me.mdm.server.alerts.AlertConstants;
import com.me.devicemanagement.framework.server.license.LicensePercentHandler;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.enrollment.task.InactiveDevicePolicyTask;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.api.core.PasswordPolicyFacade;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import org.apache.commons.lang3.StringUtils;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.mdm.server.adep.DEPEnrollmentUtil;
import java.util.ArrayList;
import java.security.cert.CertificateExpiredException;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.File;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import java.util.Date;
import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.license.MDMLicenseImplMSP;
import com.me.mdm.server.android.knox.enroll.KnoxLicenseHandler;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.license.FreeEditionHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.message.MsgHandler;

public class MDMMsgHandler implements MsgHandler
{
    private Logger logger;
    
    public MDMMsgHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public Properties modifyMsgProperty(final Properties msgProperties, final Properties userDefined, final HttpServletRequest request) {
        try {
            Long customerID;
            if (userDefined != null && userDefined.get("CUSTOMER_ID") != null) {
                customerID = ((Hashtable<K, Long>)userDefined).get("CUSTOMER_ID");
            }
            else {
                customerID = MSPWebClientUtil.getCustomerID(request);
            }
            final Object[] args = null;
            final String msgName = ((Hashtable<K, String>)msgProperties).get("MSG_NAME");
            String msgContent = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
            String msgTitle = ((Hashtable<K, String>)msgProperties).get("MSG_TITLE");
            if (msgName.equalsIgnoreCase("APNS_ABOUT_TO_EXPIRED")) {
                final Long count = APNsCertificateHandler.getInstance().getAPNSExpiryPendingDays();
                msgContent = msgContent.replace("{0}", count + "");
                ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
            }
            if (msgName.equalsIgnoreCase("AET_ABOUT_TO_EXPIRE_READ") || msgName.equalsIgnoreCase("AET_ABOUT_TO_EXPIRE_WRITE")) {
                final Long expiryDate = WpAppSettingsHandler.getInstance().getWpAETExpiryDate(customerID);
                final Long daysRemain = WpAppSettingsHandler.getInstance().getAETExpiryPendingDays(expiryDate);
                msgContent = msgContent.replace("{0}", daysRemain + "");
                msgTitle = msgTitle.replace("{0}", daysRemain + "");
                ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
                ((Hashtable<String, String>)msgProperties).put("MSG_TITLE", msgTitle);
            }
            if (msgName.equalsIgnoreCase("CERT_ABOUT_TO_EXPIRE_READ") || msgName.equalsIgnoreCase("CERT_ABOUT_TO_EXPIRE_WRITE")) {
                final Long expiryDate = WpAppSettingsHandler.getInstance().getWpCertExpiryDate(customerID);
                final Long daysRemain = WpAppSettingsHandler.getInstance().getAETExpiryPendingDays(expiryDate);
                msgContent = msgContent.replace("{0}", daysRemain + "");
                msgTitle = msgTitle.replace("{0}", daysRemain + "");
                ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
                ((Hashtable<String, String>)msgProperties).put("MSG_TITLE", msgTitle);
            }
            if (msgName.equalsIgnoreCase("NEW_WINDOWS_APP_READ") || msgName.equalsIgnoreCase("NEW_WINDOWS_APP_WRITE")) {
                final String windowsAgentVersionLatest = MDMUtil.getProductProperty("windowsagentversion");
                msgTitle = msgTitle.replace("{0}", windowsAgentVersionLatest + "");
                ((Hashtable<String, String>)msgProperties).put("MSG_TITLE", msgTitle);
            }
            if (msgName.equalsIgnoreCase("LICENSE_LIMIT_REACHED_READ") || msgName.equalsIgnoreCase("LICENSE_LIMIT_REACHED_READ_MSP") || msgName.equalsIgnoreCase("LICENSE_LIMIT_REACHED_WRITE")) {
                final Boolean isModernMgmtCapable = MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable();
                int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount(MDMApiFactoryProvider.getMDMUtilAPI().getManagedDeviceCountCriteriaForLicenseCheck());
                if (isModernMgmtCapable) {
                    managedDeviceCount = Integer.valueOf(LicenseProvider.getInstance().getNoOfComutersManaged());
                }
                final String allowedManagedDeviceCount = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
                final String status = LicenseProvider.getInstance().isMobileDeviceLicenseLimitExceed(managedDeviceCount) ? "exceeded" : "reached";
                String licenseType = FreeEditionHandler.getInstance().isFreeEdition() ? "free" : "reg";
                if (isModernMgmtCapable) {
                    licenseType = "uem_" + licenseType;
                }
                String remoteAddress;
                if (request == null) {
                    remoteAddress = userDefined.getProperty("remote_address");
                }
                else {
                    remoteAddress = request.getRemoteAddr();
                }
                msgTitle = I18N.getMsg("dc.mdm.msg.license_" + status + ".title", new Object[0]);
                if (msgName.equalsIgnoreCase("LICENSE_LIMIT_REACHED_READ_MSP")) {
                    msgContent = I18N.getMsg("dc.mdm.msg." + licenseType + "_license_" + status + ".content.read", new Object[0]) + " " + I18N.getMsg("dc.mdm.msg.license_upgrade.msp.read", new Object[0]);
                }
                else if (msgName.equalsIgnoreCase("LICENSE_LIMIT_REACHED_READ")) {
                    msgContent = I18N.getMsg("dc.mdm.msg." + licenseType + "_license_" + status + ".content.write", new Object[] { allowedManagedDeviceCount, managedDeviceCount }) + " " + I18N.getMsg("dc.mdm.msg.license_upgrade.msp.read", new Object[0]);
                }
                else {
                    CustomerInfoUtil.getInstance();
                    if (CustomerInfoUtil.isSAS()) {
                        final Boolean showReferralMsg = (Boolean)ApiFactoryProvider.getCacheAccessAPI().getCache("SHOW_REFER_AND_EARN", 1);
                        msgContent = I18N.getMsg("dc.mdm.msg." + licenseType + "_license_" + status + ".content.write", new Object[] { allowedManagedDeviceCount, managedDeviceCount }) + " " + I18N.getMsg("dc.mdm.msg.license_upgrade.msp.write", new Object[] { LicenseProvider.getInstance().getStoreURL(remoteAddress) });
                        if (showReferralMsg != null && showReferralMsg) {
                            msgContent = msgContent + ", " + I18N.getMsg("dm.mdm.cloud.license_limit.refer_text", new Object[] { ApiFactoryProvider.getUtilAccessAPI().getServerURL() + "/webclient#/uems/mdm/admin/offers?showReferralTracking=true" });
                        }
                    }
                    else {
                        final StringBuilder append = new StringBuilder().append(I18N.getMsg("dc.mdm.msg." + licenseType + "_license_" + status + ".content.write", new Object[] { allowedManagedDeviceCount, managedDeviceCount })).append(" ").append(I18N.getMsg("dc.mdm.msg.license_upgrade.msp.write", new Object[0])).append(" <a href=\"").append(I18N.getMsg(LicenseProvider.getInstance().getStoreURL(remoteAddress), new Object[0])).append("?").append(ProductUrlLoader.getInstance().getGeneralProperites().getProperty("trackingcode")).append("&did=");
                        MDMUtil.getInstance();
                        final StringBuilder append2 = append.append(MDMUtil.getDIDValue()).append("\" target=\"_blank\">").append(I18N.getMsg("desktopcentral.common.buy_now", new Object[0])).append("</a> | <a target=\"_blank\" href=\"").append(I18N.getMsg(ProductUrlLoader.getInstance().getValue("get_quote"), new Object[0])).append("?p=").append(ProductUrlLoader.getInstance().getGeneralProperites().getProperty("trackingcode")).append("&did=");
                        MDMUtil.getInstance();
                        msgContent = append2.append(MDMUtil.getDIDValue()).append("\">").append(I18N.getMsg("desktopcentral.common.get_qoute", new Object[0])).append("</a>").toString();
                    }
                }
                ((Hashtable<String, String>)msgProperties).put("MSG_TITLE", msgTitle);
                ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
            }
            if (msgName.equalsIgnoreCase("KNOX_LICENSE_ABOUT_TO_EXPIRE") || msgName.equalsIgnoreCase("KNOX_LICENSE_ABOUT_TO_EXPIRE_READ")) {
                final Integer count2 = KnoxLicenseHandler.getInstance().knoxLicenseDaysToExpire(customerID);
                msgContent = msgContent.replace("{0}", count2 + "");
                ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
            }
            if ((msgName.equalsIgnoreCase("MSP_DEVICE_ALLOCATION_LIMIT_REACHED_READ") || msgName.equalsIgnoreCase("MSP_DEVICE_ALLOCATION_LIMIT_REACHED_WRITE")) && CustomerInfoUtil.getInstance().isMSP()) {
                final String licenseType2 = LicenseProvider.getInstance().getLicenseType();
                final MDMLicenseImplMSP mdmLicense = new MDMLicenseImplMSP();
                if (!licenseType2.equalsIgnoreCase("F")) {
                    final String modifyAllocationUrl = "/webclient/#/uems/mdm/admin/customers/" + customerID;
                    final String storeUrl = I18N.getMsg(ProductUrlLoader.getInstance().getValue("store_url"), new Object[0]) + "?did=" + SyMUtil.getDIDValue();
                    final int noOfAllocatedDevices = Integer.valueOf(mdmLicense.getNoOfMobileDevicesAllocated(customerID));
                    if (msgName.equalsIgnoreCase("MSP_DEVICE_ALLOCATION_LIMIT_REACHED_WRITE")) {
                        msgContent = msgContent.replace("{0}", modifyAllocationUrl);
                        msgContent = msgContent.replace("{1}", storeUrl);
                    }
                    ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
                }
            }
            if (msgName.equalsIgnoreCase("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING")) {
                try {
                    final String setTime = SyMUtil.getSyMParameter("MDM_ADDON_REMOVAL_TIME");
                    if (setTime != null) {
                        final Long currentTime = System.currentTimeMillis();
                        final Long expiryTime = Long.valueOf(setTime);
                        final long timeDiff = Math.abs(expiryTime - currentTime);
                        final long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
                        msgContent = msgContent.replace("{0}", String.valueOf(daysDiff));
                        ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
                    }
                }
                catch (final Exception ex) {
                    this.logger.log(Level.WARNING, "Exception while trying to parse date time for license warning message.", ex);
                }
            }
            if (msgName.equalsIgnoreCase("MDM_SSL_CERTIFICATE_ABOUT_TO_EXPIRE_WRITE") || msgName.equalsIgnoreCase("MDM_SSL_CERTIFICATE_ABOUT_TO_EXPIRE_READ")) {
                X509Certificate x509Certificate = null;
                Long noOfDays = 0L;
                Date todaysDate = new Date();
                try {
                    if (SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
                        x509Certificate = CertificateUtils.loadX509CertificateFromFile(new File(SSLCertificateUtil.getInstance().getServerCertificateFilePath()));
                        Date expiryDate2 = x509Certificate.getNotAfter();
                        final SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                        expiryDate2 = sdf.parse(sdf.format(expiryDate2));
                        todaysDate = sdf.parse(sdf.format(todaysDate));
                        noOfDays = expiryDate2.getTime() - todaysDate.getTime();
                        noOfDays /= 86400000L;
                        final int mobDeviceCount = SYMClientUtil.getMobileDeviceCount();
                        if (noOfDays <= 30L && noOfDays >= 0L) {
                            final String noOfDaysRemaining = (noOfDays <= 1L) ? ((noOfDays == 0L) ? I18N.getMsg("dc.calendar.today", new Object[0]) : (I18N.getMsg("dc.mdm.common.in", new Object[0]) + " " + I18N.getMsg("dc.mdm.device_mgmt.1_day", new Object[0]))) : (I18N.getMsg("dc.mdm.common.in", new Object[0]) + " " + noOfDays + " " + I18N.getMsg("dc.mdm.common.days", new Object[0]));
                            if (mobDeviceCount > 0) {
                                if (msgName.equalsIgnoreCase("MDM_SSL_CERTIFICATE_ABOUT_TO_EXPIRE_WRITE")) {
                                    msgContent = I18N.getMsg("dc.ssl.server.certificate.expiry.MdmUsagemsg", new Object[] { noOfDaysRemaining, " <a href=\"javascript:MdmMessageHandler.getSSLcertificateUrl()\" target=\"_self\" >" + I18N.getMsg("dc.ssl.server.certificate", new Object[0]) + "</a>" });
                                }
                                else {
                                    msgContent = I18N.getMsg("dc.ssl.server.certificate.expiry.MdmUsagemsg", new Object[] { noOfDaysRemaining, I18N.getMsg("dc.common.contact_your_admin", new Object[0]) });
                                }
                            }
                            else if (msgName.equalsIgnoreCase("MDM_SSL_CERTIFICATE_ABOUT_TO_EXPIRE_WRITE")) {
                                msgContent = I18N.getMsg("dc.ssl.server.certificate.expiry.msg", new Object[] { noOfDaysRemaining, " <a href=\"javascript:MdmMessageHandler.getSSLcertificateUrl()\" target=\"_self\" >" + I18N.getMsg("dc.ssl.server.certificate", new Object[0]) + "</a>" });
                            }
                            else {
                                msgContent = I18N.getMsg("dc.ssl.server.certificate.expiry.msg", new Object[] { noOfDaysRemaining, I18N.getMsg("dc.common.contact_your_admin", new Object[0]) });
                            }
                            msgTitle = I18N.getMsg("dc.ssl.server.certificate.expiry.alert", new Object[0]);
                            ((Hashtable<String, String>)msgProperties).put("MSG_TITLE", msgTitle);
                            ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
                        }
                    }
                }
                catch (final CertificateExpiredException ex2) {
                    this.logger.log(Level.WARNING, "Exception in certificte Expiry date .So the default Expiry msg opened...", ex2);
                }
            }
            if (msgName.equalsIgnoreCase("DEP_EXPIRED_MSG") || msgName.equalsIgnoreCase("DEP_ABOUT_TO_EXPIRE_MSG")) {
                List serverNameList = new ArrayList();
                if (msgName.equalsIgnoreCase("DEP_EXPIRED_MSG")) {
                    serverNameList = DEPEnrollmentUtil.getExpiredServerNames(customerID);
                }
                else if (msgName.equalsIgnoreCase("DEP_ABOUT_TO_EXPIRE_MSG")) {
                    serverNameList = DEPEnrollmentUtil.getAboutToExpireServerNames(customerID);
                }
                final int serverSize = serverNameList.size();
                if (serverSize == 0) {
                    this.logger.log(Level.WARNING, "Server list is empty , probably could have deleted  before message is closed. So closing Messages");
                    MessageProvider.getInstance().hideMessage("DEP_EXPIRED_MSG", customerID);
                    MessageProvider.getInstance().hideMessage("DEP_ABOUT_TO_EXPIRE_MSG", customerID);
                    this.logger.log(Level.WARNING, "Message is open but server list is empty . Going to validate token all");
                    DEPEnrollmentUtil.validateDEPTokenExpiry();
                }
                else {
                    final String serverCommaSeperated = StringUtils.join(serverNameList.toArray(), ',');
                    msgContent = msgContent.replace("{0}", serverCommaSeperated);
                    msgTitle = msgTitle.replace("{0}", serverCommaSeperated);
                    ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
                    ((Hashtable<String, String>)msgProperties).put("MSG_TITLE", msgTitle);
                }
            }
            if (msgName.contains("BLACKLIST_FOR_GROUPS_AND_DEVICES")) {
                final String blacklistedAppParam = CustomerParamsHandler.getInstance().getParameterValue("BlacklistMailCustomised", (long)customerID);
                boolean openBlacklistGroupMessage = false;
                if (!MDMStringUtils.isEmpty(blacklistedAppParam)) {
                    openBlacklistGroupMessage = Boolean.parseBoolean(blacklistedAppParam);
                }
                if (openBlacklistGroupMessage) {
                    msgContent = I18N.getMsg("mdm.blacklist.reconfigure_mail_template", new Object[] { "href='#/uems/mdm/inventory/apps/settings'" });
                }
                ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
            }
            if (msgName.equalsIgnoreCase("PASSWORD_POLCY")) {
                final JSONObject filterJSON = new JSONObject();
                filterJSON.put("user_name", (Object)DMUserHandler.getUserNameFromUserID(MDMUtil.getInstance().getCurrentlyLoggedOnUserID()));
                final JSONObject headerJSON = new JSONObject().put("filters", (Object)filterJSON);
                ((Hashtable<String, JSONObject>)msgProperties).put("MSG_CONTENT", new PasswordPolicyFacade().getPasswordPolicyDetails(new JSONObject().put("msg_header", (Object)headerJSON)));
            }
            if (msgName.equalsIgnoreCase("USER_LICENSE_LIMIT_REACHED")) {
                final JSONObject responseJSON = new JSONObject();
                final int allowedTechCount = Integer.parseInt(LicenseProvider.getInstance().getNoOfTechnicians());
                responseJSON.put("allowed_technician_count", allowedTechCount);
                boolean isLimitReached = false;
                final int technicianCreatedCount = MDMRestAPIFactoryProvider.getTechnicianFacade().getTotalTechniciansCount(customerID);
                if (allowedTechCount == technicianCreatedCount || allowedTechCount < technicianCreatedCount) {
                    isLimitReached = true;
                }
                responseJSON.put("is_technician_limit_reached", isLimitReached);
                responseJSON.put("technician_count", technicianCreatedCount);
                ((Hashtable<String, JSONObject>)msgProperties).put("MSG_CONTENT", responseJSON);
            }
            if (msgName.equalsIgnoreCase("LANGUAGE_PACK_STATUS")) {
                ((Hashtable<String, JSONObject>)msgProperties).put("MSG_CONTENT", new JSONObject().put("IsLanguagePackEnabled", LicenseProvider.getInstance().isLanguagePackEnabled()));
            }
            if (msgName.equalsIgnoreCase("INACTIVE_DEVICE_FOUND")) {
                final int inactiveDeviceCount = new InactiveDevicePolicyTask().getInactiveDeviceCounts(customerID);
                final JSONObject thresholdJson = new InactiveDevicePolicyTask().getInactiveDevicePolicyThresholdValues(customerID);
                final Long inactiveThreshold = thresholdJson.getLong("InactiveThreshold");
                final int inactiveThresholdInDays = (int)(inactiveThreshold / 86400000L);
                if (inactiveDeviceCount > 0) {
                    msgContent = msgContent.replace("{0}", String.valueOf(inactiveDeviceCount));
                    msgContent = msgContent.replace("{1}", String.valueOf(inactiveThresholdInDays));
                    if (MDMEnrollmentUtil.getInstance().isIDPConfigured(customerID)) {
                        msgContent = msgContent.replace("{2}", I18N.getMsg(MDMUtil.replaceProductUrlLoaderValuesinText("mdm.inactive_devices_schedule_report_link", null), new Object[0]));
                    }
                    else {
                        msgContent = msgContent.replace("{2}", I18N.getMsg(MDMUtil.replaceProductUrlLoaderValuesinText("mdm.inactive_policy_link", null), new Object[0]));
                    }
                    ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
                }
                else {
                    MessageProvider.getInstance().hideMessage("INACTIVE_DEVICE_FOUND", customerID);
                }
            }
            if (msgName.equalsIgnoreCase("ANTI_VIRUS_MESSAGE")) {
                final String productDisplayName = ProductUrlLoader.getInstance().getValue("displayname");
                final String trackingCode = ProductUrlLoader.getInstance().getValue("trackingcode");
                msgContent = msgContent.replace("{0}", productDisplayName);
                msgTitle = msgTitle.replace("{0}", trackingCode);
                ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
                ((Hashtable<String, String>)msgProperties).put("MSG_TITLE", msgTitle);
            }
            if (msgName.equalsIgnoreCase("LOST_DEVICE_FOUND_MSG")) {
                final int isGeoTrackingEnabled = LocationSettingsDataHandler.getInstance().getLocationTrackingStatus(customerID);
                if (isGeoTrackingEnabled > 0 && isGeoTrackingEnabled != 3) {
                    msgContent += I18N.getMsg(MDMUtil.replaceProductUrlLoaderValuesinText("mdm.lost_device_loc_link", null), new Object[0]);
                }
                ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
            }
            if (msgName.equalsIgnoreCase("MDM_DEVICE_LICENSE_PERCENT_ALERT") || msgName.equalsIgnoreCase("MDM_DEVICE_LICENSE_PERCENT_ALERT_READ")) {
                final int purchasedMobileDeviceCount = ManagedDeviceHandler.getInstance().getPurchasedMobileDeviceCount(customerID);
                final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount(MDMApiFactoryProvider.getMDMUtilAPI().getManagedDeviceCountCriteriaForLicenseCheck());
                final int licensePercent = Math.round(managedDeviceCount / (float)purchasedMobileDeviceCount * 100.0f);
                final int maxLicenseAlertPercent = LicensePercentHandler.getInstance().getDeviceLicPercent(customerID, AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED, AlertConstants.LicenseAlertConstant.GREATER_THAN);
                if (maxLicenseAlertPercent != -1 && maxLicenseAlertPercent <= licensePercent && purchasedMobileDeviceCount != -1) {
                    msgTitle = msgTitle.replace("{0}", String.valueOf(licensePercent));
                    msgContent = msgContent.replace("{0}", String.valueOf(purchasedMobileDeviceCount));
                    ((Hashtable<String, String>)msgProperties).put("MSG_TITLE", msgTitle);
                    ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in MdMMessageHandler...............", e);
        }
        return msgProperties;
    }
}
