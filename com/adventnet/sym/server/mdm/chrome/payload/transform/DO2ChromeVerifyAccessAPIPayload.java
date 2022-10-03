package com.adventnet.sym.server.mdm.chrome.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeVerifyAccessAPIPayload;
import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromeVerifyAccessAPIPayload implements DO2ChromePayload
{
    @Override
    public ChromePayload createPayload(final DataObject dataObject) {
        throw new RuntimeException("Need scope for Verify access payload");
    }
    
    public ChromeVerifyAccessAPIPayload createPayload(final DataObject dataObject, final int scope) {
        ChromeVerifyAccessAPIPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("VerifyAccessAPIConfig");
            while (iterator.hasNext()) {
                payload = new ChromeVerifyAccessAPIPayload("1.0", "VerifiedAccess", "VerifiedAccess");
                final Row row = iterator.next();
                final Boolean verifyAccessMode = (Boolean)row.get("VERIFIED_ACCESS_MODE");
                final Boolean accessForContent = (Boolean)row.get("VERIFIED_ACCESS_FOR_CONTENT");
                final Boolean accessForExtenstion = (Boolean)row.get("VERIFIED_ACCESS_FOR_EXTENSIONS");
                final String serviceAccouts = (String)row.get("ALLOWED_SERVICE_ACCOUNTS");
                final String serviceAccountsWithData = (String)row.get("ALLOWED_SERVICE_ACCOUNTS_WITH_DATA");
                if (scope == 0) {
                    payload.setDeviceAttestation(accessForContent, accessForExtenstion);
                }
                else {
                    payload.setUserAttestation(accessForExtenstion);
                }
                payload.setVerifyAccessMode(verifyAccessMode);
                payload.setAccessControl(serviceAccouts, serviceAccountsWithData);
            }
        }
        catch (final Exception ex) {
            DO2ChromeVerifyAccessAPIPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return payload;
    }
}
