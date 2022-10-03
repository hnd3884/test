package com.me.mdm.server.easmanagement;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.alerts.MDMAlertConstants;
import com.me.devicemanagement.framework.server.mailmanager.MailContentGeneratorUtil;
import java.io.File;
import java.util.Properties;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Collection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.concurrent.TimeUnit;
import com.me.devicemanagement.framework.server.util.Utils;

public class EASMailHandler
{
    private static EASMailHandler easMailHandler;
    
    public static EASMailHandler getInstance() {
        if (EASMailHandler.easMailHandler == null) {
            EASMailHandler.easMailHandler = new EASMailHandler();
        }
        return EASMailHandler.easMailHandler;
    }
    
    private String calculateGracePeriodEndDate(final Long gracePeriodExpiresIn) {
        Long gracePeriodExpiryDate = System.currentTimeMillis();
        if (gracePeriodExpiresIn >= 0L) {
            gracePeriodExpiryDate += gracePeriodExpiresIn;
        }
        return "(" + Utils.getEventDate(gracePeriodExpiryDate) + ").";
    }
    
    private String convertTimeToString(final Long gracePeriodExpiresIn) {
        if (gracePeriodExpiresIn != null && gracePeriodExpiresIn > 0L) {
            final String gracePeriodEndDate = this.calculateGracePeriodEndDate(gracePeriodExpiresIn);
            final Long days = TimeUnit.MILLISECONDS.toDays(gracePeriodExpiresIn);
            final Long hours = TimeUnit.MILLISECONDS.toHours(gracePeriodExpiresIn);
            final Long minutes = TimeUnit.MILLISECONDS.toMinutes(gracePeriodExpiresIn);
            if (days != 0L) {
                return "in " + String.valueOf(days) + " day(s)" + gracePeriodEndDate;
            }
            if (hours != 0L) {
                return "in " + hours + " hours" + gracePeriodEndDate;
            }
            if (minutes != 0L) {
                return "in " + minutes + " minutes" + gracePeriodEndDate;
            }
        }
        return "shortly";
    }
    
