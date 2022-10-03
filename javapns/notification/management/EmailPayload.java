package javapns.notification.management;

import org.json.JSONException;
import org.json.JSONObject;

class EmailPayload extends MobileConfigPayload
{
    public EmailPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName, final String emailAccountType, final String emailAddress, final String incomingMailServerAuthentication, final String incomingMailServerHostName, final String incomingMailServerUsername, final String outgoingMailServerAuthentication, final String outgoingMailServerHostName, final String outgoingMailServerUsername) throws JSONException {
        super(payloadVersion, "com.apple.mail.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
        final JSONObject payload = this.getPayload();
        payload.put("EmailAccountType", (Object)emailAccountType);
        payload.put("EmailAddress", (Object)emailAddress);
        payload.put("IncomingMailServerAuthentication", (Object)incomingMailServerAuthentication);
        payload.put("IncomingMailServerHostName", (Object)incomingMailServerHostName);
        payload.put("IncomingMailServerUsername", (Object)incomingMailServerUsername);
        payload.put("OutgoingMailServerAuthentication", (Object)outgoingMailServerAuthentication);
        payload.put("OutgoingMailServerHostName", (Object)outgoingMailServerHostName);
        payload.put("OutgoingMailServerUsername", (Object)outgoingMailServerUsername);
    }
    
    public void setEmailAccountDescription(final String value) throws JSONException {
        this.getPayload().put("EmailAccountDescription", (Object)value);
    }
    
    public void setEmailAccountName(final String value) throws JSONException {
        this.getPayload().put("EmailAccountName", (Object)value);
    }
    
    public void setIncomingMailServerPortNumber(final int value) throws JSONException {
        this.getPayload().put("IncomingMailServerPortNumber", value);
    }
    
    public void setIncomingMailServerUseSSL(final boolean value) throws JSONException {
        this.getPayload().put("IncomingMailServerUseSSL", value);
    }
    
    public void setIncomingPassword(final String value) throws JSONException {
        this.getPayload().put("IncomingPassword", (Object)value);
    }
    
    public void setOutgoingPassword(final String value) throws JSONException {
        this.getPayload().put("OutgoingPassword", (Object)value);
    }
    
    public void setOutgoingPasswwordSameAsIncomingPassword(final boolean value) throws JSONException {
        this.getPayload().put("OutgoingPasswwordSameAsIncomingPassword", value);
    }
    
    public void setOutgoingMailServerPortNumber(final int value) throws JSONException {
        this.getPayload().put("OutgoingMailServerPortNumber", value);
    }
    
    public void setOutgoingMailServerUseSSL(final boolean value) throws JSONException {
        this.getPayload().put("OutgoingMailServerUseSSL", value);
    }
}
