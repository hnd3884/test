package com.adventnet.sym.server.mdm.safe.payload;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.android.payload.AndroidPasscodePolicyPayload;

public class SafePasscodePolicyPayload extends AndroidPasscodePolicyPayload
{
    public SafePasscodePolicyPayload() {
    }
    
    public SafePasscodePolicyPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, payloadIdentifier, payloadDisplayName);
    }
    
    @Override
    public void setMaximumCharacters(final int value) throws JSONException {
        this.getPayloadJSON().put("maxCharCanOccur", value);
    }
    
    @Override
    public void setMaximumNumericSequence(final int value) throws JSONException {
        this.getPayloadJSON().put("maxNumSeq", value);
    }
    
    @Override
    public void setMaximumGracePeriod(final int value) throws JSONException {
        this.getPayloadJSON().put("passcodeChangeTimeout", value);
    }
}
