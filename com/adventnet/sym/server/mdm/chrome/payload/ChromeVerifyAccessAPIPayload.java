package com.adventnet.sym.server.mdm.chrome.payload;

import java.util.List;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONException;

public class ChromeVerifyAccessAPIPayload extends ChromePayload
{
    public ChromeVerifyAccessAPIPayload() throws JSONException {
    }
    
    public ChromeVerifyAccessAPIPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "VerifiedAccess", payloadIdentifier, payloadDisplayName);
    }
    
    public void setVerifyAccessMode(final Boolean verifyAccessMode) throws JSONException {
        this.getPayloadJSON().put("IsVerifiedModeEnabled", (Object)verifyAccessMode);
    }
    
    public void setDeviceAttestation(final Boolean accessForContent, final Boolean accessForExtenstion) throws JSONException {
        this.getPayloadJSON().put("IsAttestationEnabled", (Object)accessForExtenstion);
        this.getPayloadJSON().put("IsAttestationEnabledForContentProtection", (Object)accessForContent);
    }
    
    public void setAccessControl(final String serviceAccouts, final String serviceAccountsWithData) throws JSONException {
        final List fullAccessAccList = MDMUtil.getInstance().getStringList(serviceAccountsWithData, ",");
        final List limitedAccessAccList = MDMUtil.getInstance().getStringList(serviceAccouts, ",");
        this.getPayloadJSON().put("AccountsWithFullControl", (Collection)fullAccessAccList);
        this.getPayloadJSON().put("AccountsWithLimitedControl", (Collection)limitedAccessAccList);
    }
    
    public void setUserAttestation(final Boolean accessForExtenstion) throws JSONException {
        this.getPayloadJSON().put("IsAttestationEnabled", (Object)accessForExtenstion);
    }
}
