package com.adventnet.sym.server.mdm.android.payload.transform;

import java.io.InputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.sym.server.mdm.android.payload.AndroidActiveSyncPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;

public class DO2AndroidActiveSyncPayload implements DO2AndroidPayload
{
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidActiveSyncPayload activeSyncPayload = null;
        try {
            if (dataObject != null) {
                Long certID = 0L;
                final Iterator iterator = dataObject.getRows("AndroidActiveSyncPolicy");
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String emailAddress = (String)payloadRow.get("EMAIL_ADDRESS");
                    final String userName = (String)payloadRow.get("USER_NAME");
                    final String domainName = (String)payloadRow.get("DOMAIN");
                    final String activeSyncHost = (String)payloadRow.get("ACTIVE_SYNC_HOST");
                    final String dispName = (String)payloadRow.get("ACCOUNT_NAME");
                    final boolean acceptAllCertificate = (boolean)payloadRow.get("ACCEPT_ALL_CERTIFICATES");
                    final boolean useSSL = (boolean)payloadRow.get("USE_SSL");
                    final boolean syncContact = (boolean)payloadRow.get("SYNC_CONTACTS");
                    final boolean syncCalander = (boolean)payloadRow.get("SYNC_CALANDAR");
                    final boolean syncNotes = (boolean)payloadRow.get("SYNC_NOTES");
                    final boolean syncTasks = (boolean)payloadRow.get("SYNC_TASKS");
                    final int pastDaysToSync = (int)payloadRow.get("PAST_DAYS_MAIL_TO_SYNC");
                    certID = (Long)payloadRow.get("IDENTITY_CERT_ID");
                    final Integer pastDaysCalendarSync = (Integer)payloadRow.get("PAST_DAYS_TO_CALANDER_SYNC");
                    final Boolean allowMailSettingsChange = (Boolean)payloadRow.get("ALLOW_CHANGE_MAIL_SETTINGS");
                    final Boolean allowForwardMail = (Boolean)payloadRow.get("ALLOW_FORWARD_EMAIL");
                    final Boolean allowHTMLContent = (Boolean)payloadRow.get("ALLOW_HTML_CONTENT");
                    final Integer incomingAttachment = (Integer)payloadRow.get("INCOMING_ATTACHMENT_MAX_SIZE");
                    final Integer syncSchedule = (Integer)payloadRow.get("OFF_PEAK_DAYS_SYCN_FREQUENCY");
                    final Integer peakDays = (Integer)payloadRow.get("PEAK_DAYS");
                    final Integer peakStartAt = (Integer)payloadRow.get("PEAK_START_MINS");
                    final Integer peakEndAt = (Integer)payloadRow.get("PEAK_END_MINS");
                    final Integer peakSyncSchedule = (Integer)payloadRow.get("PEAK_DAYS_SYCN_FREQUENCY");
                    final Boolean isDefault = (Boolean)payloadRow.get("DEFAULT_ACCOUNT");
                    final String signature = (String)payloadRow.get("SIGNATURE");
                    final Boolean isNotify = (Boolean)payloadRow.get("ALLOW_NOTIFY");
                    final Boolean isViberate = (Boolean)payloadRow.get("ALLOW_ALWAYS_VIBRATE");
                    final Integer retrivalSize = (Integer)payloadRow.get("RETRIVAL_SIZE");
                    final Integer roamingSettings = (Integer)payloadRow.get("ROAMING_SETTINGS");
                    final Integer primaryClientPref = (Integer)payloadRow.get("PRIMARY_CLIENT_PREF");
                    final Integer secondaryClientPref = (Integer)payloadRow.get("SECONDARY_CLIENT_PREF");
                    activeSyncPayload = new AndroidActiveSyncPayload("1.0", "com.mdm.mobiledevice.activesync", "Exchange Active Sync Policy");
                    if (emailAddress != null && !emailAddress.equalsIgnoreCase("--") && !emailAddress.isEmpty()) {
                        activeSyncPayload.setEMailAddress(emailAddress);
                    }
                    if (userName != null && !userName.equalsIgnoreCase("--") && !userName.isEmpty()) {
                        activeSyncPayload.setUserName(userName);
                    }
                    if (domainName != null && !domainName.equalsIgnoreCase("--") && !domainName.isEmpty()) {
                        activeSyncPayload.setDomainName(domainName);
                    }
                    if (activeSyncHost != null && !activeSyncHost.equalsIgnoreCase("--") && !activeSyncHost.isEmpty()) {
                        activeSyncPayload.setHost(activeSyncHost);
                    }
                    String password = "";
                    if (payloadRow.get("PASSWORD_ID") != null) {
                        final Long passwordId = (Long)payloadRow.get("PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordId.toString());
                    }
                    if (password != null && !password.equalsIgnoreCase("--") && !password.isEmpty()) {
                        activeSyncPayload.setPassword(password);
                    }
                    activeSyncPayload.setAcceptAllCertificate(acceptAllCertificate);
                    activeSyncPayload.setUseSSL(useSSL);
                    activeSyncPayload.setSyncCalendar(syncCalander);
                    activeSyncPayload.setSyncContacts(syncContact);
                    activeSyncPayload.setSyncNotes(syncNotes);
                    activeSyncPayload.setSyncTasks(syncTasks);
                    activeSyncPayload.setMailNumberOfPastDaysToSync(pastDaysToSync);
                    activeSyncPayload.setDisplayName(dispName);
                    activeSyncPayload.setPastDaysToCalendarSync(pastDaysCalendarSync);
                    activeSyncPayload.allowMailSettingModify(allowMailSettingsChange);
                    activeSyncPayload.allowMailForward(allowForwardMail);
                    activeSyncPayload.allowHTMLContent(allowHTMLContent);
                    activeSyncPayload.setSyncSchedule(syncSchedule);
                    activeSyncPayload.setPeakDays(peakDays);
                    activeSyncPayload.setPeakDayStartAt(peakStartAt * 60);
                    activeSyncPayload.setPeakDayEndsAt(peakEndAt * 60);
                    activeSyncPayload.setPeakDaySyncSchedule(peakSyncSchedule);
                    activeSyncPayload.setAsDefaultAccount(isDefault);
                    if (signature != null && !signature.equals("")) {
                        activeSyncPayload.setSignature(signature);
                    }
                    activeSyncPayload.allowNotifyOnEmail(isNotify);
                    activeSyncPayload.setViberate(isNotify && isViberate);
                    final boolean allowIncomingAttachment = incomingAttachment != -1;
                    activeSyncPayload.allowIncomingAttachments(allowIncomingAttachment);
                    if (allowIncomingAttachment) {
                        activeSyncPayload.setIncomingAttachmentSize(incomingAttachment * 1024 * 1024);
                    }
                    activeSyncPayload.setRetrivalSize(retrivalSize);
                    activeSyncPayload.setMaxMailToSync(pastDaysToSync);
                    activeSyncPayload.setMaxCalendarToSync(pastDaysCalendarSync);
                    activeSyncPayload.setRoamingSettings(roamingSettings);
                    activeSyncPayload.setPrimaryClientPref(primaryClientPref);
                    activeSyncPayload.setSecondaryClientPref(secondaryClientPref);
                }
                if (certID != null && certID != -1L) {
                    Logger.getLogger(DO2AndroidActiveSyncPayload.class.getName()).log(Level.INFO, "DO2AndroidActiveSyncPayload: Identity Certificate Id: {0}", new Object[] { certID });
                    final Long customerID = (Long)dataObject.getRow("CollnToCustomerRel").get("CUSTOMER_ID");
                    final DataObject certDO = ProfileCertificateUtil.getInstance().getCertificateInfo(customerID, certID);
                    final Criteria identitycertCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certID, 0);
                    final DataObject certificatesDO = ProfileCertificateUtil.getCertificateDO(identitycertCriteria);
                    final Row identityRow = certificatesDO.getFirstRow("Certificates");
                    final int type = (int)identityRow.get("CERTIFICATE_TYPE");
                    Logger.getLogger(DO2AndroidActiveSyncPayload.class.getName()).log(Level.INFO, "DO2AndroidActiveSyncPayload: Identity Certificate type: {0}", new Object[] { type });
                    if (type == 0) {
                        activeSyncPayload.setEnrollType("Raw");
                    }
                    else if (type == 1) {
                        activeSyncPayload.setEnrollType("Scep");
                    }
                    if (certDO != null && !certDO.isEmpty() && type == 0) {
                        Logger.getLogger(DO2AndroidActiveSyncPayload.class.getName()).log(Level.INFO, "DO2AndroidActiveSyncPayload: Adding raw certificate");
                        final Row certRow = certDO.getFirstRow("CredentialCertificateInfo");
                        final Long certificateId = (Long)certRow.get("CERTIFICATE_ID");
                        final String password2 = PayloadSecretFieldsHandler.getInstance().constructPayloadCertificatePassword(certificateId.toString());
                        final String certificate = PayloadSecretFieldsHandler.getInstance().constructPayloadCertificate(certificateId.toString());
                        activeSyncPayload.setCertificate(certificate);
                        activeSyncPayload.setCertificatePassword(password2);
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DO2AndroidActiveSyncPayload.class.getName()).log(Level.SEVERE, "DO2AndroidActiveSyncPayload: Exception in createPayload", ex);
        }
        return activeSyncPayload;
    }
    
    byte[] certificateBytesArray(final String fileName) throws IOException {
        InputStream inputStream = null;
        byte[] bytes = null;
        try {
            inputStream = ApiFactoryProvider.getFileAccessAPI().getInputStream(fileName);
            if (inputStream != null) {
                bytes = IOUtils.toByteArray(inputStream);
                if (bytes.length > Integer.MAX_VALUE) {
                    throw new IOException("The file is too big");
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger(DO2AndroidActiveSyncPayload.class.getName()).log(Level.SEVERE, "Exception in certificateBytesArray", e);
        }
        finally {
            inputStream.close();
        }
        return bytes;
    }
}
