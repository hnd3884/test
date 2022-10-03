package com.adventnet.sym.server.mdm.android.payload;

import java.util.Collection;
import java.util.List;
import org.json.JSONException;

public class AndroidWorkDataSecurityPayload extends AndroidPayload
{
    public AndroidWorkDataSecurityPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "WorkDataSecurity", payloadIdentifier, payloadDisplayName);
    }
    
    public void setAllowShareDocToPersonalApps(final boolean value) throws JSONException {
        this.getPayloadJSON().put("ShareDocsToPersonalApps", value);
    }
    
    public void setAllowShareDocToWorkProfile(final boolean value) throws JSONException {
        this.getPayloadJSON().put("ShareDocsToWorkProfile", value);
    }
    
    public void setAllowWorkProfileContentToOtherApps(final boolean value) throws JSONException {
        this.getPayloadJSON().put("AllowProfileContentsToOtherApps", value);
    }
    
    public void setAllowShareWorkProfileContactOverBluetooth(final boolean value) throws JSONException {
        this.getPayloadJSON().put("ShareWorkProfileContactOverBluetooth", value);
    }
    
    public void setAllowWorkProfileAppWidgetToHomeScreen(final boolean value) throws JSONException {
        this.getPayloadJSON().put("AllowWorkProfileAppWidgetToHomeScreen", value);
    }
    
    public void setAllowWorkContactDetailsInPersonalProfile(final boolean value) throws JSONException {
        this.getPayloadJSON().put("AllowWorkContactDetailsInPersonalProfile", value);
    }
    
    public void setAllowWorkContactAccessToPersonalApps(final boolean value) throws JSONException {
        this.getPayloadJSON().put("AllowWorkContactAccessToPersonalApps", value);
    }
    
    public void setAllowConnectedApps(final Integer value) throws JSONException {
        this.getPayloadJSON().put("ConnectedApps", (Object)value);
    }
    
    public void setConnectedApps(final List list) throws JSONException {
        this.getPayloadJSON().put("ConnectedAppsList", (Collection)list);
    }
}