    private void sendTheMail(final Long customerID, final int sendNotifMail, final Boolean sendGraceMails, final JSONObject mailboxPropsJSON) {
        try {
            Row easDeviceRow = null;
            String deviceListStr = "";
            Iterator deviceItr = null;
            JSONObject deviceJSON = null;
            DataObject easDeviceDObj = null;
            MDMAlertMailGeneratorUtil alertMailGenerator = null;
            final JSONArray mailboxBlockedDeviceList = (JSONArray)mailboxPropsJSON.get((Object)"TO_BE_BLOCKED");
            final JSONArray mailboxNonAllowedDeviceList = (JSONArray)mailboxPropsJSON.get((Object)"TO_BE_NOT_ALOLWED");
            final Boolean graced = (Boolean)mailboxPropsJSON.get((Object)"IS_MANAGED_USER_MAILBOX_EAS_GRACED");
            final JSONArray notManagedAllowedDevicesJSONArray = new JSONArray();
            if (mailboxBlockedDeviceList != null) {
                notManagedAllowedDevicesJSONArray.addAll((Collection)mailboxBlockedDeviceList);
            }
            if (mailboxNonAllowedDeviceList != null) {
                notManagedAllowedDevicesJSONArray.addAll((Collection)mailboxNonAllowedDeviceList);
            }
            easDeviceDObj = MDMUtil.getPersistence().constructDataObject();
            deviceItr = notManagedAllowedDevicesJSONArray.iterator();
            final String serverUrl = MDMEnrollmentUtil.getInstance().getServerBaseURL();
            final String selfenrollmentURL = serverUrl + "/mdm/enroll";
            while (deviceItr.hasNext()) {
                deviceJSON = deviceItr.next();
                easDeviceRow = new Row("EASDeviceDetails");
                easDeviceRow.set("EAS_DEVICE_IDENTIFIER", deviceJSON.get((Object)"EAS_DEVICE_IDENTIFIER"));
                easDeviceRow.set("DEVICE_NAME", deviceJSON.get((Object)"DEVICE_NAME"));
                easDeviceRow.set("DEVICE_MODEL", deviceJSON.get((Object)"DEVICE_MODEL"));
                easDeviceRow.set("DEVICE_IMEI", deviceJSON.get((Object)"DEVICE_IMEI"));
                easDeviceRow.set("DEVICE_OS", deviceJSON.get((Object)"DEVICE_OS"));
                easDeviceDObj.addRow(easDeviceRow);
            }
            if (notManagedAllowedDevicesJSONArray.size() > 0) {
                if (graced) {
                    if (sendGraceMails && (sendNotifMail & 0x1) == 0x1) {
                        final String gracePeriodExpiresIn = this.convertTimeToString((Long)mailboxPropsJSON.get((Object)"$days_to_expire$"));
                        alertMailGenerator = new MDMAlertMailGeneratorUtil(EASMgmt.logger);
                        final Properties mailProperties = new Properties();
                        ((Hashtable<String, Object>)mailProperties).put("$user_emailid$", mailboxPropsJSON.get((Object)"EMAIL_ADDRESS"));
                        ((Hashtable<String, Object>)mailProperties).put("$eas_user_name$", mailboxPropsJSON.get((Object)"DISPLAY_NAME"));
                        ((Hashtable<String, String>)mailProperties).put("$days_to_expire$", gracePeriodExpiresIn);
                        ((Hashtable<String, String>)mailProperties).put("$self_enroll_url$", selfenrollmentURL);
                        final String xslFile = MDMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "MDM" + File.separator + "xsl" + File.separator + "easDeviceList.xsl";
                        final String xslFile2 = MDMUtil.createI18NxslFile(xslFile, "easDeviceSummary-temp.xsl");
                        final MailContentGeneratorUtil mailGenerator = new MailContentGeneratorUtil(EASMgmt.logger);
                        deviceListStr = mailGenerator.getHTMLContent(xslFile2, easDeviceDObj, "easDevices");
                        ((Hashtable<String, String>)mailProperties).put("$exchange_blocked_devices$", deviceListStr);
                        alertMailGenerator.sendMail(MDMAlertConstants.MDM_EAS_NOTIFY_USER, "MDM_EAS_NOTIFY_USER", customerID, mailProperties);
                    }
                }
                else if ((sendNotifMail & 0x2) == 0x2) {
                    final String gracePeriodExpiresIn = this.convertTimeToString((Long)mailboxPropsJSON.get((Object)"$days_to_expire$"));
                    alertMailGenerator = new MDMAlertMailGeneratorUtil(EASMgmt.logger);
                    final Properties mailProperties = new Properties();
                    ((Hashtable<String, Object>)mailProperties).put("$user_emailid$", mailboxPropsJSON.get((Object)"EMAIL_ADDRESS"));
                    ((Hashtable<String, Object>)mailProperties).put("$eas_user_name$", mailboxPropsJSON.get((Object)"DISPLAY_NAME"));
                    ((Hashtable<String, String>)mailProperties).put("$days_to_expire$", gracePeriodExpiresIn);
                    ((Hashtable<String, String>)mailProperties).put("$self_enroll_url$", selfenrollmentURL);
                    final String xslFile = MDMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "MDM" + File.separator + "xsl" + File.separator + "easDeviceList.xsl";
                    final String xslFile2 = MDMUtil.createI18NxslFile(xslFile, "easDeviceSummary-temp.xsl");
                    final MailContentGeneratorUtil mailGenerator = new MailContentGeneratorUtil(EASMgmt.logger);
                    deviceListStr = mailGenerator.getHTMLContent(xslFile2, easDeviceDObj, "easDevices");
                    ((Hashtable<String, String>)mailProperties).put("$exchange_blocked_devices$", deviceListStr);
                    alertMailGenerator.sendMail(MDMAlertConstants.MDM_EAS_NOTIFY_USER, "MDM_EAS_NOTIFY_USER", customerID, mailProperties);
                }
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.INFO, null, ex);
        }
    }
    
    public void sendMails(final Long customerID, final Long easServerID, final Boolean sendGraceMails, final JSONArray jsArray) {
        final int sendNotifMail = EASMgmtDataHandler.getInstance().getSendNotifMailSetting(easServerID);
        if (sendNotifMail != 0) {
            for (final Object jsObject : jsArray) {
                final JSONObject mailboxPropsJSON = (JSONObject)jsObject;
                this.sendTheMail(customerID, sendNotifMail, sendGraceMails, mailboxPropsJSON);
            }
        }
    }
    
    static {
        EASMailHandler.easMailHandler = null;
    }
}
