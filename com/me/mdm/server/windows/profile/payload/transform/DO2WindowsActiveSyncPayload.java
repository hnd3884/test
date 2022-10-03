package com.me.mdm.server.windows.profile.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.persistence.Row;
import com.me.mdm.server.windows.profile.payload.WindowsActiveSyncPayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;

public class DO2WindowsActiveSyncPayload extends DO2WindowsPayload
{
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        final WindowsActiveSyncPayload windowsActiveSyncPayload = new WindowsActiveSyncPayload();
        windowsActiveSyncPayload.getReplacePayloadCommand().addRequestItem(windowsActiveSyncPayload.createTargetItemTagElement("%activesync_payload_xml%"));
        WindowsActiveSyncPayload syncPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("WpExchangeActiveSyncPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String guid = (String)row.get("GUID");
                final String emailAddress = (String)row.get("EMAIL_ADDRESS");
                final String domainName = (String)row.get("DOMIN_NAME");
                final String accountIconPath = (String)row.get("ACCOUNT_ICON_NAME");
                final String accountType = (String)row.get("ACCOUNT_TYPE");
                final String accountName = (String)row.get("ACCOUNT_NAME");
                final String serverName = (String)row.get("SERVER_NAME");
                final String userName = (String)row.get("USER_NAME");
                final Boolean useSSL = (Boolean)row.get("USE_SSL");
                final Integer schedule = (Integer)row.get("SCHEDULE");
                final Integer mailAgeFilter = (Integer)row.get("MAIL_AGE_FILTER");
                final Integer logging = (Integer)row.get("LOGGING");
                final Boolean isEmailContentTypeEnabled = (Boolean)row.get("IS_CON_TYPE_EMAIL_ENABLED");
                final Boolean isContactContenetTypeEnabled = (Boolean)row.get("IS_CON_TYPE_CONTACT_ENABLED");
                final Boolean isCalendarContenetTypeEnabled = (Boolean)row.get("IS_CON_TYPE_CAL_ENABLED");
                final Boolean isTaskContentTypeEnabled = (Boolean)row.get("IS_CON_TYPE_TASK_ENABLED");
                final String emailAddressConetentTypeName = (String)row.get("CONTENT_TYPE_EMAIL_NAME");
                final String contactsConetntTypeName = (String)row.get("CONTENT_TYPE_CONTACT_NAME");
                final String calendarConetentTypeName = (String)row.get("CONTENT_TYPE_CAL_NAME");
                final String taskContentTypeName = (String)row.get("CONTENT_TYPE_TASK_NAME");
                String sUseSSL = "0";
                String sEmailContentTypeEnabled = "0";
                String sContactContenetTypeEnabled = "0";
                String sCalendarContenetTypeEnabled = "0";
                String sTaskContentTypeEnabled = "0";
                if (!useSSL) {
                    sUseSSL = "0";
                }
                if (isEmailContentTypeEnabled) {
                    sEmailContentTypeEnabled = "1";
                }
                if (isContactContenetTypeEnabled) {
                    sContactContenetTypeEnabled = "1";
                }
                if (isCalendarContenetTypeEnabled) {
                    sCalendarContenetTypeEnabled = "1";
                }
                if (isTaskContentTypeEnabled) {
                    sTaskContentTypeEnabled = "1";
                }
                syncPayload = new WindowsActiveSyncPayload();
                if (guid != null) {
                    syncPayload.setGuid(guid);
                    syncPayload.setEmailAddress(emailAddress, guid);
                    if (domainName != null && !domainName.trim().isEmpty() && serverName != null && !serverName.trim().equals("outlook.office365.com")) {
                        syncPayload.setDomain(domainName, guid);
                    }
                    syncPayload.setAccountType(accountType, guid);
                    syncPayload.setAccountName(accountName, guid);
                    syncPayload.setServerName(serverName, guid);
                    syncPayload.setUserName(userName, guid);
                    if (!useSSL) {
                        syncPayload.setUseSSL(sUseSSL, guid);
                    }
                    syncPayload.setSchedule(schedule, guid);
                    syncPayload.setMailAgeFilter(mailAgeFilter, guid);
                    syncPayload.setLogging(logging, guid);
                    syncPayload.setEmailContentType(sEmailContentTypeEnabled, guid);
                    syncPayload.setContactsContentType(sContactContenetTypeEnabled, guid);
                    syncPayload.setCalendarContentType(sCalendarContenetTypeEnabled, guid);
                    syncPayload.setTaskContentType(sTaskContentTypeEnabled, guid);
                    syncPayload.setEmailContentTypeName(emailAddressConetentTypeName, guid);
                    syncPayload.setContactsContentTypeName(contactsConetntTypeName, guid);
                    syncPayload.setCalendarContentTypeName(calendarConetentTypeName, guid);
                    syncPayload.setTaskContentTypeName(taskContentTypeName, guid);
                    String password = "";
                    if (row.get("PASSWORD_ID") != null) {
                        final Long incomingServerPasswordId = (Long)row.get("PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(incomingServerPasswordId.toString());
                    }
                    syncPayload.setPassword(password, guid);
                }
            }
            this.packOsSpecificPayloadToXML(dataObject, syncPayload, "install", "WindowsActiveSync");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error while creating ActiveSync payload ", ex);
        }
        return windowsActiveSyncPayload;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        final WindowsActiveSyncPayload payload = new WindowsActiveSyncPayload();
        payload.getDeletePayloadCommand().addRequestItem(payload.createTargetItemTagElement("%activesync_payload_xml%"));
        WindowsActiveSyncPayload windowsPayload = null;
        try {
            windowsPayload = new WindowsActiveSyncPayload();
            final Iterator iterator = dataObject.getRows("WpExchangeActiveSyncPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String guid = (String)row.get("GUID");
                if (guid != null) {
                    windowsPayload.setRemoveProfilePayload(guid);
                }
            }
            this.packOsSpecificPayloadToXML(dataObject, windowsPayload, "remove", "WindowsActiveSync");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error while creating ActiveSync remove payload ", ex);
        }
        return payload;
    }
}
