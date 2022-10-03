package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.payload.RestrictionsPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2SharedDeviceConfigurationPayload implements DO2Payload
{
    public static final Integer SHARED_DEVICE_ABM_USER;
    public static final Integer SHARED_DEVICE_GUEST_USER;
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final IOSPayload[] payloads = { null };
        try {
            final Row sharedDeviceRow = dataObject.getRow("SharedDeviceConfiguration");
            final Integer userAllowed = (Integer)sharedDeviceRow.get("USER_ALLOWED");
            final RestrictionsPayload sharedDevicePayload = new RestrictionsPayload(1, "MDM", "com.mdm.mobiledevice.shareddevice", "Shared Device Policy");
            if (userAllowed == 2) {
                sharedDevicePayload.setSharedIpadGuestAccount(false);
            }
            else {
                sharedDevicePayload.setSharedIpadGuestAccount(true);
            }
            payloads[0] = sharedDevicePayload;
        }
        catch (final DataAccessException e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in Shared device payload", (Throwable)e);
        }
        return payloads;
    }
    
    static {
        SHARED_DEVICE_ABM_USER = 1;
        SHARED_DEVICE_GUEST_USER = 2;
    }
}
