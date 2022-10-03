package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.ExchangeActiveSyncPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2ExchangeActiveSyncPayload implements DO2Payload
{
    private static final Logger LOGGER;
    private static final int RESTRICT_SYNC_MODIFICATION = 1;
    private static final int DISABLE_SYNC = 2;
    private static final int DISABLE_SYNC_AND_RESTRICT = 3;
    private static final int ENABLE_SYNC = 4;
    private static final int ENABLE_SYNC_AND_RESTRICT = 5;
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        ExchangeActiveSyncPayload syncPayload = null;
        final ExchangeActiveSyncPayload[] payloadArray = { null };
        try {
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows("ExchangeActiveSyncPolicy");
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String accountName = (String)payloadRow.get("ACCOUNT_NAME");
                    final String activeSyncHost = (String)payloadRow.get("ACTIVE_SYNC_HOST");
                    final Boolean preventMove = (Boolean)payloadRow.get("PREVENT_MOVE");
                    final boolean disableMailSync = (boolean)payloadRow.get("DISABLE_MAIL_RECENTS_SYNCING");
                    final String domainName = (String)payloadRow.get("DOMAIN");
                    final String emailAddress = (String)payloadRow.get("EMAIL_ADDRESS");
                    final String encryptCertUUID = String.valueOf(payloadRow.get("ENCRYPTION_CERT_ID"));
                    final String identityCertificate = String.valueOf(payloadRow.get("IDENTITY_CERT_ID"));
                    String password = "";
                    final Integer daysToSync = (Integer)payloadRow.get("PAST_DAYS_MAIL_TO_SYNC");
                    final String signingCertUUID = String.valueOf(payloadRow.get("SIGNING_CERT_ID"));
                    String userName = (String)payloadRow.get("USER_NAME");
                    final Boolean useMimeEncrypt = (Boolean)payloadRow.get("USE_MIME_ENCRYPT");
                    final Boolean useOnlyMailApp = (Boolean)payloadRow.get("USE_ONLY_MAIL_APP");
                    final Boolean useSSL = (Boolean)payloadRow.get("USE_SSL");
                    final Boolean oAuthEnabled = (Boolean)payloadRow.get("OAUTH");
                    final String OAuthSignInURL = (String)payloadRow.get("OAUTH_SIGNIN_URL");
                    final String OAuthTokenRequestUrl = (String)payloadRow.get("OAUTH_TOKEN_REQUEST_URL");
                    final Integer calenderSync = (Integer)payloadRow.get("SYNC_CALENDER");
                    final Integer contactSync = (Integer)payloadRow.get("SYNC_CONTACTS");
                    final Integer noteSync = (Integer)payloadRow.get("SYNC_NOTES");
                    final Integer mailSync = (Integer)payloadRow.get("SYNC_MAIL");
                    final Integer reminderSync = (Integer)payloadRow.get("SYNC_REMINDER");
                    syncPayload = new ExchangeActiveSyncPayload(1, "MDM", "com.mdm.mobiledevice.activesync", "Exchange Active Sync Client Configuration");
                    if (payloadRow.get("PASSWORD_ID") != null) {
                        final Long passwordID = (Long)payloadRow.get("PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID.toString());
                    }
                    if (accountName != null && !accountName.equalsIgnoreCase("--") && !accountName.isEmpty()) {
                        syncPayload.setAccountDisplayName(accountName);
                    }
                    if (activeSyncHost != null && !activeSyncHost.equalsIgnoreCase("--") && !activeSyncHost.isEmpty()) {
                        syncPayload.setExchangeServerHost(activeSyncHost.trim());
                    }
                    syncPayload.setPreventMove(preventMove);
                    syncPayload.setDisableMailSync(disableMailSync);
                    if (emailAddress != null && !emailAddress.equalsIgnoreCase("--") && !emailAddress.isEmpty()) {
                        syncPayload.setEMailAddress(emailAddress.trim());
                    }
                    if (password != null && !password.equalsIgnoreCase("--") && !password.isEmpty()) {
                        syncPayload.setPassword(password);
                    }
                    syncPayload.setMailNumberOfPastDaysToSync(daysToSync);
                    if (userName != null && !userName.equalsIgnoreCase("--") && !userName.isEmpty()) {
                        if (domainName != null && !domainName.equalsIgnoreCase("--") && !domainName.isEmpty() && activeSyncHost != null && !activeSyncHost.trim().equals("outlook.office365.com")) {
                            userName = domainName + "\\" + userName;
                        }
                        syncPayload.setUserName(userName);
                    }
                    syncPayload.useSSL(useSSL);
                    syncPayload.setPreventAppSheet(useOnlyMailApp);
                    if (oAuthEnabled) {
                        syncPayload.setOauth(oAuthEnabled);
                        if (!MDMStringUtils.isEmpty(OAuthSignInURL)) {
                            syncPayload.setOAuthSignInUrl(OAuthSignInURL);
                        }
                        if (!MDMStringUtils.isEmpty(OAuthTokenRequestUrl)) {
                            syncPayload.setOAuthTokerequestUrl(OAuthTokenRequestUrl);
                        }
                    }
                    final ExchangeSyncStateObject calenderStateObject = new ExchangeSyncStateObject();
                    final ExchangeSyncStateObject contactStateObject = new ExchangeSyncStateObject();
                    final ExchangeSyncStateObject mailStateObject = new ExchangeSyncStateObject();
                    final ExchangeSyncStateObject noteStateObject = new ExchangeSyncStateObject();
                    final ExchangeSyncStateObject reminderStateObject = new ExchangeSyncStateObject();
                    this.convertSyncState(calenderSync, calenderStateObject);
                    this.convertSyncState(contactSync, contactStateObject);
                    this.convertSyncState(mailSync, mailStateObject);
                    this.convertSyncState(noteSync, noteStateObject);
                    this.convertSyncState(reminderSync, reminderStateObject);
                    if (calenderStateObject.syncState != null) {
                        syncPayload.setEnableCalender(calenderStateObject.syncState);
                    }
                    if (contactStateObject.syncState != null) {
                        syncPayload.setEnableContacts(contactStateObject.syncState);
                    }
                    if (mailStateObject.syncState != null) {
                        syncPayload.setEnableMail(mailStateObject.syncState);
                    }
                    if (noteStateObject.syncState != null) {
                        syncPayload.setEnableNotes(noteStateObject.syncState);
                    }
                    if (reminderStateObject.syncState != null) {
                        syncPayload.setEnableReminder(reminderStateObject.syncState);
                    }
                    if (calenderStateObject.restrictionState != null) {
                        syncPayload.setRestrictCalender(calenderStateObject.restrictionState);
                    }
                    if (contactStateObject.restrictionState != null) {
                        syncPayload.setRestrictContacts(contactStateObject.restrictionState);
                    }
                    if (mailStateObject.restrictionState != null) {
                        syncPayload.setRestrictMail(mailStateObject.restrictionState);
                    }
                    if (noteStateObject.restrictionState != null) {
                        syncPayload.setRestrictNotes(noteStateObject.restrictionState);
                    }
                    if (reminderStateObject.restrictionState != null) {
                        syncPayload.setRestrictReminder(reminderStateObject.restrictionState);
                    }
                }
            }
        }
        catch (final Exception exp) {
            DO2ExchangeActiveSyncPayload.LOGGER.log(Level.SEVERE, "Exception in exchange active sync payload", exp);
        }
        payloadArray[0] = syncPayload;
        return payloadArray;
    }
    
    private void convertSyncState(final Integer state, final ExchangeSyncStateObject exchangeObject) {
        switch (state) {
            case 1: {
                exchangeObject.restrictionState = false;
                break;
            }
            case 2: {
                exchangeObject.syncState = false;
                break;
            }
            case 3: {
                exchangeObject.restrictionState = false;
                exchangeObject.syncState = false;
                break;
            }
            case 4: {
                exchangeObject.syncState = true;
                break;
            }
            case 5: {
                exchangeObject.restrictionState = false;
                exchangeObject.syncState = true;
                break;
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
    
    public class ExchangeSyncStateObject
    {
        Boolean syncState;
        Boolean restrictionState;
        
        public ExchangeSyncStateObject() {
            this.syncState = null;
            this.restrictionState = null;
        }
    }
}
