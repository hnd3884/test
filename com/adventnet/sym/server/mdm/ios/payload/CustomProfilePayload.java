package com.adventnet.sym.server.mdm.ios.payload;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;
import com.dd.plist.NSDictionary;

public class CustomProfilePayload extends IOSPayload
{
    public CustomProfilePayload(final NSDictionary payloadDictionary) {
        final String payloadUUID = this.getPayloadUUID();
        final NSDictionary payloadDict = this.getPayloadDict();
        payloadDict.putAll((Map)payloadDictionary);
        payloadDict.put("PayloadVersion", (Object)1);
        payloadDict.put("PayloadUUID", (Object)payloadUUID);
        payloadDict.put("PayloadOrganization", (Object)"MDM");
        payloadDict.put("PayloadIdentifier", (Object)payloadUUID);
    }
    
    public void changePayloadUUID(final HashMap payloadUUIDs) {
        try {
            final String payloadString = this.getPayloadDict().toXMLPropertyList();
            for (final String oldUUID : payloadUUIDs.keySet()) {
                final String payloadUUID = payloadUUIDs.get(oldUUID);
                final String modifiedPayloadString = Pattern.compile(oldUUID, 2).matcher(payloadString).replaceAll(payloadUUID);
                this.getPayloadDict().putAll((Map)DMSecurityUtil.parsePropertyList(modifiedPayloadString.getBytes("UTF-8")));
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in change payloadUUID", e);
        }
    }
    
    public String getPayloadUUIDFromPayloadDict() {
        return String.valueOf(this.getPayloadDict().get((Object)"PayloadUUID"));
    }
}
