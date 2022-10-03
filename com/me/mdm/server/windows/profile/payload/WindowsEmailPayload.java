package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.data.Item;

public class WindowsEmailPayload extends WindowsPayload
{
    public WindowsEmailPayload() {
    }
    
    public WindowsEmailPayload(final String commandUUID) {
    }
    
    public void setGuid(final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, "", "node"));
    }
    
    public void setAccountIcon(final String accountIconPath, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "ACCOUNTICON";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, accountIconPath, "chr"));
    }
    
    public void setAccountType(final String accountType, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "ACCOUNTTYPE";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, accountType, "chr"));
    }
    
    public void setAuthName(final String authName, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "AUTHNAME";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, authName, "chr"));
    }
    
    public void setAuthReq(final Integer authReq, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "AUTHREQUIRED";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, authReq.toString(), "int"));
    }
    
    public void setAuthSecret(final String authSecret, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "AUTHSECRET";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, authSecret, "chr"));
    }
    
    public void setDomain(final String domain, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "DOMAIN";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, domain, "chr"));
    }
    
    public void setDownloadDay(final Integer downloadDay, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "DWNDAY";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, downloadDay.toString(), "chr"));
    }
    
    public void setInServer(final String inServer, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "INSERVER";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, inServer, "chr"));
    }
    
    public void setLinger(final Integer linger, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "LINGER";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, linger.toString(), "int"));
    }
    
    public void setSenderEmailName(final String senderEmailName, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "NAME";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, senderEmailName, "chr"));
    }
    
    public void setOutServer(final String outServer, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "OUTSERVER";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, outServer, "chr"));
    }
    
    public void setReplyAddress(final String replyAddress, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "REPLYADDR";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, replyAddress, "chr"));
    }
    
    public void setServiceName(final String serviceName, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "SERVICENAME";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, serviceName, "chr"));
    }
    
    public void setServiceType(final String serviceType, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "SERVICETYPE";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, serviceType.toString(), "chr"));
    }
    
    public void setSmtpAltAuthName(final String smtpAltAuthName, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "SMTPALTAUTHNAME";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, smtpAltAuthName, "chr"));
    }
    
    public void setSmtpAltDomain(final String smtpAltDomain, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "SMTPALTDOMAIN";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, smtpAltDomain, "chr"));
    }
    
    public void setSmtpAltEnabled(final String smtpAltEnabled, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "SMTPALTENABLED";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, smtpAltEnabled, "int"));
    }
    
    public void setSmtpAltPassword(final String smtpAltPassword, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "SMTPALTPASSWORD";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, smtpAltPassword, "chr"));
    }
    
    public void setTagProps(final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "TAGPROPS";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, "node"));
    }
    
    public void setInComingServerUseSSL(final String inComingServerUseSSL, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "TAGPROPS/8128000B";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, inComingServerUseSSL));
    }
    
    public void setOutGoingServerUseSSL(final String outGoingServerUseSSL, final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D/" + "TAGPROPS/812C000B";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(locUri, outGoingServerUseSSL));
    }
    
    public void setRemoveProfilePayload(final String sGuid) {
        final String locUri = "./Vendor/MSFT/EMAIL2/%7B" + sGuid + "%7D";
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
