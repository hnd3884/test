package com.me.mdm.server.windows.profile.payload;

import org.json.JSONObject;
import java.util.List;

public class WindowsDesktopLockDownPayload extends WindowsLockDownPayload
{
    public WindowsDesktopLockDownPayload() {
        this.keyPrefix = "./Device/Vendor/MSFT/AssignedAccess/KioskModeApp";
    }
    
    public void setKioskPayload(final String domain, final String user, final List lockDownApps) throws Exception {
        final JSONObject kioskJSON = new JSONObject();
        if (domain != null && !domain.equals("")) {
            kioskJSON.put("Account", (Object)(domain + "\\" + user));
        }
        else {
            kioskJSON.put("Account", (Object)user);
        }
        kioskJSON.put("AUMID", (Object)lockDownApps.get(0));
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.keyPrefix, kioskJSON.toString(), "chr"));
    }
    
    @Override
    public void setRemovePayload() {
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(this.keyPrefix));
    }
}
