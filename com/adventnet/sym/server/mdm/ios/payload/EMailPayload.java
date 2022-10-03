package com.adventnet.sym.server.mdm.ios.payload;

public class EMailPayload extends IOSPayload
{
    public EMailPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.mail.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setAccountDescription(final String accountDescription) {
        this.getPayloadDict().put("EmailAccountDescription", (Object)accountDescription);
    }
    
    public void setAccountType(final String accountType) {
        this.getPayloadDict().put("EmailAccountType", (Object)accountType);
    }
    
    public void setAccountDisplayName(final String displayName) {
        this.getPayloadDict().put("EmailAccountName", (Object)displayName);
    }
    
    public void setEMailAddress(final String emailAddress) {
        this.getPayloadDict().put("EmailAddress", (Object)emailAddress);
    }
    
    public void setIncomingMailServerAuthentication(final String incomingAuth) {
        this.getPayloadDict().put("IncomingMailServerAuthentication", (Object)incomingAuth);
    }
    
    public void setIncomingMailServerHostName(final String incomingHostName) {
        this.getPayloadDict().put("IncomingMailServerHostName", (Object)incomingHostName);
    }
    
    public void setIncomingMailServerIMAPPathPrefix(final String incomingMailServerIMAPPathPrefix) {
        this.getPayloadDict().put("IncomingMailServerIMAPPathPrefix", (Object)incomingMailServerIMAPPathPrefix);
    }
    
    public void setIncomingMailServerPortNumber(final int portNumber) {
        this.getPayloadDict().put("IncomingMailServerPortNumber", (Object)portNumber);
    }
    
    public void setIncomingMailServerUseSSL(final boolean isSSL) {
        this.getPayloadDict().put("IncomingMailServerUseSSL", (Object)isSSL);
    }
    
    public void setIncomingMailServerUsername(final String userName) {
        this.getPayloadDict().put("IncomingMailServerUsername", (Object)userName);
    }
    
    public void setIncomingPassword(final String password) {
        this.getPayloadDict().put("IncomingPassword", (Object)password);
    }
    
    public void setOutgoingMailServerAuthentication(final String isAuthEnabled) {
        this.getPayloadDict().put("OutgoingMailServerAuthentication", (Object)"EmailAuthPassword");
    }
    
    public void setOutgoingMailServerHostName(final String serverHostName) {
        this.getPayloadDict().put("OutgoingMailServerHostName", (Object)serverHostName);
    }
    
    public void setOutgoingMailServerPortNumber(final int portNumber) {
        this.getPayloadDict().put("OutgoingMailServerPortNumber", (Object)portNumber);
    }
    
    public void setOutgoingMailServerUseSSL(final boolean isSSL) {
        this.getPayloadDict().put("OutgoingMailServerUseSSL", (Object)isSSL);
    }
    
    public void setOutgoingMailServerUsername(final String userName) {
        this.getPayloadDict().put("OutgoingMailServerUsername", (Object)userName);
    }
    
    public void setOutgoingPasswordSameAsIncomingPassword(final boolean useIncomingPwd) {
        this.getPayloadDict().put("OutgoingPasswordSameAsIncomingPassword", (Object)useIncomingPwd);
    }
    
    public void setOutgoingPassword(final String outGoingPwd) {
        this.getPayloadDict().put("OutgoingPassword", (Object)outGoingPwd);
    }
    
    public void setPreventMove(final boolean allowMove) {
        this.getPayloadDict().put("PreventMove", (Object)allowMove);
    }
    
    public void setDisableMailSync(final boolean disableMailSync) {
        this.getPayloadDict().put("disableMailRecentsSyncing", (Object)disableMailSync);
    }
    
    public void setUseOnlyMailApp(final boolean useOnlyMailApp) {
        this.getPayloadDict().put("PreventAppSheet", (Object)useOnlyMailApp);
    }
    
    public void setSMIMEEnabled(final boolean isSMIMEEnabled) {
        this.getPayloadDict().put("SMIMEEnabled", (Object)isSMIMEEnabled);
    }
    
    public void setSMIMEEncryptionCertificateUUID(final String sMIMEEncryptionCertificateUUID) {
        this.getPayloadDict().put("SMIMEEncryptionCertificateUUID", (Object)sMIMEEncryptionCertificateUUID);
    }
    
    public void setSMIMESigningCertificateUUID(final String sMIMESigningCertificateUUID) {
        this.getPayloadDict().put("SMIMESigningCertificateUUID", (Object)sMIMESigningCertificateUUID);
    }
}
