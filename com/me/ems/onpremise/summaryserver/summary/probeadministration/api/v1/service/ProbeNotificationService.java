package com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.ArrayList;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.util.ProbeNotificationUtil;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.util.ProbeNotificationConstants;
import java.util.HashMap;
import java.util.logging.Logger;

public class ProbeNotificationService
{
    public static Logger logger;
    
    public HashMap getNotificationSettings(final Long probeId) {
        final HashMap notificationDetails = new HashMap();
        notificationDetails.put("enableProbeDownNotification", ProbeNotificationUtil.isEventEnabledForProbe(probeId, ProbeNotificationConstants.PROBE_DOWN_NOTIFICATION));
        notificationDetails.put("enableDiskSpaceNotification", ProbeNotificationUtil.isEventEnabledForProbe(probeId, ProbeNotificationConstants.DISK_SPACE_NOTIFICATION));
        final boolean isEmailEnabled = ProbeNotificationUtil.isModeEnabledForProbe("EMAIL", probeId);
        notificationDetails.put("enableEmailNotification", isEmailEnabled);
        if (!isEmailEnabled) {
            notificationDetails.put("emailIDs", new ArrayList());
        }
        else {
            notificationDetails.put("emailIDs", ProbeNotificationUtil.getEmailAsList(probeId));
        }
        final boolean isSMSEnabled = ProbeNotificationUtil.isModeEnabledForProbe("SMS", probeId);
        notificationDetails.put("enableSMSNotification", isSMSEnabled);
        if (!isSMSEnabled) {
            notificationDetails.put("users", new ArrayList());
        }
        else {
            notificationDetails.put("users", ProbeNotificationUtil.getSMSUsers(probeId));
        }
        ProbeNotificationService.logger.log(Level.INFO, "Notification settings--> " + notificationDetails);
        return notificationDetails;
    }
    
    public HashMap updateNotificationSettings(final Long probeId, final Map notificationDetails) {
        final boolean isProbeDownEnabled = notificationDetails.get("enableProbeDownNotification");
        if (isProbeDownEnabled != ProbeNotificationUtil.isEventEnabledForProbe(probeId, ProbeNotificationConstants.PROBE_DOWN_NOTIFICATION)) {
            ProbeNotificationUtil.setEventEnabledStatus(ProbeNotificationConstants.PROBE_DOWN_NOTIFICATION, probeId, isProbeDownEnabled);
        }
        final boolean isDiskSpaceEnabled = notificationDetails.get("enableDiskSpaceNotification");
        if (isDiskSpaceEnabled != ProbeNotificationUtil.isEventEnabledForProbe(probeId, ProbeNotificationConstants.DISK_SPACE_NOTIFICATION)) {
            ProbeNotificationUtil.setEventEnabledStatus(ProbeNotificationConstants.DISK_SPACE_NOTIFICATION, probeId, isDiskSpaceEnabled);
        }
        final boolean isEmailEnabled = notificationDetails.get("enableEmailNotification");
        if (isEmailEnabled != ProbeNotificationUtil.isModeEnabledForProbe("EMAIL", probeId) && !isEmailEnabled) {
            ProbeNotificationUtil.setModeDisabled("EMAIL", probeId);
        }
        final boolean isSMSEnabled = notificationDetails.get("enableSMSNotification");
        if (isSMSEnabled != ProbeNotificationUtil.isModeEnabledForProbe("SMS", probeId) && !isSMSEnabled) {
            ProbeNotificationUtil.setModeDisabled("SMS", probeId);
        }
        if (isSMSEnabled) {
            final List<HashMap> users = notificationDetails.get("users");
            final DataObject dObj = this.listToDataObject(users, probeId);
            ProbeNotificationUtil.addOrUpdateSMSUser(dObj, probeId);
        }
        if (isEmailEnabled) {
            final List<String> emailIDs = notificationDetails.get("emailIDs");
            final String emailString = this.getEmailListAsString(emailIDs);
            ProbeNotificationUtil.addOrUpdateEmailAddr(probeId, emailString);
        }
        return this.getNotificationSettings(probeId);
    }
    
    public DataObject listToDataObject(final List users, final Long probeId) {
        final DataObject dObj = (DataObject)new WritableDataObject();
        try {
            for (int i = 0; i < users.size(); ++i) {
                final HashMap user = users.get(i);
                final Row row = new Row("ProbeNotificationSmsUser");
                row.set("PROBE_ID", (Object)probeId);
                row.set("USER_ID", (Object)Long.parseLong(user.get("userID")));
                dObj.addRow(row);
            }
        }
        catch (final DataAccessException e) {
            ProbeNotificationService.logger.log(Level.SEVERE, "Exception while passing users as DO due to ", (Throwable)e);
        }
        return dObj;
    }
    
    public String getEmailListAsString(final List emailIds) {
        String mailString = "";
        for (int i = 0; i < emailIds.size(); ++i) {
            if (i + 1 == emailIds.size()) {
                mailString += emailIds.get(i);
            }
            else {
                mailString = mailString + emailIds.get(i) + ",";
            }
        }
        return mailString;
    }
    
    static {
        ProbeNotificationService.logger = Logger.getLogger("probeActionsLogger");
    }
}
