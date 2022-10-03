package com.adventnet.sym.server.mdm.safe.payload;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;

public class SafeEmailPolicyPayload extends AndroidPayload
{
    public SafeEmailPolicyPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Email", payloadIdentifier, payloadDisplayName);
    }
    
    public void setEMailAddress(final String emailAddress) throws JSONException {
        this.getPayloadJSON().put("EmailAddress", (Object)emailAddress);
    }
    
    public void setIncomingProtocol(final String incomintProtocol) throws JSONException {
        this.getPayloadJSON().put("InComingProtocol", (Object)incomintProtocol);
    }
    
    public void setIncomingServerHost(final String incomingHost) throws JSONException {
        this.getPayloadJSON().put("IncomingMailServerHostName", (Object)incomingHost);
    }
    
    public void setIncomingServerPort(final int incomingPort) throws JSONException {
        this.getPayloadJSON().put("IncomingMailServerPortNumber", incomingPort);
    }
    
    public void setIncomingServerUserName(final String incomingUserName) throws JSONException {
        this.getPayloadJSON().put("IncomingMailServerUsername", (Object)incomingUserName);
    }
    
    public void setIncomingServerPassword(final String incomingPassword) throws JSONException {
        this.getPayloadJSON().put("IncomingPassword", (Object)incomingPassword);
    }
    
    public void setIncomingServerUseSSL(final boolean useSSL) throws JSONException {
        this.getPayloadJSON().put("IncomingMailServerUseSSL", useSSL);
    }
    
    public void setIncomingServerAcceptAllCertificate(final boolean acceptCertificate) throws JSONException {
        this.getPayloadJSON().put("IncominMailServerAcceptCertificate", acceptCertificate);
    }
    
    public void setOutgoingProtocol(final String outgoingtProtocol) throws JSONException {
        this.getPayloadJSON().put("OutGoingProtocol", (Object)outgoingtProtocol);
    }
    
    public void setOutgoingServerHost(final String outgoingHost) throws JSONException {
        this.getPayloadJSON().put("OutgoingMailServerHostName", (Object)outgoingHost);
    }
    
    public void setOutgoingServerPort(final int outgoingPort) throws JSONException {
        this.getPayloadJSON().put("OutgoingMailServerPortNumber", outgoingPort);
    }
    
    public void setOutgoingServerUserName(final String outgoingUserName) throws JSONException {
        this.getPayloadJSON().put("OutgoingMailServerUsername", (Object)outgoingUserName);
    }
    
    public void setOutgoingServerPassword(final String outgoingPassword) throws JSONException {
        this.getPayloadJSON().put("OutgoingPassword", (Object)outgoingPassword);
    }
    
    public void setOutgoingServerUseSSL(final boolean useSSL) throws JSONException {
        this.getPayloadJSON().put("OutgoingMailServerUseSSL", useSSL);
    }
    
    public void setOutgoingServerAcceptAllCertificate(final boolean acceptCertificate) throws JSONException {
        this.getPayloadJSON().put("OugGoinMailServerAcceptCertificate", acceptCertificate);
    }
    
    public void setNotify(final boolean notify) throws JSONException {
        this.getPayloadJSON().put("IsNotify", notify);
    }
    
    public void setDefault(final boolean defaultAcc) throws JSONException {
        this.getPayloadJSON().put("IsDefault", defaultAcc);
    }
    
    public void setVibrateOnEmail(final boolean vibrate) throws JSONException {
        this.getPayloadJSON().put("VibrateOnEamil", vibrate);
    }
    
    public void setAllowForward(final boolean allowForward) throws JSONException {
        this.getPayloadJSON().put("AllowForward", allowForward);
    }
}
