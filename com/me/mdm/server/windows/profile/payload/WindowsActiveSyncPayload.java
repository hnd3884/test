package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.data.Item;

public class WindowsActiveSyncPayload extends WindowsPayload
{
    public WindowsActiveSyncPayload() {
    }
    
    public WindowsActiveSyncPayload(final String commandUUID) {
    }
    
    public void setGuid(final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, "", "node"));
    }
    
    public void setEmailAddress(final String emailAddress, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "EmailAddress";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, emailAddress));
    }
    
    public void setDomain(final String domainName, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Domain";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, domainName));
    }
    
    public void setAccountIcon(final String accountIconPath, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "AccountIcon";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, accountIconPath));
    }
    
    public void setAccountType(final String accountType, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "AccountType";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, accountType));
    }
    
    public void setAccountName(final String accountName, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "AccountName";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, accountName));
    }
    
    public void setPassword(final String password, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Password";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, password));
    }
    
    public void setServerName(final String serverName, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "ServerName";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, serverName));
    }
    
    public void setUserName(final String userName, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "UserName";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, userName));
    }
    
    public void setUseSSL(final String useSSL, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Options/UseSSL";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, useSSL));
    }
    
    public void setSchedule(final Integer schedule, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Options/Schedule";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, schedule.toString()));
    }
    
    public void setMailAgeFilter(final Integer mailAgeFileter, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Options/MailAgeFilter";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, mailAgeFileter.toString()));
    }
    
    public void setLogging(final Integer logging, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Options/Logging";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, logging.toString()));
    }
    
    public void setEmailContentType(final String emailContentType, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Options/ContentTypes/%7Bc6d47067-6e92-480e-b0fc-4ba82182fac7%7D/Enabled";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, emailContentType));
    }
    
    public void setContactsContentType(final String contactsContentType, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Options/ContentTypes/%7B0dd8685c-e272-4fcb-9ecf-2ead7ea2497b%7D/Enabled";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, contactsContentType));
    }
    
    public void setCalendarContentType(final String calendarContentType, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Options/ContentTypes/%7B4a5d9fe0-f139-4a63-a5a4-4f31ceea02ad%7D/Enabled";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, calendarContentType));
    }
    
    public void setTaskContentType(final String taskContentType, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Options/ContentTypes/%7B783ae4f6-4c12-4423-8270-66361260d4f1%7D/Enabled";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, taskContentType));
    }
    
    public void setEmailContentTypeName(final String emailContentTypeName, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Options/ContentTypes/%7Bc6d47067-6e92-480e-b0fc-4ba82182fac7%7D/Name";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, emailContentTypeName));
    }
    
    public void setContactsContentTypeName(final String contactsContentTypeName, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Options/ContentTypes/%7B0dd8685c-e272-4fcb-9ecf-2ead7ea2497b%7D/Name";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, contactsContentTypeName));
    }
    
    public void setCalendarContentTypeName(final String calendarContentTypeName, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Options/ContentTypes/%7B4a5d9fe0-f139-4a63-a5a4-4f31ceea02ad%7D/Name";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, calendarContentTypeName));
    }
    
    public void setTaskContentTypeName(final String taskContentTypeName, final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D/" + "Options/ContentTypes/%7B783ae4f6-4c12-4423-8270-66361260d4f1%7D/Name";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, taskContentTypeName));
    }
    
    public void setRemoveProfilePayload(final String sGuid) {
        final String locUri = "./Vendor/MSFT/ActiveSync/Accounts/%7B" + sGuid + "%7D";
        final Item item = this.createTargetItemTagElement(locUri);
        this.getDeletePayloadCommand().addRequestItem(item);
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificInstallPayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getReplacePayloadCommand());
        return winConfigPayload;
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificRemovePayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getDeletePayloadCommand());
        return winConfigPayload;
    }
}
